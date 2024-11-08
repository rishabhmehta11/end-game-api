package com.sensor.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sensor.api.dto.ApiMetric;
import com.sensor.api.model.metrics.Metric;
import com.sensor.api.service.MetricService;
import com.sensor.api.util.JsonFileReader;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/metrics")
@Slf4j
public class MetricController {

	@Autowired
	private MetricService metricService;

	@Autowired
	private JsonFileReader jsonFileReader;

	// Using Json file to Pre loading some data
	private static final String SENSOR_DATA = "sensorData.json";

	// Specifying list of availabe statistics
	private static final List<String> AVAILABLE_STATISTICS = new ArrayList<>() {
		{
			add("min");
			add("max");
			add("sum");
			add("average");
		}
	};

	// An addtional controller endpoint for bulk loading some data to get and
	// compute statistics for temprory purpose only
	@PostMapping("/data/preload")
	public ResponseEntity<List<Metric>> PreLoadMetricData() {
		List<Metric> metric = jsonFileReader.LoadJsonDataIntoTable(SENSOR_DATA, Metric.class);
		return ResponseEntity.ok(metricService.saveMultipleMetricRecords(metric));
	}

	// Endpoint for receiving new data
	@PostMapping("/data/new")
	public ResponseEntity<Metric> saveMetric(@RequestBody Metric metricRequest) {
		return ResponseEntity.ok(metricService.saveMetricRecord(Metric.builder()
				.id(metricRequest.getId())
				.sensorId(metricRequest.getSensorId())
				.metricType(metricRequest.getMetricType())
				.value(metricRequest.getValue())
				.dateUTC(metricRequest.getDateUTC())
				.dateUTCMillis(metricRequest.getDateUTC().toInstant().toEpochMilli())
				.build()));
	}

	// Endpoint to get data, sensorId, metricType, and statistic are string type and
	// requried input
	// numberOfDays range from one day to one month and it's an optional input field
	@GetMapping("/data/get")
	public ResponseEntity<List<ApiMetric>> fetchMetrics(
			@RequestParam(value = "sensorIds", required = true) List<String> sensorIds,
			@RequestParam(value = "metricTypes", required = true) List<String> metricTypes,
			@RequestParam(value = "statistic", required = true) String statistic,
			@RequestParam(value = "numberOfDays", required = false) Integer numberOfDays) {

		if (numberOfDays != null && (numberOfDays > 31 || numberOfDays < 1)) {
			String message = String.format(
					"Invalid Number of day: Data requested for %s day which exceeds the limit. Number of Days can be between one day and a month",
					numberOfDays);
			log.error(message);

			return ResponseEntity.badRequest().build();
		}
		if (!AVAILABLE_STATISTICS.contains(statistic)) {
			String message = String.format(
					"Invalid Statistic variable: Statistic entered is not listed. List of available statistics: %s",
					AVAILABLE_STATISTICS);
			log.error(message);

			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(metricService.getMetricsData(sensorIds, metricTypes, statistic, numberOfDays));
	}

}
