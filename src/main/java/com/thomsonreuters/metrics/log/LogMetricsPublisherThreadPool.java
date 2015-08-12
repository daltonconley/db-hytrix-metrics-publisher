package com.thomsonreuters.metrics.log;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.netflix.hystrix.HystrixThreadPoolMetrics;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherThreadPool;

public class LogMetricsPublisherThreadPool implements HystrixMetricsPublisherThreadPool {

	private HystrixThreadPoolMetrics metrics;
	private MetricRegistry metricRegistry;
	private String prefix = "";
	private MetricFilter metricFilter = MetricFilter.ALL;
	
	public LogMetricsPublisherThreadPool(String prefix, MetricFilter metricFilter, HystrixThreadPoolMetrics metrics, MetricRegistry metricRegistry) {
		this.metrics = metrics;
		this.metricRegistry = metricRegistry;
		
		if (prefix != null) {
			this.prefix = prefix;
		}
		
		if (metricFilter != null) {
			this.metricFilter = metricFilter;
		}
	}
	
	
	@Override
	public void initialize() {
		
		metricRegistry.register(prefix + "threadpool.currentPoolSize", new Gauge<Long>(){
			@Override
			public Long getValue() {
				return metrics.getCurrentPoolSize().longValue();
			}
		});
		
		metricRegistry.register(prefix + "threadpool.currentQueueSize", new Gauge<Long>(){
			@Override
			public Long getValue() {
				return metrics.getCurrentQueueSize().longValue();
			}
		});
		
		try {
			startInfluxdbReporter(metricRegistry);
		} catch (Exception e) {
			throw new RuntimeException("Could not start elasticsearch db metrics reporter", e);
		}
	}
	
	private LogMetricsReporter startInfluxdbReporter(MetricRegistry registry) throws Exception {

	    final LogMetricsReporter reporter = LogMetricsReporter
	            .forRegistry(registry)
	            .filter(metricFilter)
	            .build();

	    reporter.start(5, TimeUnit.SECONDS);
	    return reporter;
	}

}
