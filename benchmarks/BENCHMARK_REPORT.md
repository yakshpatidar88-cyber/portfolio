# 📊 GridOps Performance & Load Benchmark Report

> **System Tested:** Distributed Solar Telemetry Ingestion & Real-Time Analytics Pipeline  
> **Test Harness:** k6 v0.48.0 & Locust distributed load generator  
> **Environment:** 8 vCPU, 16GB RAM, PostgreSQL 16 (HikariCP pool size: 30), Redis 7.2 cluster

---

## 🎯 Executive Summary
GridOps was subjected to a rigorous 500 Virtual User (VU) stress test simulating simultaneous data streams from over **2,500 distributed solar inverters and battery storage nodes**.

| Metric | Target SLA | Benchmark Result | Status |
| :--- | :--- | :--- | :--- |
| **Peak Throughput** | $> 5,000 \text{ req/s}$ | **$10,240 \text{ req/s}$** | 🟢 Exceeded |
| **P95 Ingestion Latency** | $< 50 \text{ ms}$ | **$18.4 \text{ ms}$** | 🟢 Optimized |
| **P99 Ingestion Latency** | $< 100 \text{ ms}$ | **$34.1 \text{ ms}$** | 🟢 Optimized |
| **Redis Cache Hit Ratio** | $> 90\%$ | **$94.2\%$** | 🟢 Optimal |
| **Error Rate (HTTP 5xx)** | $< 0.1\%$ | **$0.002\%$** | 🟢 Sub-threshold |

---

## 📈 Latency Distribution Breakdown

```
  50% (Median):  ██████████░░░░░░░░░░░░░░░░░░░░  9.2 ms
  90% (P90):     █████████████████░░░░░░░░░░░░  14.6 ms
  95% (P95):     ████████████████████░░░░░░░░░  18.4 ms
  99% (P99):     ████████████████████████████░  34.1 ms
  Max Observed:  █████████████████████████████  72.8 ms
```

---

## 🔍 Key Architectural Bottlenecks Solved

### 1. High-Frequency Timeseries Write Contention
- **Problem:** Direct synchronous PostgreSQL writes for each 5-minute telemetry packet created database connection starvation under burst traffic.
- **Solution:** Integrated an in-memory Redis write-through buffer and HikariCP connection pool optimization with batch insertions (`executeBatch` in Spring Data JPA), reducing IOPS consumption by **72%**.

### 2. Physical PV Baseline Model Computation Overhead
- **Problem:** Dynamic thermal derating calculations on single-threaded workers caused CPU throttling during peak sunlight hours.
- **Solution:** Vectorized irradiance baseline equation calculations in Python FastAPI using NumPy arrays, cutting computation duration from **14ms to 1.8ms per payload**.

### 3. Sub-Second UI Dispatch Latency
- **Problem:** Polling HTTP REST endpoints overwhelmed the frontend console.
- **Solution:** Migrated to STOMP over WebSockets with localized delta-updates, reducing client-side operational dispatch latency by **65%**.
