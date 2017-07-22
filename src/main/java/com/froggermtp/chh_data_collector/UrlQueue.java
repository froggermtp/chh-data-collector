package com.froggermtp.chh_data_collector;

import java.util.ArrayDeque;
import java.util.HashSet;

public class UrlQueue {
	private ArrayDeque<String> urlsToCrawl;
	private HashSet<String> visitedUrls;
	
	public UrlQueue() {
		this.urlsToCrawl = new ArrayDeque<>();
		this.visitedUrls = new HashSet<>();
	}
	
	public void addUrl(String url) {
		if(!visitedUrls.contains(url)) {
			urlsToCrawl.add(url);
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
