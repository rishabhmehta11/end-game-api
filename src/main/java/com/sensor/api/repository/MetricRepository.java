package com.sensor.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sensor.api.model.metrics.Metric;

@Repository
public interface MetricRepository extends JpaRepository<Metric, Long> {
	List<Metric> findBySensorIdInAndMetricTypeInAndDateUTCMillisBetween(
			List<String> sensorIds, List<String> metricTypes, long startTimeStamp, long endTimeStamp);

	Optional<Metric> findTopBySensorIdAndMetricTypeOrderByDateUTCMillisDesc(String sensorIds, String metricTypes);
}
