package com.bt.wifi.radiusproxy;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Main {

	public static void main(String[] args) {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		//applicationContext.getBean(MultiThreadedServer.class).start(true, true);
		//applicationContext.getBean(QueueManager.class).async();
	}
}
