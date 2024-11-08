package com.sensor.api.Unit;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.sensor.api.dto.ApiMetric;
import com.sensor.api.model.metrics.Metric;
import com.sensor.api.repository.MetricRepository;
import com.sensor.api.service.MetricService;

@SpringBootTest
public class MetricServiceTest {

	@InjectMocks
	private MetricService metricService;

	@Mock
	private MetricRepository metricRepository;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void TestSaveMetricData() {
		Metric metric = Metric.builder().id("unit_test_1").sensorId("sensor_3")
				.metricType("humidity").value(25.4)
				.dateUTC(new Date(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()))
				.dateUTCMillis(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()).build();

		when(metricRepository.save(any(Metric.class))).thenReturn(metric);

		Metric newMetric = metricService.saveMetricRecord(metric);

		assertNotNull(newMetric);
		assertEquals("sensor_3", newMetric.getSensorId());
		assertNotEquals("temperature", newMetric.getMetricType());
		assertEquals(25.4, newMetric.getValue());
	}

	@Test
	public void TestFetchMetricData() {
		List<Metric> mockMetricData = List.of(
				Metric.builder().id("test_metric_1").sensorId("sensor_3")
						.metricType("temperature").value(21.4)
						.dateUTC(new Date(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()))
						.dateUTCMillis(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli())
						.build(),
				Metric.builder().id("test_metric_2").sensorId("sensor_3")
						.metricType("temperature").value(22.5)
						.dateUTC(new Date(LocalDateTime.now().minusHours(12).toInstant(ZoneOffset.UTC).toEpochMilli()))
						.dateUTCMillis(LocalDateTime.now().minusHours(12).toInstant(ZoneOffset.UTC).toEpochMilli())
						.build(),
				Metric.builder().id("test_metric_3").sensorId("sensor_3")
						.metricType("temperature").value(25.5)
						.dateUTC(new Date(LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli()))
						.dateUTCMillis(LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli())
						.build(),
				Metric.builder().id("test_metric_4").sensorId("sensor_3")
						.metricType("humidity").value(55.0)
						.dateUTC(new Date(LocalDateTime.now().minusHours(2).toInstant(ZoneOffset.UTC).toEpochMilli()))
						.dateUTCMillis(LocalDateTime.now().minusHours(2).toInstant(ZoneOffset.UTC).toEpochMilli())
						.build());

		List<String> sensorIds = List.of("sensor_3");
		List<String> metricTypes = List.of("temperature");
		String statistic = "average";

		// Mock repository call
		when(metricRepository.findBySensorIdInAndMetricTypeInAndDateUTCMillisBetween(
				anyList(), anyList(), anyLong(), anyLong())).thenReturn(mockMetricData);

		List<ApiMetric> responses = metricService.getMetricsData(sensorIds, metricTypes, statistic, 2);

		// Validate
		assertEquals(2, responses.size());

		Map<String, Double> expectedResults = Map.of(
				"temperature", (21.4 + 22.5 + 25.5) / 3,
				"humidity", 55.0);

		for (ApiMetric apiMetric : responses) {
			Double expectedValue = expectedResults.get(apiMetric.getMetricType());
			assertEquals(expectedValue, apiMetric.getComputedValue());
			assertEquals(statistic, apiMetric.getStatistic());
		}

	}
}
