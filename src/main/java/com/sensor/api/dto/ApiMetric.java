package com.sensor.api.dto;

import lombok.Data;

@Data
public class ApiMetric {
	private String metricType;
	private String statistic;
	private Double computedValue;
	private String sensorId;

	public ApiMetric(String sensorId, String metricType, String statistic, Double computedValue) {
		this.metricType = metricType;
		this.computedValue = computedValue;
		this.statistic = statistic;
		this.sensorId = sensorId;
	}
}
