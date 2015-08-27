package com.thomsonreuters.metrics.elastic;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Clock;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ScheduledReporter;
import com.codahale.metrics.Timer;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Index;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.IndicesExists;


public class ElasticMetricsReporter extends ScheduledReporter {

	private MetricFilter filter;
	private ElasticMetricsPublisherConfig config = new ElasticMetricsPublisherConfig();
	private JestClient client;
	private Map<String, Long> previousValues = new HashMap<String, Long>();

	protected ElasticMetricsReporter(MetricRegistry registry,
			Clock clock,
			TimeUnit rateUnit,
			TimeUnit durationUnit,
			MetricFilter filter) {
		super(registry, "elastic", filter, rateUnit, durationUnit);
		this.filter = filter;
		
		if (config.getDbHost() != null) {
			
			JestClientFactory factory = new JestClientFactory();
			factory.setHttpClientConfig(new HttpClientConfig
				.Builder(config.getDbHost())
				.multiThreaded(true)
				.build());
			client = factory.getObject();
		}
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

		public ElasticMetricsReporter build() throws Exception {
			return new ElasticMetricsReporter(registry, clock, rateUnit, durationUnit, filter);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void report(SortedMap<String, Gauge> gauges,
			SortedMap<String, Counter> counters,
			SortedMap<String, Histogram> histograms,
			SortedMap<String, Meter> meters, SortedMap<String, Timer> timers) {
		
		if (client != null) {
			for (Map.Entry<String, Gauge> entry : gauges.entrySet()) {
				String name = entry.getKey();
				
				if (filter.matches(name, null)) {
					reportGauge(entry.getKey(), entry.getValue(), Clock.defaultClock().getTime());
				}
			}
		}
	}
	
	private void reportGauge(String name, Gauge<?> gauge, Long timestamp) {
		Long gaugeValue = ((Number) gauge.getValue()).longValue();
		Long previousValue = previousValues.get(name);
		
		if (previousValue == null || !previousValue.equals(gauge.getValue())) {
			try {
				SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
				String indexName = "hystrix-metrics-" + ft.format(new Date());
				
				createIndexIfDoesntExist(indexName);
				Map<String, Object> source = new HashMap<String, Object>();
				source.put("timestamp", new Date(timestamp));
				source.put("metric", name);
				source.put("serviceName", config.getAppInfoManager().getInfo().getAppName());
				source.put("instanceId", config.getAppInfoManager().getInfo().getId());
				source.put("value", gauge.getValue());
				
				Index index = new Index.Builder(source).index(indexName).type("metric").build();
				client.execute(index);
				
				previousValues.put(name, gaugeValue);
				
			} catch (IOException e) {
				//e.printStackTrace();
			}
		}
	}
	
	private boolean createIndexIfDoesntExist(String indexName) throws IOException {
		boolean indexExists = client.execute(new IndicesExists.Builder(indexName).build()).isSucceeded();
		
		if (!indexExists) { 
			client.execute(new CreateIndex.Builder(indexName).build());
		}
		
		return false;
	}
}

