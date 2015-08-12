package com.thomsonreuters.metrics.log;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.codahale.metrics.Clock;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Timer;

public class LogMetricsReporter extends ScheduledReporter {
	
	private static final Logger log = Logger.getLogger(LogMetricsReporter.class);

	private MetricFilter filter;
	private LogMetricsPublisherConfig config = new LogMetricsPublisherConfig();
	private Map<String, Long> previousValues = new HashMap<String, Long>();

	protected LogMetricsReporter(MetricRegistry registry,
			Clock clock,
			TimeUnit rateUnit,
			TimeUnit durationUnit,
			MetricFilter filter) {
		super(registry, "log", filter, rateUnit, durationUnit);
		this.filter = filter;
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

		public LogMetricsReporter build() throws Exception {
			return new LogMetricsReporter(registry, clock, rateUnit, durationUnit, filter);
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
		Long gaugeValue = (Long) gauge.getValue();
		Long previousValue = previousValues.get(name);
		
		if (previousValue == null || !previousValue.equals(gaugeValue)) {
			log.info(new HystrixMetricLog(new Date(timestamp), name, config.getAppInfoManager().getInfo().getAppName(), config.getAppInfoManager().getInfo().getId(), gaugeValue));
			previousValues.put(name, gaugeValue);
		}
	}
}

