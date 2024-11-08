package com.sensor.api.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sensor.api.dto.ApiMetric;
import com.sensor.api.model.metrics.Metric;
import com.sensor.api.repository.MetricRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MetricService {

	@Autowired
	private MetricRepository metricRepository;

	public List<Metric> saveMultipleMetricRecords(List<Metric> metrics) {
		return metricRepository.saveAll(metrics);
	}

	public Metric saveMetricRecord(Metric metric) {
		return metricRepository.save(metric);
	}

	public List<ApiMetric> getMetricsData(List<String> sensorIds, List<String> metricTypes, String statistic,
			Integer numberOfDays) {

		List<Metric> metrics = new ArrayList<>();
		if (numberOfDays == null) {
			for (String sensorId : sensorIds) {
				for (String metricType : metricTypes) {
					metricRepository.findTopBySensorIdAndMetricTypeOrderByDateUTCMillisDesc(sensorId, metricType)
							.ifPresent(metrics::add);
				}
			}
		} else {
			Long startDateTimestamp = LocalDateTime.now().minusDays(numberOfDays).toInstant(ZoneOffset.UTC)
					.toEpochMilli();
			Long endDateTimeStamp = LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli();
			metrics = metricRepository.findBySensorIdInAndMetricTypeInAndDateUTCMillisBetween(sensorIds,
					metricTypes, startDateTimestamp, endDateTimeStamp);

		}

		/*
		 * As of now the result will is computed based on the statistics and metricType
		 * of each sensor
		 * Each sensor is counted as individual data source and result shows statics for
		 * each sensor
		 * It can be changed to get data from all sensors, count it as one and get the
		 * statistic.
		 * For doing that we will just need to remove the groupingBy sensorID
		 */
		return metrics.stream()
				.collect(Collectors.groupingBy(Metric::getSensorId,
						Collectors.groupingBy(Metric::getMetricType,
								Collectors.mapping(Metric::getValue, Collectors.toList()))))
				.entrySet().stream()
				.flatMap(sensorEntry -> sensorEntry.getValue().entrySet().stream().map(metricEntry -> {
					String sensorId = sensorEntry.getKey();
					String metricType = metricEntry.getKey();
					List<Double> values = metricEntry.getValue();
					Double resultValue = calculateStatistic(values, statistic);
					return new ApiMetric(sensorId, metricType, statistic, resultValue);
				}))
				.toList();

	}

	private Double calculateStatistic(List<Double> values, String statistic) {
		if (statistic.equalsIgnoreCase("min")) {
			return values.stream().min(Double::compareTo).orElse(null);
		}
		if (statistic.equalsIgnoreCase("max")) {
			return values.stream().max(Double::compareTo).orElse(null);
		}
		if (statistic.equalsIgnoreCase("sum")) {
			return values.stream().mapToDouble(Double::doubleValue).sum();
		}
		if (statistic.equalsIgnoreCase("average")) {
			return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
		}
		return null;
	}
}
