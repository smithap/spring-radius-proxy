package com.bt.wifi.radiusproxy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusServer;


@Component
public class MultiThreadedServer extends RadiusServer implements InitializingBean {
	private static final Log LOG = LogFactory.getLog(MultiThreadedServer.class);
	
	@Resource(name = "clientMap")
	private Map<String, String> clientMap;

	@Resource
	private BlockingQueue<QueueItem> queue;

	public MultiThreadedServer() {
	}

	@Override
	public String getSharedSecret(InetSocketAddress client) {
		LOG.debug(String.format("getSharedSecret(%s)", client.getHostName()));
		//TODO: use ip addresses as well as or instead of hostnames
		return clientMap.get(client.getHostName());
	}

	@Override
	public String getUserPassword(String userName) {
		return null; // not used here, see QueueManager
	}

	@Override
	protected void listen(DatagramSocket s) {
		while (true) {
			DatagramPacket packetIn = new DatagramPacket(new byte[RadiusPacket.MAX_PACKET_LENGTH], RadiusPacket.MAX_PACKET_LENGTH);
			try {
				// receive packet
				try {
					LOG.trace("about to call socket.receive()");
					s.receive(packetIn);
					if (LOG.isDebugEnabled())
						LOG.debug("receive buffer size = " + s.getReceiveBufferSize());
				} catch (SocketException se) {
					if (closing) {
						// end thread
						LOG.info("got closing signal - end listen thread");
						return;
					}
					// retry s.receive()
					LOG.error("SocketException during s.receive() -> retry", se);
					continue;
				}

				InetSocketAddress localAddress = (InetSocketAddress) s.getLocalSocketAddress();
				InetSocketAddress remoteAddress = new InetSocketAddress(packetIn.getAddress(), packetIn.getPort());
				String secret = getSharedSecret(remoteAddress);
				if (secret == null) {
					if (LOG.isInfoEnabled())
						LOG.info("ignoring packet from unknown client " + remoteAddress + " received on local address " + localAddress);
					return;
				}

				if (!queue.offer(new QueueItem(s, packetIn, secret)))
					LOG.warn("queue overflowing");

			} catch (SocketTimeoutException ste) {
				// this is expected behaviour
				LOG.trace("normal socket timeout");
			} catch (IOException ioe) {
				// error while reading/writing socket
				LOG.error("communication error", ioe);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.start(true, true);
	}
}