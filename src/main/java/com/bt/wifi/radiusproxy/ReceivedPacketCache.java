package com.bt.wifi.radiusproxy;

import java.net.InetSocketAddress;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class ReceivedPacketCache {
	private static final Log LOG = LogFactory.getLog(ReceivedPacketCache.class);

	@Cacheable(value="receivedPacket", key="#address.hashCode() + '-' + new String(#authenticator).hashCode() + '-' + #packetIdentifier")
	public ReceivedPacket get(int packetIdentifier, InetSocketAddress address, byte[] authenticator) {
		LOG.debug(String.format("get(%d, %s, %s)", packetIdentifier, address, Arrays.toString(authenticator)));
		return null;
	}
	
	@CachePut(value="receivedPacket", key="#address.hashCode() + '-' + new String(#authenticator).hashCode() + '-' + #packetIdentifier")
	public ReceivedPacket put(int packetIdentifier, InetSocketAddress address, byte[] authenticator) {
		LOG.debug(String.format("put(%d, %s, %s)", packetIdentifier, address, Arrays.toString(authenticator)));
		return new ReceivedPacket(packetIdentifier, address, authenticator);
	}
}
