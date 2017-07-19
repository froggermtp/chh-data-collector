package com.froggermtp.chh_data_collector;

public abstract class ScrapedData {
	private final String url;
	
	public ScrapedData(String url) {
		this.url = url;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
}
