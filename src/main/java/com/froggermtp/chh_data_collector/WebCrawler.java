package com.froggermtp.chh_data_collector;

import org.jsoup.nodes.Document;

/**
 * Provides the public api for the web crawler.
 * <p>
 * All of the methods in the class are expected to overridden as needed.
 */
public abstract class WebCrawler {
	private boolean isRunning = true;
	
	/**
	 * Determines whether a given URL should be visited by the web crawler.
	 * <p>
	 * By default, all URLs will be visited.
	 * 
	 * @param url  the URL to check, not null
	 * @return true if the URL should visited, otherwise returns false
	 */
	public boolean shouldVisit(String url) {
		return true;
	}
	
	/**
	 * Runs each time a URL is visited by the web crawler.
	 * <p>
	 * If a page should be scraped for information, the scraping logic should occur within this
	 * method.
	 * 
	 * @param doc  the {@code Document} that is currently be visited, not null
	 */
	public void onVisit(Document doc) {
		// Do nothing by default
	}
	
	/**
	 * Stops the web crawler arbitrarily.
	 * <p>
	 * This method can be used to stop the web crawler before it has run out of links to crawl.
	 */
	public void stop() {
		isRunning = false;
	}
	
	/**
	 * @return true if the web crawler is running, otherwise returns false
	 */
	public boolean isRunning() {
		return isRunning;
	}
}
