package com.thomsonreuters.metrics.log;

import com.netflix.appinfo.ApplicationInfoManager;

public class LogMetricsPublisherConfig {

	private ApplicationInfoManager appInfoManager;

	public LogMetricsPublisherConfig() {
		appInfoManager = ApplicationInfoManager.getInstance();
	}

	public ApplicationInfoManager getAppInfoManager() {
		return appInfoManager;
	}

	public void setAppInfoManager(ApplicationInfoManager appInfoManager) {
		this.appInfoManager = appInfoManager;
	}
}
