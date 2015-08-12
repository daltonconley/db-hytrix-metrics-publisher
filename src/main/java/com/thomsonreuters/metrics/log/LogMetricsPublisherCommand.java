package com.thomsonreuters.metrics.log;

import java.util.concurrent.TimeUnit;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.netflix.hystrix.HystrixCommandMetrics;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisherCommand;
import com.netflix.hystrix.util.HystrixRollingNumberEvent;

public class LogMetricsPublisherCommand implements HystrixMetricsPublisherCommand {

	private HystrixCommandMetrics metrics;
	private MetricRegistry metricRegistry;
	private String prefix = "";
	private MetricFilter metricFilter = MetricFilter.ALL;
	
	public LogMetricsPublisherCommand(String prefix, MetricFilter metricFilter, HystrixCommandMetrics metrics, MetricRegistry metricRegistry) {
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
		
		metricRegistry.register(prefix + "requestCount", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return metrics.getHealthCounts().getTotalRequests();
			}
		});
		
		metricRegistry.register(prefix + "errorCount", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getHealthCounts().getErrorCount());
			}
		});
		
		metricRegistry.register(prefix + "errorPercentage", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getHealthCounts().getErrorPercentage());
			}
		});
		
		metricRegistry.register(prefix + "executionTime", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getExecutionTimeMean());
			}
		});
		
		metricRegistry.register(prefix + "rollingCountFailure", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getRollingCount(HystrixRollingNumberEvent.FAILURE));
			}
		});
		
		metricRegistry.register(prefix + "rollingCountSuccess", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getRollingCount(HystrixRollingNumberEvent.SUCCESS));
			}
		});
		
		metricRegistry.register(prefix + "rollingCountTimeout", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getRollingCount(HystrixRollingNumberEvent.TIMEOUT));
			}
		});
		
		metricRegistry.register(prefix + "currentConcurrentExecutionCount", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getCurrentConcurrentExecutionCount());
			}
		});
		
		metricRegistry.register(prefix + "latencyExecute_0", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getExecutionTimePercentile(0));
			}
		});
		
		metricRegistry.register(prefix + "latencyExecute_25", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getExecutionTimePercentile(25));
			}
		});
		
		metricRegistry.register(prefix + "latencyExecute_50", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getExecutionTimePercentile(50));
			}
		});
		
		metricRegistry.register(prefix + "latencyExecute_75", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getExecutionTimePercentile(75));
			}
		});
		
		metricRegistry.register(prefix + "latencyExecute_90", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getExecutionTimePercentile(90));
			}
		});
		
		metricRegistry.register(prefix + "latencyExecute_95", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getExecutionTimePercentile(95));
			}
		});
		
		metricRegistry.register(prefix + "latencyExecute_99", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getExecutionTimePercentile(99));
			}
		});
		
		metricRegistry.register(prefix + "latencyExecute_Average", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getExecutionTimeMean());
			}
		});
		
		metricRegistry.register(prefix + "latencyTotal_0", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getTotalTimePercentile(0));
			}
		});
		
		metricRegistry.register(prefix + "latencyTotal_25", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getTotalTimePercentile(25));
			}
		});
		
		metricRegistry.register(prefix + "latencyTotal_50", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getTotalTimePercentile(50));
			}
		});
		
		metricRegistry.register(prefix + "latencyTotal_75", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getTotalTimePercentile(75));
			}
		});
		
		metricRegistry.register(prefix + "latencyTotal_90", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getTotalTimePercentile(90));
			}
		});
		
		metricRegistry.register(prefix + "latencyTotal_95", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getTotalTimePercentile(95));
			}
		});
		
		metricRegistry.register(prefix + "latencyTotal_99", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getTotalTimePercentile(99));
			}
		});
		
		metricRegistry.register(prefix + "latencyTotal_Average", new Gauge<Long>() {
			@Override
			public Long getValue() {
				return new Long(metrics.getTotalTimeMean());
			}
		});
		
		try {
			startReporter(metricRegistry);
		} catch (Exception e) {
			throw new RuntimeException("Could not start elasticsearch db metrics reporter", e);
		}
	}
	
	private LogMetricsReporter startReporter(MetricRegistry registry) throws Exception {

	    final LogMetricsReporter reporter = LogMetricsReporter
	            .forRegistry(registry)
	            .filter(metricFilter)
	            .build();

	    reporter.start(10, TimeUnit.SECONDS);
	    return reporter;
	}
}
