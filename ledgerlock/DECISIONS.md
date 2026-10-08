# 🏛️ LedgerLock Architecture Decision Records (ADR)

This document records the foundational architectural decisions, technical justifications, and tradeoffs for LedgerLock.

---

## ADR-001: Immutable Double-Entry Bookkeeping vs Mutable Balances

- **Status:** ACCEPTED
- **Date:** Day 1
- **Context:**  
  Conventional web applications manage balances using `UPDATE accounts SET balance = balance - 100 WHERE id = :id`. Under concurrent traffic, this causes double-spending race conditions, circular deadlocks, and permanently destroys auditability.
- **Decision:**  
  All balance mutations are strictly immutable, append-only ledger entries. Every business transaction consists of balanced debit and credit entries enforcing:
  $$\sum \text{Debits} - \sum \text{Credits} = 0.0000$$
  Rows in `ledger_entries` are NEVER updated or deleted. Database-level triggers and role permissions will explicitly `REVOKE UPDATE, DELETE`.
- **Consequences:**  
  - *Pros:* Mathematical guarantee of fund conservation; tamper-evident audit history; zero deadlocks on single-row balance mutations.
  - *Cons:* Calculating balance requires summing entries over time. (Mitigated via ADR-002 CQRS & Snapshotting).

---

## ADR-002: Heterogeneous Storage Architecture (PostgreSQL + Redis + Kafka)

- **Status:** ACCEPTED
- **Date:** Day 1
- **Context:**  
  A single database cannot efficiently handle strict ACID transactions, microsecond distributed mutex locking, and decoupled asynchronous event delivery at high throughput.
- **Decision:**  
  Adopt a heterogeneous data tier:
  1. **PostgreSQL 16:** Source of Truth. Handles append-only ledger entries, transactional foreign keys, and row-level pessimistic locks (`SELECT FOR UPDATE`).
  2. **Redis 7:** High-Speed Distributed Lock Manager (Redlock algorithm for distributed mutual exclusion) and CQRS Read-Cache (<2ms balance lookups).
  3. **Apache Kafka (KRaft mode):** Asynchronous transaction event streaming using the Transactional Outbox Pattern to decouple notification and audit consumers.
- **Consequences:**  
  - *Pros:* Optimized performance for each distinct workload; separation of concerns.
  - *Cons:* Distributed system complexity; requires idempotency keys and outbox tables to guarantee consistency.

---

## ADR-003: Concurrency Engine via Java 21+ Virtual Threads (Project Loom)

- **Status:** ACCEPTED
- **Date:** Day 1
- **Context:**  
  Traditional Java thread-per-request architecture uses OS platform threads. Each platform thread consumes ~1MB memory and incurs high OS context-switch overhead, limiting concurrency to a few thousand concurrent requests.
- **Decision:**  
  Enable Java 21+ Virtual Threads (`spring.threads.virtual.enabled=true`). Virtual threads are lightweight user-mode threads managed by the JVM, allowing LedgerLock to handle 10,000+ concurrent transfer requests without thread pool exhaustion.
- **Consequences:**  
  - *Pros:* Massive throughput on blocking I/O (database queries, Redis calls) without reactive framework complexity (like WebFlux/RxJava).
  - *Cons:* Requires avoiding `synchronized` pinning; standard thread-local variables must be used cautiously.

---

## ADR-004: UI/UX Design System & Anti-Slop Visual Philosophy

- **Status:** ACCEPTED
- **Date:** Day 1
- **Context:**  
  Most developer portfolio projects suffer from generic "AI slop"—monotonous purple/blue gradients, flat Bootstrap cards, and lifeless static tables.
- **Decision:**  
  The Day 13 Visual Console will follow a bespoke, tactile design system:
  1. **Palette:** Deep obsidian background (`#08090d`), frosted glass layers (`backdrop-blur-2xl`), emerald green `#10b981` (settled/balanced), crimson `#ef4444` (tamper/unbalanced).
  2. **Texture:** Liquid glassmorphism with 1px translucent borders (`rgba(255,255,255,0.08)`) and subtle neomorphic depth on interactive buttons.
  3. **Typography:** Monospaced tabular numerals (`Geist Mono` / `JetBrains Mono`) to eliminate visual jitter on live updating currency and hashes; `Inter` for interface labels.
  4. **Micro-Interactions:** Spring-physics entry animations for new ledger entries, interactive hash-chain tamper simulator, and real-time cryptographic verification badges.
- **Consequences:**  
  - *Pros:* Distinctive, professional FinTech executive dashboard feel that immediately stands out to hiring managers.
  - *Cons:* Requires thoughtful CSS styling and Framer Motion integration.