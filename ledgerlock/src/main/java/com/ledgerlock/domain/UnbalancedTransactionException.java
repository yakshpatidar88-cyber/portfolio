package com.ledgerlock.domain;

/**
 * Thrown when a financial transaction violates the double-entry bookkeeping invariant:
 * (Sum of Debits != Sum of Credits).
 */
public class UnbalancedTransactionException extends RuntimeException {
    
    public UnbalancedTransactionException(String message) {
        super(message);
    }

    public UnbalancedTransactionException(Long totalDebits, Long totalCredits) {
        super(String.format("Double-entry transaction is unbalanced! Total Debits: %d cents, Total Credits: %d cents. Net imbalance: %d cents.",
                totalDebits, totalCredits, (totalDebits - totalCredits)));
    }
}