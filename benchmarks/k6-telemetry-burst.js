import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

// Custom Metrics for GridOps Telemetry
export const errorRate = new Rate('telemetry_error_rate');
export const ingestionLatency = new Trend('telemetry_ingestion_duration', true);
export const totalPayloads = new Counter('telemetry_total_ingested');

export const options = {
  stages: [
    { duration: '30s', target: 50 },    // Warm-up ramp: 0 to 50 VUs
    { duration: '1m', target: 200 },     // Normal daylight telemetry: 200 VUs (~2,000 req/s)
    { duration: '30s', target: 500 },    // Peak solar noon burst: 500 VUs (~10,000 req/s)
    { duration: '1m', target: 500 },     // Sustained peak load
    { duration: '30s', target: 0 },      // Ramp down
  ],
  thresholds: {
    'http_req_duration': ['p(95)<50', 'p(99)<100'], // P95 < 50ms, P99 < 100ms
    'telemetry_error_rate': ['rate<0.01'],          // Less than 1% error rate
    'telemetry_ingestion_duration': ['p(95)<30'],   // DB write + Redis cache write < 30ms
  },
};

const BASE_URL = __ENV.TARGET_URL || 'http://localhost:8080';

// Generate synthetic solar array telemetry data payload
function generateTelemetryPayload(vuId, iter) {
  const assetId = `ARRAY-${(vuId % 20) + 1}`;
  const baseIrradiance = 850 + (Math.sin(iter / 10) * 150);
  const cellTemp = 35 + (Math.random() * 10);
  
  return JSON.stringify({
    assetId: assetId,
    timestamp: new Date().toISOString(),
    metrics: {
      solarIrradiance: Math.max(0, baseIrradiance),
      panelTemperature: cellTemp,
      stringVoltage: 680.5 + (Math.random() * 20),
      stringCurrent: 45.2 + (Math.random() * 5),
      activePowerOutputMW: 32.4 + (Math.random() * 4),
      batterySOCPercent: 88.5 + (Math.random() * 5),
    },
    metadata: {
      inverterFirmware: 'v3.4.1',
      gridFrequencyHz: 50.02,
    }
  });
}

export default function () {
  const payload = generateTelemetryPayload(__VU, __ITER);
  const params = {
    headers: {
      'Content-Type': 'application/json',
      'X-Client-Version': 'k6-loadtest',
    },
    timeout: '5s',
  };

  const startTime = new Date().getTime();
  const res = http.post(`${BASE_URL}/api/v1/telemetry/ingest`, payload, params);
  const duration = new Date().getTime() - startTime;

  ingestionLatency.add(duration);
  totalPayloads.add(1);

  const isSuccess = check(res, {
    'status is 200 or 202 (Accepted)': (r) => r.status === 200 || r.status === 202,
    'response has validation id': (r) => r.json('ingestionId') !== undefined,
  });

  errorRate.add(!isSuccess);
  sleep(0.1); // 100ms throttle between sensor ticks
}
