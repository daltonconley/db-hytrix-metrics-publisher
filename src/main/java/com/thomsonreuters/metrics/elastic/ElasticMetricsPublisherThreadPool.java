package com.thomsonreuters.metrics.elastic;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.netflix.hystrix.HystrixThreadPoolMetrics;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherThreadPool;

public class ElasticMetricsPublisherThreadPool implements HystrixMetricsPublisherThreadPool {

	private HystrixThreadPoolMetrics metrics;
	private MetricRegistry metricRegistry;
	private String prefix = "";
	private MetricFilter metricFilter = MetricFilter.ALL;
	
	public ElasticMetricsPublisherThreadPool(String prefix, MetricFilter metricFilter, HystrixThreadPoolMetrics metrics, MetricRegistry metricRegistry) {
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
		
		metricRegistry.register(prefix + "threadpool.currentPoolSize", new Gauge<Number>(){
			@Override
			public Number getValue() {
				return (Integer) metrics.getCurrentPoolSize();
			}
		});
		
		metricRegistry.register(prefix + "threadpool.currentQueueSize", new Gauge<Number>(){
			@Override
			public Number getValue() {
				return (Integer) metrics.getCurrentQueueSize();
			}
		});
		
		try {
			startInfluxdbReporter(metricRegistry);
		} catch (Exception e) {
			throw new RuntimeException("Could not start elasticsearch db metrics reporter", e);
		}
	}
	
	private ElasticMetricsReporter startInfluxdbReporter(MetricRegistry registry) throws Exception {

	    final ElasticMetricsReporter reporter = ElasticMetricsReporter
	            .forRegistry(registry)
	            .filter(metricFilter)
	            .build();

	    reporter.start(5, TimeUnit.SECONDS);
	    return reporter;
	}

}
