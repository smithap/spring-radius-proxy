package com.bt.wifi.radiusproxy;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfSuiteRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(ContiPerfSuiteRunner.class)
@SuiteClasses(ClientTest.class)
//@PerfTest(invocations = 10, threads = 200, timer = RandomTimer.class, timerParams = { 50, 500 })
@PerfTest(invocations = 10000, threads = 200)
public class ClientLoadTest {

}
