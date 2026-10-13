package com.ledgerlock.domain;

/**
 * Lifecycle states of an idempotent request execution.
 */
public enum IdempotencyStatus {
    IN_FLIGHT,   // Request currently being processed by a worker thread
    COMPLETED,   // Successfully executed and cached
    FAILED       // Execution failed (client can retry with a new or same key)
}