# 📐 LedgerLock Formal Domain & Schema Specification

This document defines the mathematical invariants, entity-relationship models, and lifecycle states of the LedgerLock double-entry accounting engine.

---

## 1. The Core Invariants

### Invariant 1: Conservation of Money (Zero-Sum Invariant)
Every business transaction $T$ must consist of a set of ledger entries $E_T$:
$$\sum_{e \in E_T, \text{type}=\text{DEBIT}} e.\text{amount} - \sum_{e \in E_T, \text{type}=\text{CREDIT}} e.\text{amount} = 0.0000$$
- If the net sum is non-zero, the database transaction automatically aborts and rolls back.
- Money is never created or destroyed; it is only transferred between accounts.

### Invariant 2: Append-Only Immutability
- No record in `ledger_entries` may ever be modified (`UPDATE`) or removed (`DELETE`).
- Corrections to historical transactions are performed exclusively by appending **compensating transactions** (reversals).

### Invariant 3: Deterministic Resource Lock Ordering
- When transferring funds between `Account A` and `Account B`, locks are always acquired in strictly sorted lexicographical order of their UUIDs:
  $$\text{Lock}(\min(A, B)) \longrightarrow \text{Lock}(\max(A, B))$$
- This mathematically prevents Coffman's Circular Wait condition, eliminating deadlocks.

### Invariant 4: Cryptographic Hash Chaining
- Each entry $N$ in the ledger stores a SHA-256 hash containing its predecessor's hash:
  $$\text{Hash}_N = \text{SHA-256}(\text{EntryID}_N \parallel \text{Timestamp}_N \parallel \text{Amount}_N \parallel \text{Hash}_{N-1})$$
- Retroactive tampering of any database row breaks the chain for all subsequent rows.

---

## 2. Entity Data Models

### Entity 1: `Account`
Represents an economic entity holding funds (user wallet, platform treasury, merchant reserve).
- `id` (UUID v4, Primary Key)
- `account_number` (VARCHAR(32), Unique, Indexed)
- `currency` (VARCHAR(3), ISO-4217, e.g. 'USD', 'INR')
- `status` (ENUM: 'ACTIVE', 'FROZEN', 'CLOSED')
- `created_at` (TIMESTAMPTZ, Default NOW())
- `updated_at` (TIMESTAMPTZ, Default NOW())
*Note: Accounts deliberately contain NO balance column.*

### Entity 2: `Transaction`
Represents the business context of a financial movement.
- `id` (UUID v4, Primary Key)
- `idempotency_key` (VARCHAR(128), Unique, Indexed)
- `description` (VARCHAR(255))
- `status` (ENUM: 'PENDING', 'SETTLED', 'FAILED', 'REVERSED')
- `created_at` (TIMESTAMPTZ, Default NOW())
- `posted_at` (TIMESTAMPTZ, Nullable)

### Entity 3: `LedgerEntry`
The atomic, balanced movement of value.
- `id` (UUID v4, Primary Key)
- `transaction_id` (UUID, Foreign Key -> `Transaction.id`, Indexed)
- `account_id` (UUID, Foreign Key -> `Account.id`, Indexed)
- `entry_type` (ENUM: 'DEBIT', 'CREDIT')
- `amount` (BIGINT, Cent/Cents precision, e.g. 10000 = $100.00, CHECK amount > 0)
- `currency` (VARCHAR(3), ISO-4217)
- `sequence_number` (BIGINT, Monotonically Increasing, Unique per Account)
- `previous_hash` (VARCHAR(64), SHA-256 of previous entry)
- `entry_hash` (VARCHAR(64), SHA-256 of current entry)
- `created_at` (TIMESTAMPTZ, Default NOW())

### Entity 4: `IdempotencyRecord`
Guarantees network retries do not execute duplicate transfers.
- `key` (VARCHAR(128), Primary Key)
- `request_hash` (VARCHAR(64), SHA-256 of HTTP payload)
- `status` (ENUM: 'IN_FLIGHT', 'COMPLETED', 'FAILED')
- `response_body` (TEXT, Cached JSON response)
- `created_at` (TIMESTAMPTZ, Default NOW())
- `expires_at` (TIMESTAMPTZ, Indexed for TTL eviction)