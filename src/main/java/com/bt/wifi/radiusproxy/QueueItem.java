package com.bt.wifi.radiusproxy;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class QueueItem {

	private DatagramSocket datagramSocket;
	private DatagramPacket datagramPacket;
	private String secret;

	public QueueItem(DatagramSocket s, DatagramPacket packetIn, String secret) {
		this.datagramSocket = s;
		this.datagramPacket = packetIn;
		this.secret = secret;
	}

	public DatagramPacket getDatagramPacket() {
		return datagramPacket;
	}

	public DatagramSocket getDatagramSocket() {
		return datagramSocket;
	}

	public String getSecret() {
		return secret;
	}
}
