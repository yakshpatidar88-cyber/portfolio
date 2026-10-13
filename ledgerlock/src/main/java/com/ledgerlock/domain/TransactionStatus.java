package com.ledgerlock.domain;

/**
 * Settlement states for a multi-leg Transaction.
 */
 public enum TransactionStatus {
     PENDING,
     SETTLED,
     FAILED,
     REVERSED
 }