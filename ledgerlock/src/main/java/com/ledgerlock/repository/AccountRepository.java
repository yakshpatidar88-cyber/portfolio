package com.ledgerlock.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ledgerlock.domain.Account;

import jakarta.persistence.LockModeType;

/**
 * =====================================================================================
 * Repository: AccountRepository
 * Manages database access for Account entities with pessimistic row locking.
 * =====================================================================================
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    /**
     * Standard non-locking lookup by unique account number.
     * Used for read-only queries.
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Acquires an exclusive row-level lock (SELECT ... FOR UPDATE) on the account by UUID.
     * 
     * Concurrency Guarantee:
     * Any other transaction attempting to acquire a lock on this account will be 
     * blocked until the current transaction commits or rolls back.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdWithPessimisticLock(@Param("id") UUID id);

    /**
     * Acquires an exclusive row-level lock (SELECT ... FOR UPDATE) on the account by account number.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberWithPessimisticLock(@Param("accountNumber") String accountNumber);
}
