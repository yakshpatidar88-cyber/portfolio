package com.ledgerlock.domain;

import java.util.List;

/**
 * =====================================================================================
 * Validator: DoubleEntryValidator
 * Enforces the core accounting invariants for any proposed set of LedgerEntries.
 * =====================================================================================
 */
public class DoubleEntryValidator {

    /**
     * Validates that the provided entries satisfy all double-entry invariants.
     * 
     * @param entries List of LedgerEntry legs belonging to a single transaction
     * @throws UnbalancedTransactionException if any invariant is violated
     */
    public static void validate(List<LedgerEntry> entries) {
        if (entries == null || entries.size() < 2) {
            throw new UnbalancedTransactionException("A transaction must contain at least two ledger entries (minimum one debit and one credit).");
        }

        long totalDebits = 0L;
        long totalCredits = 0L;
        String expectedCurrency = entries.get(0).getCurrency();

        for (LedgerEntry entry : entries) {
            // Rule 1: Currency consistency
            if (!expectedCurrency.equals(entry.getCurrency())) {
                throw new UnbalancedTransactionException(String.format(
                        "Currency mismatch detected! Expected: %s, Found: %s in entry for account: %s",
                        expectedCurrency, entry.getCurrency(), entry.getAccount().getAccountNumber()));
            }

            // Rule 2: Amount must be positive
            if (entry.getAmount() <= 0) {
                throw new UnbalancedTransactionException("Ledger entry amount must be strictly greater than zero.");
            }

            // Sum debits and credits
            if (entry.getEntryType() == EntryType.DEBIT) {
                totalDebits += entry.getAmount();
            } else if (entry.getEntryType() == EntryType.CREDIT) {
                totalCredits += entry.getAmount();
            }
        }

        // Rule 3: Zero-sum invariant (Sum of Debits == Sum of Credits)
        if (totalDebits != totalCredits) {
            throw new UnbalancedTransactionException(totalDebits, totalCredits);
        }
    }
}
