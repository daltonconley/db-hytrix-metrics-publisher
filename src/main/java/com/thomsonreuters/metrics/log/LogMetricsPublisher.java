package com.thomsonreuters.metrics.log;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.netflix.hystrix.HystrixCircuitBreaker;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.HystrixThreadPoolMetrics;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherCommand;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherThreadPool;

public class LogMetricsPublisher extends HystrixMetricsPublisher {
	
	private MetricRegistry metricRegistry;
	private String metricPrefix;
	private MetricFilter metricFilter;
	
	public LogMetricsPublisher(String metricPrefix, MetricFilter metricFilter, MetricRegistry metricRegistry) {
		this.metricRegistry = metricRegistry;
		this.metricPrefix = metricPrefix;
		this.metricFilter = metricFilter;
	}
	
	@Override
	public HystrixMetricsPublisherCommand getMetricsPublisherForCommand(HystrixCommandKey commandKey, HystrixCommandGroupKey commandGroupKey, HystrixCommandMetrics metrics, HystrixCircuitBreaker circuitBreaker, HystrixCommandProperties properties) {
		return new LogMetricsPublisherCommand(metricPrefix, metricFilter, metrics, metricRegistry);
	}
	
	@Override
    public HystrixMetricsPublisherThreadPool getMetricsPublisherForThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixThreadPoolMetrics metrics, HystrixThreadPoolProperties properties) {
        return new LogMetricsPublisherThreadPool(metricPrefix, metricFilter, metrics, metricRegistry);
    }
}
