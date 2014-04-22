package com.bt.wifi.radiusproxy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusException;
import org.tinyradius.util.RadiusServer;

@Component
public class QueueManager extends RadiusServer implements InitializingBean {
	private static final Log LOG = LogFactory.getLog(QueueManager.class);

	@Resource
	private BlockingQueue<QueueItem> queue;

	@Resource
	private ReceivedPacketCache receivedPacketCache;

	@Resource
	private HandlerManager handlerManager;

	public QueueManager() {
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		async();
	}

	@Override
	public String getSharedSecret(InetSocketAddress client) {
		return null; // not used here
	}

	@Override
	public String getUserPassword(String userName) {
		return null; // not used here
	}

	@Async
	public void async() {
		LOG.debug("async");
		while (true) {
			try {
				QueueItem queueItem = queue.take();
				run(queueItem);
			} catch (Throwable t) {
				LOG.error("Error in main thread", t);
			}
		}
	}

	@Async
	public void run(QueueItem queueItem) {
		LOG.debug(String.format("run(%s)", queueItem));
		try {
			// check client
			InetSocketAddress localAddress = (InetSocketAddress) queueItem.getDatagramSocket().getLocalSocketAddress();
			InetSocketAddress remoteAddress = new InetSocketAddress(queueItem.getDatagramPacket().getAddress(), queueItem.getDatagramPacket().getPort());

			// parse packet
			RadiusPacket request = makeRadiusPacket(queueItem.getDatagramPacket(), queueItem.getSecret());
			if (LOG.isInfoEnabled())
				LOG.info("received packet from " + remoteAddress + " on local address " + localAddress + ": " + request);

			// handle packet
			LOG.trace("about to call RadiusServer.handlePacket()");
			RadiusPacket response = handlePacket(localAddress, remoteAddress, request, queueItem.getSecret());

			// send response
			if (response != null) {
				if (LOG.isInfoEnabled())
					LOG.info("send response: " + response);
				DatagramPacket packetOut = makeDatagramPacket(response, queueItem.getSecret(), remoteAddress.getAddress(), queueItem.getDatagramPacket().getPort(), request);
				queueItem.getDatagramSocket().send(packetOut);
			} else
				LOG.info("no response sent");
		} catch (IOException ioe) {
			// error while reading/writing socket
			LOG.error("communication error", ioe);
		} catch (RadiusException re) {
			// malformed packet
			LOG.error("malformed Radius packet", re);
		}
	}

	@Override
	public RadiusPacket accessRequestReceived(AccessRequest accessRequest, InetSocketAddress client) throws RadiusException {
		LOG.debug(String.format("accessRequestReceived(%s, %s)", accessRequest, client));
		return handlerManager.accessRequestReceived(accessRequest, client);
	}

	protected boolean isPacketDuplicate(RadiusPacket packet, InetSocketAddress address) {
		LOG.debug(String.format("isPacketDuplicate(%s, %s)", packet, address));

		ReceivedPacket receivedPacket = receivedPacketCache.get(packet.getPacketIdentifier(), address, packet.getAuthenticator());
		if (null != receivedPacket) {
			LOG.debug(String.format("dropping duplicate packet: %s", receivedPacket));
			return true;
		}

		receivedPacketCache.put(packet.getPacketIdentifier(), address, packet.getAuthenticator());
		return false;
	}
}