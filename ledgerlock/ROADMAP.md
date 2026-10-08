# 🗺️ LedgerLock: 14-Day Distributed Systems Roadmap

> Tracking 42–56 Semantic Commits across 14 Days.

---

## Day 1: Architecture, Docker Infrastructure & Domain Design
- [x] **Commit 1.1** `build: initialize maven coordinates and core dependency BOM`
- [x] **Commit 1.2** `ci(docker): configure multi-container postgres 16, redis 7, and kafka cluster`
- [x] **Commit 1.3** `docs(arch): add architecture decisions log and living roadmap`
- [ ] **Commit 1.4** `feat(domain): draft formal ledger specifications and entity models`

---

## Day 2: PostgreSQL Flyway Schema & Zero-Update Security
- [ ] **Commit 2.1** `feat(db): create flyway migration for accounts and transactions`
- [ ] **Commit 2.2** `feat(db): add ledger_entries schema with composite timeseries indexes`
- [ ] **Commit 2.3** `feat(db): enforce database-level immutability (revoke update/delete)`
- [ ] **Commit 2.4** `test(db): verify flyway migration execution and zero-update constraints`

---

## Day 3: JPA Domain Entities & Double-Entry Invariants
- [ ] **Commit 3.1** `feat(entity): implement Account and Transaction JPA entities`
- [ ] **Commit 3.2** `feat(entity): implement immutable LedgerEntry with zero-drift constraint`
- [ ] **Commit 3.3** `test(unit): build double-entry invariant validator (sum debits == credits)`

---

## Day 4: Spring Data JPA & Query Optimizations
- [ ] **Commit 4.1** `feat(repo): create account repository with pessimistic write locks`
- [ ] **Commit 4.2** `feat(repo): create ledger entry repository with balance summation`
- [ ] **Commit 4.3** `feat(dto): create transfer request/response DTOs with RFC-7807 problem details`

---

## Day 5: Deadlock-Free Concurrency Lock Manager
- [ ] **Commit 5.1** `feat(concurrency): implement deterministic account ordering lock algorithm`
- [ ] **Commit 5.2** `feat(concurrency): configure redisson distributed lock manager`
- [ ] **Commit 5.3** `test(concurrency): verify deterministic ordering breaks coffman circular wait`

---

## Day 6: Absolute Idempotency & Replay Protection
- [ ] **Commit 6.1** `feat(idempotency): build idempotency record entity and repo`
- [ ] **Commit 6.2** `feat(idempotency): implement multi-tier redis fast-path interceptor`
- [ ] **Commit 6.3** `feat(security): implement sha-256 payload fingerprinting against replays`

---

## Day 7: Core Transfer Service & Balance Mutator
- [ ] **Commit 7.1** `feat(service): implement atomic p2p wallet transfer orchestrator`
- [ ] **Commit 7.2** `test(stress): write junit 5 multi-threaded transfer race condition tests`
- [ ] **Commit 7.3** `test(stress): verify zero balance drift under 1,000 concurrent transfers`

---

## Day 8: Cryptographic SHA-256 Chaining & Tamper Auditor
- [ ] **Commit 8.1** `feat(audit): implement cryptographic sha-256 hash chaining on ledger entries`
- [ ] **Commit 8.2** `feat(audit): build automated ledger integrity auditor service`
- [ ] **Commit 8.3** `test(audit): simulate retroactive database tampering and assert detection`

---

## Day 9: CQRS Materializer & Balance Snapshot Engine
- [ ] **Commit 9.1** `feat(cqrs): implement redis balance projection read path (<2ms)`
- [ ] **Commit 9.2** `feat(cqrs): build periodic snapshotting worker (5000 transactions checkpoint)`
- [ ] **Commit 9.3** `test(cqrs): benchmark read-path latency vs postgres aggregated scan`

---

## Day 10: Transactional Outbox Pattern & Kafka Publisher
- [ ] **Commit 10.1** `feat(outbox): create transactional outbox table and repository`
- [ ] **Commit 10.2** `feat(outbox): implement CDC background publisher to kafka`
- [ ] **Commit 10.3** `feat(kafka): create ledger settlement audit consumer`

---

## Day 11: REST APIs, Swagger UI & Observability
- [ ] **Commit 11.1** `feat(api): create TransferController and AccountController`
- [ ] **Commit 11.2** `docs(api): configure springdoc openapi 3 / swagger ui`
- [ ] **Commit 11.3** `feat(metrics): add micrometer prometheus metrics for transfer latency and locks`

---

## Day 12: High-Concurrency Stress Testing & Benchmarks
- [ ] **Commit 12.1** `test(bench): build k6 / locust concurrent load test script (5,000 requests/sec)`
- [ ] **Commit 12.2** `docs(bench): record p50, p95, p99 latency benchmarks and zero-drift proof`

---

## Day 13: Real-Time Audit Console & Visual Explorer (Frontend)
- [ ] **Commit 13.1** `feat(ui): scaffold react 18 + tailwind with obsidian glassmorphic design system`
- [ ] **Commit 13.2** `feat(ui): build real-time transaction ledger feed with spring micro-interactions`
- [ ] **Commit 13.3** `feat(ui): implement interactive cryptographic hash chain tamper debugger`

---

## Day 14: CI/CD Pipeline & Placement Defense Documentation
- [ ] **Commit 14.1** `ci(actions): configure github actions automated test verification and docker build`
- [ ] **Commit 14.2** `docs(placement): publish comprehensive system design interview defense guide`
- [ ] **Commit 14.3** `docs(readme): polish production-ready github portfolio readme with architecture diagrams`