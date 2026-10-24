package com.ledgerlock.repository;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ledgerlock.domain.IdempotencyRecord;

/**
 * =====================================================================================
 * Repository: IdempotencyRecordRepository
 * Manages API idempotency records and TTL-based cache eviction.
 * =====================================================================================
 */
@Repository
public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, String> {

    /**
     * Looks up an idempotency record by client idempotency key.
     */
    Optional<IdempotencyRecord> findByIdempotencyKey(String idempotencyKey);

    /**
     * Bulk deletion of expired idempotency records.
     * Prevents table bloat by purging keys older than their TTL.
     * 
     * @param now Current timestamp
     * @return Number of purged records
     */
    @Modifying
    @Query("DELETE FROM IdempotencyRecord r WHERE r.expiresAt < :now")
    int deleteExpiredRecords(@Param("now") Instant now);
}
