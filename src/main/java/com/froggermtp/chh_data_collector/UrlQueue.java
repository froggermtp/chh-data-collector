package com.froggermtp.chh_data_collector;

import java.util.ArrayDeque;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlQueue {
	private static final Logger logger = LoggerFactory.getLogger(UrlQueue.class);
	
	private ArrayDeque<String> urlsToCrawl = new ArrayDeque<>();
	private HashSet<String> visitedUrls = new HashSet<>();
	
	public void addUrl(String url) {
		if(!visitedUrls.contains(url)) {
			urlsToCrawl.add(url);
			
			logger.debug("Url added to queue: {}", url);
		}
	}
	
	public String getUrl()  {
		if(!urlsToCrawl.isEmpty()) {
			String url = urlsToCrawl.poll();
			visitedUrls.add(url);
			
			return url;
		}
		
		return null;
	}
	
	public boolean isEmpty() {
		return urlsToCrawl.isEmpty();
	}
	
}
