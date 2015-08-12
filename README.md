# Hystrix Publishing Library
A library to publish hystrix event stream metrics to an external data store.

All implementations use the codehale (dropwizard) metrics library. An instance of a `MetricRegistry` is required.

## Implementations
#### Elastic
Supports writing directly to an Elasticsearch cluster via the REST interface,
using Jest. Jest does this asychronously, so in theory this scales pretty well.

#### InfluxDB
Supports writing directly to an InfluxDB instance. This currently only supports
InfluxDB 0.8.x but upgrading should be simply.

#### Log files
Allows you to write Hystrix streams to log files. This uses log4j. The idea is
to use **Logstash** to support writing to some other data store.

## Examples
    ElasticMetricsPublisher publisher
      = new ElasticMetricsPublisher("prefix.", null, new MetricRegistry());
    HystrixPlugins.getInstance().registeryMetricsPublisher(publisher);

This example uses no filter (the null value).

Some libraries will inject additional metrics, outside of what these implementations provide (such as the spring-cloud libs). The use of a filter allows you to filter these out. In the following filter example, we filter only the metrics that have names that start with **_prefix_**.

    public class PrefixNameMetricFilter implements MetricFilter {

      @Override
      public boolean matches(String name, Metric metric) {
        if (name.startsWith("prefix")) {
            return true;
          }
          return false;
      }
    }

And injecting the filter...

    ElasticMetricsPublisher publisher
      = new ElasticMetricsPublisher("prefix.",
          new PrefixNameMetricFilter(), new MetricRegistry());
    HystrixPlugins.getInstance().registeryMetricsPublisher(publisher);
