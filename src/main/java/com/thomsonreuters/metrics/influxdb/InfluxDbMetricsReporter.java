package com.thomsonreuters.metrics.influxdb;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Serie;

import com.codahale.metrics.Clock;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Timer;

public class InfluxDbMetricsReporter extends ScheduledReporter {
	
	private static final Logger log = Logger.getLogger(InfluxDbMetricsReporter.class);
	
	private InfluxDB influxDb;
	private MetricFilter filter;
	private InfluxDbMetricsPublisherConfig config;
	private Map<String, Long> previousValues = new HashMap<String, Long>();

	protected InfluxDbMetricsReporter(MetricRegistry registry, Clock clock, TimeUnit rateUnit, TimeUnit durationUnit, MetricFilter filter) {
		super(registry, "influxdb", filter, rateUnit, durationUnit);
		this.filter = filter;
		
		config = new InfluxDbMetricsPublisherConfig();
		influxDb = InfluxDBFactory.connect(config.getDbHost(), config.getDbUser(), config.getDbPass());
	}
	
	public static Builder forRegistry(MetricRegistry registry) {
		return new Builder(registry);
	}

	public static class Builder {
		private final MetricRegistry registry;
		private Clock clock;
		private TimeUnit rateUnit;
		private TimeUnit durationUnit;
		private MetricFilter filter;

		private Builder(MetricRegistry registry) {
			this.registry = registry;
			this.clock = Clock.defaultClock();
			this.rateUnit = TimeUnit.SECONDS;
			this.durationUnit = TimeUnit.MILLISECONDS;
			this.filter = MetricFilter.ALL;
		}

		public Builder filter(MetricFilter filter) {
			this.filter = filter;
			return this;
		}

		public InfluxDbMetricsReporter build() {
			return new InfluxDbMetricsReporter(registry, clock, rateUnit, durationUnit, filter);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void report(SortedMap<String, Gauge> gauges,
			SortedMap<String, Counter> counters,
			SortedMap<String, Histogram> histograms,
			SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
		
		for (Map.Entry<String, Gauge> entry : gauges.entrySet()) {
			String name = entry.getKey();
			
			if (filter.matches(name, null)) {
				reportGauge(entry.getKey(), entry.getValue(), Clock.defaultClock().getTime());
			}
		}
	}
	
	private void reportGauge(String name, Gauge<?> gauge, Long timestamp) {
		Serie serie = new Serie.Builder("hystrix/gauge/" + name + "/" + config.getAppInfoManager().getInfo().getAppName() + "/" + config.getAppInfoManager().getInfo().getId())
		.columns("timestamp", "service", "instance", "value")
		.values(timestamp, config.getAppInfoManager().getInfo().getAppName(), config.getAppInfoManager().getInfo().getId(), gauge.getValue())
		.build();
		
		Long gaugeValue = (Long) gauge.getValue();
		
		// we don't want to record metrics that haven't changed.
		Long previousValue = previousValues.get(name);
		if (previousValue == null || !previousValue.equals(gauge.getValue())) {
			log.info("Publishing changed metric [" + name + "] with value [" + gauge.getValue() + "]");
			influxDb.write(config.getDbName(), TimeUnit.MILLISECONDS, serie);
			previousValues.put(name, gaugeValue);
		}
	}
}

