package com.thomsonreuters.metrics.influxdb;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.config.ConfigurationManager;

public class InfluxDbMetricsPublisherConfig 
{
	private String dbHost = ConfigurationManager.getConfigInstance().getString("metrics.influxdb.host");
	private String dbUser = ConfigurationManager.getConfigInstance().getString("metrics.influxdb.user");
	private String dbPass = ConfigurationManager.getConfigInstance().getString("metrics.influxdb.pass");
	private String dbName = ConfigurationManager.getConfigInstance().getString("metrics.influxdb.database");
	private String dbSeries = ConfigurationManager.getConfigInstance().getString("metrics.influxdb.series");

	private ApplicationInfoManager appInfoManager = ApplicationInfoManager.getInstance();
	
	public String getDbHost() {
		return dbHost;
	}

	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPass() {
		return dbPass;
	}

	public void setDbPass(String dbPass) {
		this.dbPass = dbPass;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	
	public String getDbSeries() {
		return dbSeries;
	}

	public void setDbSeries(String dbSeries) {
		this.dbSeries = dbSeries;
	}

	public ApplicationInfoManager getAppInfoManager() {
		return appInfoManager;
	}

	public void setAppInfoManager(ApplicationInfoManager appInfoManager) {
		this.appInfoManager = appInfoManager;
	}
}
