package com.sensor.api.model.metrics;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "metric")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Metric {
	@Id
	private String id;

	private String sensorId;
	private String metricType;
	private Double value;
	private Long timestamp;

}
