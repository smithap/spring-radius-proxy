package com.bt.wifi.radiusproxy;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.tinyradius.util.RadiusClient;
import org.tinyradius.util.RadiusException;

public class ClientTest {

	@Test
	public void test() throws IOException, RadiusException {
		RadiusClient radiusClient = new RadiusClient("localhost", "testing123");
		radiusClient.setAuthPort(1812);
		boolean result = radiusClient.authenticate("adrian", "secret");
		assertTrue(result);
	}
}
