package com.thomsonreuters.metrics.elastic;

import org.apache.commons.configuration.AbstractConfiguration;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.config.ConfigurationManager;

public class ElasticMetricsPublisherConfig {
	
	private String dbHost;
	private ApplicationInfoManager appInfoManager;

	public ElasticMetricsPublisherConfig() {
		AbstractConfiguration configInstance = ConfigurationManager.getConfigInstance();
		configInstance.setThrowExceptionOnMissing(false);
		
		dbHost = configInstance.getString("metrics.elastic.host");
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
