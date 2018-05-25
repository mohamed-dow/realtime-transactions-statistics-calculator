package com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.controller;

import static org.junit.Assert.assertEquals;

import com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.domain.RequestEvent;
import com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.domain.ResponseEvent;
import com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.service.RealTimeTransactionsStatsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.support.membermodification.MemberModifier;
import org.springframework.http.HttpStatus;

@RunWith(MockitoJUnitRunner.class)
public class RealTimeTransactionStatsControllerTest {

  @InjectMocks
  private RealTimeTransactionStatsController realTimeTransactionStatsController;

  @Mock
  private RealTimeTransactionsStatsService realTimeTransactionsStatsService;

  @Before
  public void setUp() throws IllegalAccessException {
    MemberModifier
        .field(RealTimeTransactionStatsController.class, "realTimeTransactionsStatsService")
        .set(realTimeTransactionStatsController, realTimeTransactionsStatsService);
  }

  @Test
  public void submitTransactionTestSuccess() throws Exception {
    RequestEvent requestEvent = new RequestEvent();
    Mockito.when(realTimeTransactionsStatsService.pushToInMemoryCache(Matchers.anyObject()))
        .thenReturn(201);
    assertEquals(realTimeTransactionStatsController.submitTransactions(requestEvent), 201);
  }

  @Test
  public void submitTransactionTestNotSubmitted() throws Exception {
    RequestEvent requestEvent = new RequestEvent();
    Mockito.when(realTimeTransactionsStatsService.pushToInMemoryCache(Matchers.anyObject()))
        .thenReturn(204);
    assertEquals(realTimeTransactionStatsController.submitTransactions(requestEvent), 204);
  }

  @Test
  public void retrieveStatsTestHappyPath() throws Exception {
    ResponseEvent expectedResponseEvent = new ResponseEvent();
    expectedResponseEvent.setCount(90);
    expectedResponseEvent.setMax(1000);
    expectedResponseEvent.setMin(2);
    expectedResponseEvent.setSum(4030.08);
    expectedResponseEvent
        .setAvg(expectedResponseEvent.getSum() / expectedResponseEvent.getCount());
    Mockito.when(realTimeTransactionsStatsService.retrieveStatsFromMemory())
        .thenReturn(expectedResponseEvent);
    ResponseEvent actualResponseEvent = realTimeTransactionStatsController.retrieveStats();
    assertEquals(expectedResponseEvent.getCount(), actualResponseEvent.getCount());
    assertEquals(expectedResponseEvent.getAvg(), actualResponseEvent.getAvg(), 00);
    assertEquals(expectedResponseEvent.getMax(), actualResponseEvent.getMax(), 00);
    assertEquals(expectedResponseEvent.getMin(), actualResponseEvent.getMin(), 00);
    assertEquals(expectedResponseEvent.getSum(), actualResponseEvent.getSum(), 00);
  }

  @Test
  public void flushTestHappyPath() throws Exception {
    Mockito.doNothing().when(realTimeTransactionsStatsService).flush();
    assertEquals(realTimeTransactionStatsController.flush(), HttpStatus.OK);
  }
}