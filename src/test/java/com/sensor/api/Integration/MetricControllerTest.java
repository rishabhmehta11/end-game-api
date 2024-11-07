package com.sensor.api.Integration;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensor.api.model.metrics.Metric;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MetricControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        // metricRepository.deleteAll();
    }

    @Test
    public void testAddMetric() throws Exception {

        Metric request = Metric.builder().id("test_1").sensorId("sensor_3")
                .metricType("humidity").value(21.4)
                .timestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli()).build();

        mockMvc.perform(post("/metrics/data/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void testQueryMetrics() throws Exception {
        mockMvc.perform(get("/metrics/data/get")
                .param("sensorIds", "sensor_1")
                .param("metricTypes", "temperature")
                .param("statistic", "average"))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[{\"metricType\":\"temperature\",\"statistic\":\"average\",\"computedValue\":21.0,\"sensorId\":\"sensor_1\"}]"));
    }
}
