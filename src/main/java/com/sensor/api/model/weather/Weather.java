package com.sensor.api.model.weather;

import lombok.Data;

@Data
public class Weather {
	private Double temperature;
	private Double humidity;
	private Double windSpeed;
}
