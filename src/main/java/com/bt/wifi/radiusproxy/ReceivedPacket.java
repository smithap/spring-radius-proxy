package com.bt.wifi.radiusproxy;

import java.net.InetSocketAddress;
import java.util.Arrays;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * This internal class represents a packet that has been received by the
 * server.
 */
public class ReceivedPacket {
	/**
	 * The identifier of the packet.
	 */
	private int packetIdentifier;

	/**
	 * The address of the host who sent the packet.
	 */
	private InetSocketAddress address;

	/**
	 * Authenticator of the received packet.
	 */
	private byte[] authenticator;
	
	public ReceivedPacket(int packetIdentifier, InetSocketAddress address, byte[] authenticator) {
		this.packetIdentifier = packetIdentifier;
		this.address = address;
		this.authenticator = authenticator;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + Arrays.hashCode(authenticator);
		result = prime * result + packetIdentifier;
		return result;
	}
	
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this).toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReceivedPacket other = (ReceivedPacket) obj;
		if (other.address.equals(address) && other.packetIdentifier == packetIdentifier) {
			if (authenticator != null && other.authenticator != null) {
				// packet is duplicate if stored authenticator is equal to
				// the packet authenticator
				return Arrays.equals(other.authenticator, authenticator);
			}
			// should not happen, packet is duplicate
			return true;
		}
		return false;
	}
}