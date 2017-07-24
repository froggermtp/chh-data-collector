package com.froggermtp.chh_data_collector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point
 */
public class App {
	private static final Logger logger = LoggerFactory.getLogger(App.class);
	
	public static void main( String[] args ) {	
		String[] seedUrls = {"http://www.rapzilla.com/rz/music/freemp3s/"};
		
		CrawlerConfig config = new CrawlerConfig();
		config.addSeedUrls(seedUrls);
		config.setFollowExternalLinks(false);
		config.setScrapeSeedUrls(false);
    	
        WebCrawlerController webcrawler = new WebCrawlerController(config, new RapzillaWebCrawler());
        webcrawler.crawl();
        
        logger.info("Total links visited : {}", webcrawler.getTotalLinksVisited());
    }
}
