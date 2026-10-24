package com.ledgerlock.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ledgerlock.domain.LedgerEntry;

/**
 * =====================================================================================
 * Repository: LedgerEntryRepository
 * Manages persistence and querying for immutable financial ledger entries.
 * =====================================================================================
 */
@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, UUID> {

    /**
     * Calculates the exact current balance for an account by summing all historical entries:
     * Balance = Sum(Credits) - Sum(Debits)
     * 
     * COALESCE ensures brand-new accounts with 0 entries return 0L instead of null.
     */
    @Query("SELECT COALESCE(SUM(CASE WHEN e.entryType = com.ledgerlock.domain.EntryType.CREDIT THEN e.amount ELSE -e.amount END), 0L) " +
           "FROM LedgerEntry e " +
           "WHERE e.account.id = :accountId")
    Long calculateBalanceByAccountId(@Param("accountId") UUID accountId);

    /**
     * Fetches the latest entry for an account to determine the next monotonic sequence number
     * and retrieve the predecessor hash for cryptographic SHA-256 chaining.
     */
    Optional<LedgerEntry> findTopByAccountIdOrderBySequenceNumberDesc(UUID accountId);

    /**
     * Fetches all ledger legs (debits and credits) belonging to a specific transaction.
     * Used for transaction reconciliation and double-entry parity auditing.
     */
    List<LedgerEntry> findByTransactionIdOrderByCreatedAtAsc(UUID transactionId);

    /**
     * Paginated chronological ledger feed for an account.
     * Optimized by our composite B-Tree timeseries index: (account_id, created_at DESC).
     */
    Page<LedgerEntry> findByAccountIdOrderByCreatedAtDesc(UUID accountId, Pageable pageable);
}