# End Game Sensor API

## Prerequisites

[*] Postgres SQL
[*] Maven
[*] API uses JDK17 environment.
[*] Postman (optional)

## Name

Sensor API

## Description

This is a RESTful API for managing and querying weather sensor data. The API allows you to store weather metrics (e.g., temperature, humidity, wind speed) reported from different sensors and provides an endpoint to query this data.
You can query specific metrics over a date range (between one day and a month) and calculate statistics such as average, min, max, or sum for each metric.

## Features

- **Receive New Metrics**: Receive and store new metric data to DB.
- **Get Metrics Data**: Query data from one or more sensors, specifying the metric type (temperature, humidity, windSpeed), statistics (e.g., min, max, sum, average), and date range (between one day and a month).

## Running Locally

You will need to set an local postgres database to store and retrive the data. Once postgres is set up you can use /metrics/preload controller to prepoulate some data for test/demo purpose. The test json file contains data ranging from 24-10-2024 till 08-11-2024 to help compute statistics

```bash
mvn spring-boot:run
```
