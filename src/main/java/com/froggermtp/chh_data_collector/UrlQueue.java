package com.froggermtp.chh_data_collector;

import java.util.ArrayDeque;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a queue for storing the URLs that will be visited by the web crawler.
 * <p>
 * In order to ensure that a URL is not processed twice, the queue also tracks which URLs have
 * already been visited.
 */
public class UrlQueue {
	private static final Logger logger = LoggerFactory.getLogger(UrlQueue.class);
	
	/** 
	 * Contains all the URLs that will be processed by the web crawler.
	 */
	private ArrayDeque<String> urlsToCrawl = new ArrayDeque<>();
	/** 
	 * Contains all the URLs that have already been processed by the web crawler.
	 */
	private HashSet<String> visitedUrls = new HashSet<>();
	
	/**
	 * Adds a URL to the queue if, and only if, it has not already been processed.
	 * 
	 * @param url  the URL to add to the queue, not null
	 */
	public void addUrl(String url) {
		if (url == null) {
			throw new NullPointerException("Url cannot be null");
		}
		
		if (!visitedUrls.contains(url) && !urlsToCrawl.contains(url)) {
			urlsToCrawl.add(url);
			
			logger.debug("Url added to queue: {}", url);
		} else {
			logger.debug("Url already in queue: {}", url);
		}
	}
	
	/**
	 * Returns the next URL from the queue.
	 * 
	 * @return the next URL in the queue, null if queue empty
	 */
	public String getUrl()  {
		if (!urlsToCrawl.isEmpty()) {
			String url = urlsToCrawl.poll();
			visitedUrls.add(url);
			
			return url;
		}
		
		return null;
	}
	
	/**
	 * @return true if queue is empty, otherwise returns false
	 */
	public boolean isEmpty() {
		return urlsToCrawl.isEmpty();
	}
	
}
