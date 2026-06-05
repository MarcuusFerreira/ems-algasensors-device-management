package com.algaworks.algasensors.device.management.api.controller;

import com.algaworks.algasensors.device.management.api.model.SensorInput;
import com.algaworks.algasensors.device.management.api.model.SensorOutput;
import com.algaworks.algasensors.device.management.common.IdGenerator;
import com.algaworks.algasensors.device.management.domain.model.Sensor;
import com.algaworks.algasensors.device.management.domain.model.SensorId;
import com.algaworks.algasensors.device.management.domain.repository.SensorRepository;
import io.hypersistence.tsid.TSID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sensors")
public class SensorController {

    private final SensorRepository sensorRepository;

    @GetMapping
    public ResponseEntity<Page<SensorOutput>> search(@PageableDefault Pageable pageable) {
        Page<Sensor> page = sensorRepository.findAll(pageable);
        return ResponseEntity.ok(page.map(this::convertToModel));
    }

    @GetMapping("{sensorId}")
    public ResponseEntity<SensorOutput> getOne(@PathVariable TSID sensorId) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return ResponseEntity.ok(convertToModel(sensor));
    }

    @PostMapping
    public ResponseEntity<SensorOutput> create(@RequestBody SensorInput input) {
        var sensor = Sensor.builder()
                .id(new SensorId(IdGenerator.generateTSID()))
                .name(input.name())
                .ip(input.ip())
                .location(input.location())
                .protocol(input.protocol())
                .model(input.model())
                .enabled(false)
                .build();
        sensor = sensorRepository.saveAndFlush(sensor);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        convertToModel(sensor)
                );
    }

    @PutMapping("{sensorId}")
    public ResponseEntity<SensorOutput> update(
            @PathVariable TSID sensorId,
            @RequestBody SensorInput input
    ) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        sensor.setName(input.name());
        sensor.setIp(input.ip());
        sensor.setLocation(input.location());
        sensor.setProtocol(input.protocol());
        sensor.setModel(input.model());

        sensor = sensorRepository.saveAndFlush(sensor);

        return ResponseEntity.ok(convertToModel(sensor));
    }

    @DeleteMapping("{sensorId}")
    public ResponseEntity<Void> delete(@PathVariable TSID sensorId) {
        sensorRepository.deleteById(new SensorId(sensorId));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{sensorId}/enable")
    public ResponseEntity<Void> enableSensor(@PathVariable TSID sensorId) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        sensor.setEnabled(true);
        sensorRepository.save(sensor);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{sensorId}/enable")
    public ResponseEntity<Void> disableSensor(@PathVariable TSID sensorId) {
        Sensor sensor = sensorRepository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        sensor.setEnabled(false);
        sensorRepository.saveAndFlush(sensor);
        return ResponseEntity.noContent().build();
    }

    private SensorOutput convertToModel(Sensor sensor) {
        return new SensorOutput(
                sensor.getId().getValue(),
                sensor.getName(),
                sensor.getIp(),
                sensor.getLocation(),
                sensor.getProtocol(),
                sensor.getModel(),
                sensor.getEnabled()
        );
    }
}
