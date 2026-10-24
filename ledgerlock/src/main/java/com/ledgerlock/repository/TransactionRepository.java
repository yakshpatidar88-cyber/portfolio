package com.ledgerlock.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ledgerlock.domain.Transaction;
import com.ledgerlock.domain.TransactionStatus;

/**
 * =====================================================================================
 * Repository: TransactionRepository
 * Manages database access for business transactions and idempotency deduplication.
 * =====================================================================================
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    /**
     * Finds an existing transaction by client-provided idempotency key.
     */
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);

    /**
     * Queries transactions by settlement status (e.g. PENDING, SETTLED).
     */
    List<Transaction> findByStatus(TransactionStatus status);

    /**
     * Paginated transaction feed ordered by creation date descending.
     */
    Page<Transaction> findAllByOrderByCreatedAtDesc(Pageable pageable);
}