CREATE TABLE metric(
  id VARCHAR PRIMARY KEY, sensor_id VARCHAR, metric_type VARCHAR, value double precision, timestamp TIMESTAMP WITHOUT TIME ZONE DEFAULT (NOW() AT TIME ZONE 'utc')
)
CREATE TABLE weatherData()
CREATE TABLE sensor()