package com.ledgerlock.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Double-Entry Bookkeeping Invariant Unit Tests")
class DoubleEntryValidatorTest {

    private Account aliceAccount;
    private Account bobAccount;
    private Account platformFeeAccount;
    private Transaction sampleTransaction;

    @BeforeEach
    void setUp() {
        aliceAccount = new Account("ACC-ALICE-01", "USD");
        bobAccount = new Account("ACC-BOB-02", "USD");
        platformFeeAccount = new Account("ACC-PLATFORM-FEE", "USD");
        sampleTransaction = new Transaction("IDEM-KEY-001", "Peer-to-peer wallet transfer");
    }

    @Test
    @DisplayName("Should PASS when debits and credits are perfectly balanced ($50.00)")
    void shouldPassWhenDebitsEqualCredits() {
        LedgerEntry debitLeg = new LedgerEntry(
                sampleTransaction, aliceAccount, EntryType.DEBIT, 5000L, "USD", 1L, null, "hash-1"
        );
        LedgerEntry creditLeg = new LedgerEntry(
                sampleTransaction, bobAccount, EntryType.CREDIT, 5000L, "USD", 1L, null, "hash-2"
        );

        assertDoesNotThrow(() -> DoubleEntryValidator.validate(List.of(debitLeg, creditLeg)));
    }

    @Test
    @DisplayName("Should PASS for multi-leg split transaction ($100 Debit = $70 Merchant + $30 Platform Fee)")
    void shouldPassForMultiLegSplitTransaction() {
        LedgerEntry buyerDebit = new LedgerEntry(
                sampleTransaction, aliceAccount, EntryType.DEBIT, 10000L, "USD", 1L, null, "hash-buyer"
        );
        LedgerEntry merchantCredit = new LedgerEntry(
                sampleTransaction, bobAccount, EntryType.CREDIT, 7000L, "USD", 1L, null, "hash-merchant"
        );
        LedgerEntry platformFeeCredit = new LedgerEntry(
                sampleTransaction, platformFeeAccount, EntryType.CREDIT, 3000L, "USD", 1L, null, "hash-fee"
        );

        assertDoesNotThrow(() -> DoubleEntryValidator.validate(List.of(buyerDebit, merchantCredit, platformFeeCredit)));
    }

    @Test
    @DisplayName("Should FAIL and throw exception when debits and credits are unbalanced ($100 vs $90)")
    void shouldFailWhenTransactionIsUnbalanced() {
        LedgerEntry debitLeg = new LedgerEntry(
                sampleTransaction, aliceAccount, EntryType.DEBIT, 10000L, "USD", 1L, null, "hash-1"
        );
        LedgerEntry creditLeg = new LedgerEntry(
                sampleTransaction, bobAccount, EntryType.CREDIT, 9000L, "USD", 1L, null, "hash-2"
        );

        UnbalancedTransactionException ex = assertThrows(
                UnbalancedTransactionException.class,
                () -> DoubleEntryValidator.validate(List.of(debitLeg, creditLeg))
        );

        assertTrue(ex.getMessage().contains("Net imbalance: 1000 cents"));
    }

    @Test
    @DisplayName("Should FAIL when transaction has fewer than 2 entries (single leg)")
    void shouldFailWhenLessThanTwoEntries() {
        LedgerEntry loneDebit = new LedgerEntry(
                sampleTransaction, aliceAccount, EntryType.DEBIT, 5000L, "USD", 1L, null, "hash-1"
        );

        assertThrows(UnbalancedTransactionException.class, () -> DoubleEntryValidator.validate(List.of(loneDebit)));
    }

    @Test
    @DisplayName("Should FAIL when currency mismatch occurs across transaction legs")
    void shouldFailWhenCurrencyMismatched() {
        LedgerEntry debitUSD = new LedgerEntry(
                sampleTransaction, aliceAccount, EntryType.DEBIT, 5000L, "USD", 1L, null, "hash-1"
        );
        Account eurAccount = new Account("ACC-EUR-99", "EUR");
        LedgerEntry creditEUR = new LedgerEntry(
                sampleTransaction, eurAccount, EntryType.CREDIT, 5000L, "EUR", 1L, null, "hash-2"
        );

        assertThrows(UnbalancedTransactionException.class, () -> DoubleEntryValidator.validate(List.of(debitUSD, creditEUR)));
    }
}
