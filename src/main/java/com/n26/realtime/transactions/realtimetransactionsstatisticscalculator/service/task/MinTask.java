package com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.service.task;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinTask implements Callable<TaskResults> {

  private Map<Long, Double> transactionListFor60Seconds;
  private static final Logger LOGGER = LoggerFactory.getLogger(MinTask.class);

  public MinTask(Map<Long, Double> transactionListFor60Seconds) {
    this.transactionListFor60Seconds = transactionListFor60Seconds;
  }

  @Override
  public TaskResults call() {
    long start = System.currentTimeMillis();
    TaskResults result = new TaskResults(this.getClass().getSimpleName());
    result.setTaskResult(
        this.transactionListFor60Seconds.values().stream().min(
            Comparator.naturalOrder()).get());
    LOGGER.info("Min Task took: [{}ms]", System.currentTimeMillis() - start);
    return result;
  }
}
