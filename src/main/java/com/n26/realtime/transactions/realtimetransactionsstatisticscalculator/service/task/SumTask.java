package com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.service.task;

import java.util.Map;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SumTask implements Callable<TaskResults> {

  private Map<Long, Double> transactionListFor60Seconds;
  private static final Logger LOGGER = LoggerFactory.getLogger(SumTask.class);

  public SumTask(Map<Long, Double> transactionListFor60Seconds) {
    this.transactionListFor60Seconds = transactionListFor60Seconds;
  }

  @Override
  public TaskResults call() {
    long start = System.currentTimeMillis();
    TaskResults result = new TaskResults(this.getClass().getSimpleName());
    result.setTaskResult(
        this.transactionListFor60Seconds.values().stream().mapToDouble(Double::doubleValue)
            .sum());
    LOGGER.info("Sum Task took: [{}ms]", System.currentTimeMillis() - start);
    return result;
  }
}