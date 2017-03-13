package com.froggermtp.chh_data_collector;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main entry point
 */
public class App {
	private static final Logger logger = Logger.getLogger(App.class.getName());
	
	public static void main( String[] args ) {	
		String[] seedUrls = {"http://www.rapzilla.com/rz/music/freemp3s/download-list"};
    	
        WebCrawler webcrawler = new WebCrawler(seedUrls);
        webcrawler.crawl();
        
        logger.log(Level.INFO, "total links visited : {0}", webcrawler.getTotalLinksVisited());
    }
}
