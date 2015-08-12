package com.thomsonreuters.metrics.log;

import java.util.Date;

import org.json.JSONObject;

public class HystrixMetricLog {
	
	private Date timestamp;
	private String metric;
	private String serviceName;
	private String instanceId;
	private Number value;
	
	public HystrixMetricLog(Date timestamp, String metric, String serviceName, String instanceId, Number value) {
		this.timestamp = timestamp;
		this.metric = metric;
		this.serviceName = serviceName;
		this.instanceId = instanceId;
		this.value = value;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public Number getValue() {
		return value;
	}

	public void setValue(Number value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return new JSONObject(this).toString();
	}
}
