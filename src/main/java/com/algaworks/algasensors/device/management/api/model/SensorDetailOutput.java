package com.algaworks.algasensors.device.management.api.model;

import io.hypersistence.tsid.TSID;

public record SensorDetailOutput(
        SensorOutput sensor,
        SensorMonitoringOutput monitoring
) {
}
