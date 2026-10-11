-- =====================================================================================
-- Migration: V3__enforce_ledger_immutability.sql
-- Description: Enforces database-level append-only immutability on ledger_entries.
--              Any UPDATE or DELETE attempt triggers a fatal exception.
-- =====================================================================================

-- 1. Create the trigger function that raises a fatal exception on any mutation attempt
CREATE OR REPLACE FUNCTION prevent_ledger_entry_mutation()
RETURNS TRIGGER AS $$
BEGIN
    -- Raise an unrecoverable exception with SQLSTATE '28000' (Invalid Authorization/Policy Violation)
    RAISE EXCEPTION 'CRITICAL SECURITY VIOLATION: Ledger entries are append-only and strictly immutable. UPDATE and DELETE operations are permanently prohibited on table ledger_entries.'
        USING ERRCODE = '28000';
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- 2. Attach the trigger to ledger_entries for all UPDATE and DELETE events
CREATE TRIGGER trg_protect_ledger_entries_immutability
BEFORE UPDATE OR DELETE ON ledger_entries
FOR EACH ROW
EXECUTE FUNCTION prevent_ledger_entry_mutation();

-- 3. Document table security policy via PostgreSQL catalog comment
COMMENT ON TABLE ledger_entries IS 'CORE IMMUTABLE FINANCIAL LEDGER: Protected by trg_protect_ledger_entries_immutability trigger. Modifications and deletions are strictly rejected.';