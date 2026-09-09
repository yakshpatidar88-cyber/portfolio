import random
import time
from locust import HttpUser, task, between

class GridOpsTelemetryUser(HttpUser):
    """
    Locust Load Test Suite for GridOps Distributed Telemetry Ingestion
    Simulates concurrent solar farm SCADA gateways streaming timeseries data.
    """
    wait_time = between(0.05, 0.2)  # High-frequency 50ms - 200ms requests

    def on_start(self):
        self.site_id = f"SITE-{random.randint(100, 999)}"
        self.asset_ids = [f"INV-STR-{i:03d}" for i in range(1, 25)]

    @task(6)
    def stream_solar_telemetry(self):
        """Simulate high-frequency timeseries sensor metrics"""
        asset = random.choice(self.asset_ids)
        payload = {
            "siteId": self.site_id,
            "assetId": asset,
            "timestamp": int(time.time()),
            "telemetry": {
                "irradianceWm2": round(random.uniform(700.0, 1050.0), 2),
                "cellTempCelsius": round(random.uniform(28.0, 52.0), 2),
                "dcVoltageV": round(random.uniform(620.0, 710.0), 2),
                "dcCurrentA": round(random.uniform(35.0, 60.0), 2),
                "acPowerMw": round(random.uniform(15.0, 48.0), 2),
                "batterySoc": round(random.uniform(75.0, 99.0), 2),
            },
            "derateModelEnabled": True
        }
        self.client.post(
            "/api/v1/telemetry/ingest",
            json=payload,
            name="POST /api/v1/telemetry/ingest"
        )

    @task(2)
    def query_live_console_metrics(self):
        """Simulate operations room console polling active alerts and power generation"""
        self.client.get(
            f"/api/v1/analytics/sites/{self.site_id}/realtime",
            name="GET /api/v1/analytics/sites/:id/realtime"
        )

    @task(1)
    def evaluate_incident_prioritization(self):
        """Simulate incident prioritization algorithm invocation"""
        payload = {
            "siteId": self.site_id,
            "anomalyType": "INVERTER_STRING_DEVIATION",
            "observedPowerMw": 18.5,
            "expectedPowerMw": 32.0,
            "durationMinutes": 15
        }
        self.client.post(
            "/api/v1/incidents/prioritize",
            json=payload,
            name="POST /api/v1/incidents/prioritize"
        )
