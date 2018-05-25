
package com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.controller;

    import com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.domain.RequestEvent;
    import com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.domain.ResponseEvent;
    import com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.service.RealTimeTransactionsStatsService;
    import java.util.concurrent.ExecutionException;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.MediaType;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestMethod;
    import org.springframework.web.bind.annotation.RestController;

@RestController
public class RealTimeTransactionStatsController {

  @Autowired
  private RealTimeTransactionsStatsService realTimeTransactionsStatsService;


  private static final Logger LOGGER = LoggerFactory
      .getLogger(RealTimeTransactionStatsController.class);

  @RequestMapping(value = "/transactions", method = RequestMethod.POST, consumes = {
      MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
  public int submitTransactions(
      @RequestBody RequestEvent requestEvent) {
    long start = System.currentTimeMillis();
    int httpStatusCode = realTimeTransactionsStatsService.pushToInMemoryCache(requestEvent);
    LOGGER.info("Transaction Service took [{}ms] to process the data",
        (System.currentTimeMillis() - start));
    return httpStatusCode;
  }

  @RequestMapping(value = "/statistics", method = RequestMethod.GET, consumes = {
      MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
  public ResponseEvent retrieveStats() {
    long start = System.currentTimeMillis();
    ResponseEvent responseEvent = null;
    try {
      responseEvent = realTimeTransactionsStatsService.retrieveStatsFromMemory();
    } catch (InterruptedException | ExecutionException e) {
      LOGGER.error(
          "Stats service failed while processing concurrent tasks, error Message:[{}] exception [{}]",
          e.getMessage(), e);
    }
    LOGGER.info("Statistics Service took: [{}ms]",
        (System.currentTimeMillis() - start));
    return responseEvent;
  }

  @RequestMapping(value = "/flush", method = RequestMethod.GET)
  public HttpStatus flush() {
    long start = System.currentTimeMillis();
    realTimeTransactionsStatsService.flush();
    LOGGER.info("Transaction Service took [{}ms] to process the data",
        (System.currentTimeMillis() - start));
    return HttpStatus.OK;
  }
}