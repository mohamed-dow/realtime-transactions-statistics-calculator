package com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.service.task;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskResults {

  private final String taskName;
  private Object taskResult;

  public TaskResults(String taskName) {
    this.taskName = taskName;
  }
}
