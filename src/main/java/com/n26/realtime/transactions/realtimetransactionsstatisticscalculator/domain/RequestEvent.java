package com.n26.realtime.transactions.realtimetransactionsstatisticscalculator.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties
public class RequestEvent {

  private double amount;
  private long timestamp;
}
