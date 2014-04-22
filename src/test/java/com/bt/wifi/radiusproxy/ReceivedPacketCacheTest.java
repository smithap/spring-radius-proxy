package com.bt.wifi.radiusproxy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.InetSocketAddress;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:applicationContext.xml" })
@DirtiesContext
public class ReceivedPacketCacheTest {
	@Autowired
	private ReceivedPacketCache receivedPacketCache;
	
	private byte[] authenticator = "asdf".getBytes();
	private InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", 22233);
	private int packetIdentifier = 123;

	@Test
	public void testPut1() {
		// setup
		receivedPacketCache.put(packetIdentifier, address, authenticator);

		// act
		ReceivedPacket result = receivedPacketCache.get(packetIdentifier, address, authenticator);
		System.err.println(result);

		// assert
		assertNotNull(result);
	}

	@Test
	public void testPut2() {
		// setup
		receivedPacketCache.put(44444, address, "authenticator".getBytes());

		// act
		ReceivedPacket result = receivedPacketCache.get(44444, address, "authenticator".getBytes());
		System.err.println(result);

		// assert
		assertNotNull(result);
	}

	@Test
	public void testMiss() {
		// setup

		// act
		ReceivedPacket result = receivedPacketCache.get(121212, address, authenticator);
		System.err.println(result);

		// assert
		assertNull(result);
	}
}
