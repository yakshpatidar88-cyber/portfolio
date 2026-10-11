
package com.ledgerlock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * =====================================================================================
 * LedgerLock: High-Throughput Distributed Financial Ledger & Double-Entry Accounting Engine
 * =====================================================================================
 * 
 * Entry point for the LedgerLock application.
 * 
 * Architectural Highlights:
 * 1. Java 21+ Virtual Threads enabled for massive concurrent I/O throughput.
 * 2. Immutable double-entry bookkeeping enforced at the PostgreSQL engine level.
 * 3. Zero-update security guarantees tamper-proof auditability.
 * 4. Flyway-managed schema migrations.
 */
@SpringBootApplication
public class LedgerLockApplication {

    private static final Logger log = LoggerFactory.getLogger(LedgerLockApplication.class);

    public static void main(String[] args) {
        log.info("Starting LedgerLock Financial Ledger Engine with Java 21+ Virtual Threads...");
        SpringApplication.run(LedgerLockApplication.class, args);
        log.info("LedgerLock Engine successfully started and ready for high-throughput transactions.");
    }
}