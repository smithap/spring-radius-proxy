package com.bt.wifi.radiusproxy;

import java.net.InetSocketAddress;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.tinyradius.packet.AccessRequest;
import org.tinyradius.packet.RadiusPacket;
import org.tinyradius.util.RadiusException;

@Component
public class HandlerManager {
	private static final Log LOG = LogFactory.getLog(HandlerManager.class);
	
	@Resource
	private List<Handler> handlers;

	public RadiusPacket accessRequestReceived(AccessRequest accessRequest, InetSocketAddress client) throws RadiusException {
		LOG.debug(String.format("accessRequestReceived(%s, %s)", accessRequest, client));
		for (Handler handler: handlers) {
			RadiusPacket result = handler.accessRequestReceived(accessRequest, client);
			if (null != result)
				return result;
		}
		return null;
	}
}
