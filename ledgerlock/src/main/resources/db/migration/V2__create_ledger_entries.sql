-- =====================================================================================
-- Migration: V2__create_ledger_entries.sql
-- Description: Creates the core immutable ledger_entries table with composite indexes
-- =====================================================================================

CREATE TABLE ledger_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    
    -- Foreign key to parent transaction; RESTRICT prevents parent transaction deletion
    transaction_id UUID NOT NULL REFERENCES transactions(id) ON DELETE RESTRICT,
    
    -- Foreign key to account; RESTRICT prevents account deletion if history exists
    account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE RESTRICT,
    
    -- Type must be either DEBIT or CREDIT
    entry_type VARCHAR(10) NOT NULL,
    
    -- Amount in whole integer cents (e.g. 5000 = $50.00). Must strictly be positive.
    amount BIGINT NOT NULL,
    
    -- Currency matching ISO-4217 standard
    currency VARCHAR(3) NOT NULL,
    
    -- Strict monotonic sequence number per account for ordering verification
    sequence_number BIGINT NOT NULL,
    
    -- Cryptographic hash of the predecessor entry (null only for the account's first entry)
    previous_hash VARCHAR(64),
    
    -- Cryptographic SHA-256 hash of this entry: SHA256(id || timestamp || amount || prev_hash)
    entry_hash VARCHAR(64) NOT NULL,
    
    -- Timestamp with timezone awareness
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Constraints
    CONSTRAINT chk_ledger_entries_type CHECK (entry_type IN ('DEBIT', 'CREDIT')),
    CONSTRAINT chk_ledger_entries_positive_amount CHECK (amount > 0),
    CONSTRAINT chk_ledger_entries_currency CHECK (currency IN ('USD', 'EUR', 'GBP', 'INR')),
    CONSTRAINT uq_account_sequence UNIQUE (account_id, sequence_number)
);

-- =====================================================================================
-- Performance Indexes
-- =====================================================================================

-- 1. Composite Timeseries Index: High-speed chronological query for account ledger history
CREATE INDEX idx_ledger_entries_account_created ON ledger_entries (account_id, created_at DESC);

-- 2. Lookup index to fetch all debit/credit legs for a transaction to verify balance
CREATE INDEX idx_ledger_entries_transaction_id ON ledger_entries (transaction_id);

-- 3. Sequence index for verifying monotonic hash chain continuity
CREATE INDEX idx_ledger_entries_account_sequence ON ledger_entries (account_id, sequence_number);