package com.thomsonreuters.metrics.influxdb;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.netflix.hystrix.HystrixThreadPoolMetrics;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherThreadPool;

public class InfluxDbMetricsPublisherThreadPool implements HystrixMetricsPublisherThreadPool {

	private HystrixThreadPoolMetrics metrics;
	private MetricRegistry metricRegistry;
	private String prefix = "";
	private MetricFilter metricFilter = MetricFilter.ALL;
	
	public InfluxDbMetricsPublisherThreadPool(String prefix, MetricFilter metricFilter, HystrixThreadPoolMetrics metrics, MetricRegistry metricRegistry) {
		this.metrics = metrics;
		this.metricRegistry = metricRegistry;
		this.metricFilter = metricFilter;
		this.prefix = prefix;
	}
	
	@Override
	public void initialize() {
		
		metricRegistry.register(prefix + "threadpool_currentPoolSize", new Gauge<Long>(){
			@Override
			public Long getValue() {
				return metrics.getCurrentPoolSize().longValue();
			}
		});
		
		metricRegistry.register(prefix + "threadpool_currentActiveCount", new Gauge<Long>(){
			@Override
			public Long getValue() {
				return metrics.getCurrentActiveCount().longValue();
			}
		});
		
		metricRegistry.register(prefix + "threadpool_currentQueueSize", new Gauge<Long>(){
			@Override
			public Long getValue() {
				return metrics.getCurrentQueueSize().longValue();
			}
		});
		
		try {
			startReporter(metricRegistry);
		} catch (Exception e) {
			throw new RuntimeException("Could not start influx db metrics reporter", e);
		}
	}
	
	private InfluxDbMetricsReporter startReporter(MetricRegistry registry) throws Exception {

	    final InfluxDbMetricsReporter reporter = InfluxDbMetricsReporter
	            .forRegistry(registry)
	            .filter(metricFilter)
	            .build();

	    reporter.start(10, TimeUnit.SECONDS);
	    return reporter;
	}
}
