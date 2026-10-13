package com.ledgerlock.domain;

/**
 * Fundamental financial leg type in double-entry bookkeeping.
 * - DEBIT: Decreases liability/equity or increases assets.
 * - CREDIT: Increases liability/equity or decreases assets.
 */
public enum EntryType {
    DEBIT,
    CREDIT
}