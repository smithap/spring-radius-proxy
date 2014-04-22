package com.bt.wifi.radiusproxy;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.tinyradius.attribute.RadiusAttribute;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusException;

@Component
public class SimpleHandler implements Handler {
	private static final Log LOG = LogFactory.getLog(SimpleHandler.class);

	@Override
	public RadiusPacket accessRequestReceived(AccessRequest accessRequest, InetSocketAddress client) throws RadiusException {
		LOG.debug(String.format("accessRequestReceived(%s, %s)", accessRequest, client));
		// return null;
		String plaintext = getUserPassword(accessRequest.getUserName());
		int type = RadiusPacket.ACCESS_REJECT;
		if ("adrian".equals(accessRequest.getUserName()))
			if (plaintext != null && accessRequest.verifyPassword(plaintext))
				type = RadiusPacket.ACCESS_ACCEPT;

		RadiusPacket answer = new RadiusPacket(type, accessRequest.getPacketIdentifier());
		copyProxyState(accessRequest, answer);
		return answer;
	}

	private String getUserPassword(String userName) {
		return "secret";
	}

	protected void copyProxyState(RadiusPacket request, RadiusPacket answer) {
		List proxyStateAttrs = request.getAttributes(33);
		for (Iterator i = proxyStateAttrs.iterator(); i.hasNext();) {
			RadiusAttribute proxyStateAttr = (RadiusAttribute) i.next();
			answer.addAttribute(proxyStateAttr);
		}
	}

}
