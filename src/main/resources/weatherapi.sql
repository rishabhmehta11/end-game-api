CREATE TABLE metric(
  id VARCHAR PRIMARY KEY, sensor_id VARCHAR, metric_type VARCHAR, value double precision, date_utc_millis BIGINT, date_utc TIMESTAMP WITHOUT TIME ZONE DEFAULT (NOW() AT TIME ZONE 'utc')
)