package com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.service;

import com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.domain.RequestEvent;
import com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.domain.ResponseEvent;
import com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.service.task.CountTask;
import com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.service.task.MaxTask;
import com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.service.task.MinTask;
import com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.service.task.SumTask;
import com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.service.task.TaskResults;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class RealTimeTransactionsStatsService {


  @Value("${task.executor.pool.size}")
  private String poolSize;

  @Value("${concurrent.task.time.out}")
  private int concurrentTaskTimeOut;

  private static final Logger LOGGER = LoggerFactory
      .getLogger(RealTimeTransactionsStatsService.class);

  private Map<Long, Double> transactionsList = new HashMap<>();
  private ExecutorService executor;
  private static final int OK = 201;
  private static final int NO_CONTENT = 204;

  @PostConstruct
  public void init() {
    executor = Executors.newFixedThreadPool(Integer.parseInt(poolSize));
  }

  public int pushToInMemoryCache(RequestEvent requestEvent) {
    int status;
    if (isValidTimeStamp(requestEvent.getTimestamp())) {
      this.transactionsList.put(System.currentTimeMillis(),
          requestEvent.getAmount());
      status = OK;
    } else {
      status = NO_CONTENT;
    }

    return status;
  }

  public ResponseEvent retrieveStatsFromMemory() throws ExecutionException, InterruptedException {
    long start = System.currentTimeMillis();
    ResponseEvent responseEvent = calculateStats();
    LOGGER.info("Stats calculation took: [{}ms]", (System.currentTimeMillis() - start));
    return responseEvent;
  }

  @Scheduled(cron = "${cron.scheduling}", zone = "${selected.timezone}")
  public void flush() {
    long start = System.currentTimeMillis();
    this.transactionsList = filterLast60SecondsTransactions(generateLast60SecondsTimestamp(),
        this.transactionsList);
    LOGGER.info("In-memory data flush took: [{}ms]", (System.currentTimeMillis() - start));

  }

  private boolean isValidTimeStamp(long timestamp) {
    return timestamp > generateLast60SecondsTimestamp();
  }

  private long generateLast60SecondsTimestamp() {
    return System.currentTimeMillis() - 60000;
  }

  private ResponseEvent calculateStats()
      throws InterruptedException, ExecutionException {
    ResponseEvent responseEvent = new ResponseEvent();
    generateLast60SecondsTimestamp();
    Map<Long, Double> listOfLast60SecondsTransactions = filterLast60SecondsTransactions(
        generateLast60SecondsTimestamp());
    if (!listOfLast60SecondsTransactions.isEmpty()) {
      Callable<TaskResults> minTask = new MinTask(listOfLast60SecondsTransactions);
      Callable<TaskResults> maxTask = new MaxTask(listOfLast60SecondsTransactions);
      Callable<TaskResults> sumTask = new SumTask(listOfLast60SecondsTransactions);
      Callable<TaskResults> countTask = new CountTask(listOfLast60SecondsTransactions);
      Collection<Callable<TaskResults>> tasks = new ArrayList<>(
          Arrays.asList(minTask, maxTask, sumTask, countTask));
      List<Future<TaskResults>> results = executor
          .invokeAll(tasks, concurrentTaskTimeOut, TimeUnit.SECONDS);
      for (Future<TaskResults> result : results) {
        if ("MinTask".equals(result.get().getTaskName())) {
          responseEvent.setMin((Double) result.get().getTaskResult());
        } else if ("MaxTask".equals(result.get().getTaskName())) {
          responseEvent.setMax((Double) result.get().getTaskResult());
        } else if ("SumTask".equals(result.get().getTaskName())) {
          responseEvent.setSum((Double) result.get().getTaskResult());
        } else if ("CountTask".equals(result.get().getTaskName())) {
          responseEvent.setCount((Integer) result.get().getTaskResult());
        }
      }
      responseEvent.setAvg(responseEvent.getSum() / responseEvent.getCount());
    }
    return responseEvent;
  }

  private Map<Long, Double> filterLast60SecondsTransactions(
      long lastMinuteTimestamp) {
    return this.transactionsList.entrySet().stream()
        .filter(trxn -> trxn.getKey().compareTo(lastMinuteTimestamp) > 0)
        .collect(Collectors.toMap(trxn -> trxn.getKey(), trxn -> trxn.getValue()));

  }

  private Map<Long, Double> filterLast60SecondsTransactions(
      long lastMinuteTimestamp, Map<Long, Double> tempTransactionList) {
    return tempTransactionList.entrySet().stream()
        .filter(trxn -> trxn.getKey().compareTo(lastMinuteTimestamp) > 0)
        .collect(Collectors.toMap(trxn -> trxn.getKey(), trxn -> trxn.getValue()));

  }

}