package com.ledgerlock.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * =====================================================================================
 * Entity: LedgerEntry
 * Represents an atomic, immutable financial debit or credit leg.
 * 
 * DESIGN HIGHLIGHTS:
 * 1. Read-only monetary fields (no public setters for amount, entryType, account).
 * 2. Amount stored in whole integer cents (BIGINT).
 * 3. Lazy-loaded foreign key references to prevent N+1 query amplification.
 * 4. SHA-256 hash fields for cryptographic audit chaining.
 * =====================================================================================
 */
@Entity
@Table(name = "ledger_entries")
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    // LAZY fetch avoids eager SQL joins when querying large batches of entries
    @NotNull(message = "Transaction reference is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaction_id", nullable = false, updatable = false)
    private Transaction transaction;

    @NotNull(message = "Account reference is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private Account account;

    @NotNull(message = "Entry type (DEBIT/CREDIT) is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, updatable = false, length = 10)
    private EntryType entryType;

    // Monetary amount stored in whole cents (e.g. 5000 = $50.00). Must be strictly positive.
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be strictly positive")
    @Column(name = "amount", nullable = false, updatable = false)
    private Long amount;

    @NotNull(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code")
    @Column(name = "currency", nullable = false, updatable = false, length = 3)
    private String currency;

    @NotNull(message = "Sequence number is required")
    @Column(name = "sequence_number", nullable = false, updatable = false)
    private Long sequenceNumber;

    // Predecessor entry's SHA-256 hash (null only for the account's very first transaction)
    @Column(name = "previous_hash", length = 64, updatable = false)
    private String previousHash;

    // Cryptographic hash of this entry: SHA-256(id || timestamp || amount || prev_hash)
    @NotNull(message = "Entry hash is required")
    @Column(name = "entry_hash", nullable = false, length = 64, updatable = false)
    private String entryHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // Default constructor required by JPA
    protected LedgerEntry() {}

    /**
     * Canonical constructor: Requires all financial attributes up-front.
     * Guarantees that a LedgerEntry is never instantiated in an incomplete state.
     */
    public LedgerEntry(
            Transaction transaction,
            Account account,
            EntryType entryType,
            Long amount,
            String currency,
            Long sequenceNumber,
            String previousHash,
            String entryHash
    ) {
        this.transaction = transaction;
        this.account = account;
        this.entryType = entryType;
        this.amount = amount;
        this.currency = currency;
        this.sequenceNumber = sequenceNumber;
        this.previousHash = previousHash;
        this.entryHash = entryHash;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    // Getters only (Immutability: No setters for financial or relational data!)
    public UUID getId() { return id; }
    public Transaction getTransaction() { return transaction; }
    public Account getAccount() { return account; }
    public EntryType getEntryType() { return entryType; }
    public Long getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public Long getSequenceNumber() { return sequenceNumber; }
    public String getPreviousHash() { return previousHash; }
    public String getEntryHash() { return entryHash; }
    public Instant getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LedgerEntry that)) return false;
        return Objects.equals(id, that.id) || 
               (Objects.equals(account, that.account) && Objects.equals(sequenceNumber, that.sequenceNumber));
    }

    @Override
    public int hashCode() {
        return Objects.hash(account, sequenceNumber);
    }
}