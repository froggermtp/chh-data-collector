package com.froggermtp.chh_data_collector;

import java.util.ArrayDeque;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is a queue that contains all the urls that will be processed by the web crawler.
 * In order to ensure that a url is not processed twice, the queue also tracks which urls have
 * already been visited.
 */
public class UrlQueue {
	private static final Logger logger = LoggerFactory.getLogger(UrlQueue.class);
	
	/** Contains all the urls that will be processed by the web crawler.*/
	private ArrayDeque<String> urlsToCrawl = new ArrayDeque<>();
	
	/** Contains all the urls that have already been processed by the web crawler. */
	private HashSet<String> visitedUrls = new HashSet<>();
	
	/**
	 * Adds a url to the queue if, and only if, it has not already been processed.
	 * 
	 * @param url  the url to add to the queue, not null
	 */
	public void addUrl(String url) {
		if(url == null) {
			throw new NullPointerException("Url cannot be null");
		}
		
		if(!visitedUrls.contains(url)) {
			urlsToCrawl.add(url);
			
			logger.debug("Url added to queue: {}", url);
		}
		
		logger.debug("Url already in queue: {}", url);
	}
	
	/**
	 * Gets the next url from the queue.
	 * 
	 * @return the next url in the queue, null if queue empty
	 */
	public String getUrl()  {
		if(!urlsToCrawl.isEmpty()) {
			String url = urlsToCrawl.poll();
			visitedUrls.add(url);
			
			return url;
		}
		
		return null;
	}
	
	/**
	 * Returns true if the queue is empty.
	 * 
	 * @return true if queue is empty, otherwise returns false
	 */
	public boolean isEmpty() {
		return urlsToCrawl.isEmpty();
	}
	
}
