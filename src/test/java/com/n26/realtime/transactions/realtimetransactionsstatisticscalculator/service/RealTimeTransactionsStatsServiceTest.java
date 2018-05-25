package com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.service;

import static org.testng.AssertJUnit.assertEquals;

import com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.domain.RequestEvent;
import com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.domain.ResponseEvent;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ExecutionException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.support.membermodification.MemberModifier;

@RunWith(MockitoJUnitRunner.class)
public class RealTimeTransactionsStatsServiceTest {


  private RealTimeTransactionsStatsService realTimeTransactionsStatsService = new RealTimeTransactionsStatsService();


  @Before
  public void setUp() throws IllegalAccessException {
    MemberModifier.field(RealTimeTransactionsStatsService.class, "poolSize")
        .set(realTimeTransactionsStatsService, "5");
    MemberModifier.field(RealTimeTransactionsStatsService.class, "concurrentTaskTimeOut")
        .set(realTimeTransactionsStatsService, 10);
    realTimeTransactionsStatsService.init();
  }

  @Test
  public void pushToInMemoryCacheTestSuccess() {
    RequestEvent requestEvent = new RequestEvent();
    requestEvent.setAmount(100.04);
    requestEvent.setTimestamp(System.currentTimeMillis());
    assertEquals(201, realTimeTransactionsStatsService.pushToInMemoryCache(requestEvent));
  }

  @Test
  public void pushToInMemoryCacheTestNotSubmitted() {
    RequestEvent requestEvent = new RequestEvent();
    requestEvent.setAmount(100.04);
    requestEvent.setTimestamp(ZonedDateTime
        .ofInstant(Instant.ofEpochMilli(Instant.now().toEpochMilli()),
            ZoneId.of("UTC")).minusMinutes(5).toEpochSecond());
    assertEquals(204, realTimeTransactionsStatsService.pushToInMemoryCache(requestEvent));
  }
  @Ignore("Test is ignored for the time being till bug fix")
  @Test
  public void retrieveStatsFromMemoryTestHappyPath()
      throws ExecutionException, InterruptedException {
    ResponseEvent expectedResponseEvent = new ResponseEvent();
    expectedResponseEvent.setCount(11);
    expectedResponseEvent.setMax(9);
    expectedResponseEvent.setMin(0);
    expectedResponseEvent.setSum(54.0);
    expectedResponseEvent
        .setAvg(expectedResponseEvent.getSum() / expectedResponseEvent.getCount());
    pushStubDataToInMemoryCache();
    ResponseEvent actualResponseEvent = realTimeTransactionsStatsService
        .retrieveStatsFromMemory();
    assertEquals(expectedResponseEvent.getCount(), actualResponseEvent.getCount());
    assertEquals(expectedResponseEvent.getMax(), actualResponseEvent.getMax(), 00);
    assertEquals(expectedResponseEvent.getMin(), actualResponseEvent.getMin(), 00);
    assertEquals(expectedResponseEvent.getSum(), actualResponseEvent.getSum(), 00);

  }

  private void pushStubDataToInMemoryCache() {
    RequestEvent requestEvent = new RequestEvent();
    int x = 0;
    while (x < 10) {
      requestEvent.setAmount(x++);
      requestEvent.setTimestamp(System.currentTimeMillis());
      realTimeTransactionsStatsService.pushToInMemoryCache(requestEvent);
    }
  }
}