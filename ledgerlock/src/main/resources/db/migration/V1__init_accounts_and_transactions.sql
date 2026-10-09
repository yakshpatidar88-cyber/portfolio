-- =====================================================================================
-- Migration: V1__init_accounts_and_transactions.sql
-- Description: Creates the accounts and transactions tables with strict domain constraints
-- =====================================================================================

-- Enable the pgcrypto extension to generate cryptographically secure UUID v4 identifiers
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- =====================================================================================
-- Table: accounts
-- Represents an economic entity capable of holding balanced ledger entries.
-- Note: Notice there is NO 'balance' column here to prevent race condition overwrites!
-- =====================================================================================
CREATE TABLE accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    account_number VARCHAR(32) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    -- Constraints
    CONSTRAINT uq_accounts_account_number UNIQUE (account_number),
    CONSTRAINT chk_accounts_currency CHECK (currency IN ('USD', 'EUR', 'GBP', 'INR')),
    CONSTRAINT chk_accounts_status CHECK (status IN ('ACTIVE', 'FROZEN', 'CLOSED'))
);

-- Index for high-speed account lookup by account number
CREATE INDEX idx_accounts_account_number ON accounts(account_number);

-- =====================================================================================
-- Table: transactions
-- Represents the business intent/event bundling balanced debit and credit ledger legs.
-- =====================================================================================
CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    idempotency_key VARCHAR(128) NOT NULL,
    description VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    posted_at TIMESTAMPTZ,

    -- Constraints
    CONSTRAINT uq_transactions_idempotency_key UNIQUE (idempotency_key),
    CONSTRAINT chk_transactions_status CHECK (status IN ('PENDING', 'SETTLED', 'FAILED', 'REVERSED'))
);

-- Index for sub-millisecond idempotency deduplication checks
CREATE INDEX idx_transactions_idempotency_key ON transactions(idempotency_key);
-- Index for querying transaction status and timelines
CREATE INDEX idx_transactions_created_at ON transactions(created_at DESC);