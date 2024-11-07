package com.sensor.api.util;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensor.api.controller.MetricController;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JsonFileReader {

	public <T> List<T> LoadJsonDataIntoTable(String jsonFileName, Class<T> clazz) {
		ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
				false);
		try {
			List<T> dummyData = mapper.readValue(getInputStream(jsonFileName),
					mapper.getTypeFactory().constructCollectionType(List.class, clazz));

			return dummyData;
		} catch (Exception e) {
			String msg = String.format("Error reading data from %s Json File", jsonFileName);
			log.error(msg, e);
		}
		return Collections.emptyList();
	}

	private InputStream getInputStream(String fileName) {
		return MetricController.class.getClassLoader().getResourceAsStream(fileName);
	}
}
