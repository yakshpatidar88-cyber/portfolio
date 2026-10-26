package com.ledgerlock.concurrency;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * =====================================================================================
 * Concurrency: OrderedAccountPair
 * Enforces a global deterministic lock acquisition order between accounts.
 * 
 * MATHEMATICAL PROOF (Elimination of Coffman's Circular Wait):
 * For any two accounts A and B:
 * If all concurrent threads acquire Lock(min(A, B)) before Lock(max(A, B)),
 * a cycle in the resource allocation graph cannot form.
 * Therefore, deadlocks are mathematically impossible.
 * =====================================================================================
 */
public record OrderedAccountPair(UUID firstLockId, UUID secondLockId) {

    public OrderedAccountPair {
        Objects.requireNonNull(firstLockId, "First lock ID must not be null");
        Objects.requireNonNull(secondLockId, "Second lock ID must not be null");
        if (firstLockId.equals(secondLockId)) {
            throw new IllegalArgumentException("Cannot create an ordered lock pair for the same account: " + firstLockId);
        }
    }

    /**
     * Factory method that deterministically sorts two account UUIDs in natural lexicographical order.
     * 
     * @param accountA First account UUID
     * @param accountB Second account UUID
     * @return OrderedAccountPair with firstLockId < secondLockId
     */
    public static OrderedAccountPair of(UUID accountA, UUID accountB) {
        Objects.requireNonNull(accountA, "Source account ID must not be null");
        Objects.requireNonNull(accountB, "Destination account ID must not be null");

        if (accountA.compareTo(accountB) < 0) {
            return new OrderedAccountPair(accountA, accountB);
        } else if (accountA.compareTo(accountB) > 0) {
            return new OrderedAccountPair(accountB, accountA);
        } else {
            throw new IllegalArgumentException("Self-transfer detected: Account cannot transfer funds to itself (" + accountA + ")");
        }
    }

    /**
     * Deterministically sorts an arbitrary collection of account UUIDs for multi-leg split transactions.
     * 
     * @param accountIds Collection of account UUIDs involved in the transaction
     * @return Distinct, sorted list of UUIDs to acquire in sequence
     */
    public static List<UUID> sortDeterministically(Collection<UUID> accountIds) {
        Objects.requireNonNull(accountIds, "Account IDs collection must not be null");
        return accountIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();
    }
}