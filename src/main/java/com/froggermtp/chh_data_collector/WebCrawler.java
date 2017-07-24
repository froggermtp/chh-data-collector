package com.froggermtp.chh_data_collector;

import org.jsoup.nodes.Document;

public abstract class WebCrawler {
	public boolean isRunning = true;
	
	public boolean shouldVisit(String url) {
		return true;
	}
	
	public void onVisit(Document doc) {
		// Do nothing by default
	}
	
	public void stop() {
		isRunning = false;
	}
}
