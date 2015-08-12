package com.thomsonreuters.metrics.elastic;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.config.ConfigurationManager;

public class ElasticMetricsPublisherConfig {
	
	private String dbHost = ConfigurationManager.getConfigInstance().getString("metrics.elastic.host");
	private ApplicationInfoManager appInfoManager;

	public ElasticMetricsPublisherConfig() {
		appInfoManager = ApplicationInfoManager.getInstance();
	}
	
	public String getDbHost() {
		return dbHost;
	}

	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	public ApplicationInfoManager getAppInfoManager() {
		return appInfoManager;
	}

	public void setAppInfoManager(ApplicationInfoManager appInfoManager) {
		this.appInfoManager = appInfoManager;
	}
}
