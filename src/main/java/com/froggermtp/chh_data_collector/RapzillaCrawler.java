package com.froggermtp.chh_data_collector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RapzillaCrawler extends WebCrawler {
	private static final Logger logger = LoggerFactory.getLogger(RapzillaCrawler.class);
	
	private static final Pattern URL_PATTERN = Pattern.compile(
			"http://www.rapzilla.com/rz/music/freemp3s/\\d+.");
	
	// TODO: Remove this
	private int tempCounter = 0;

	public RapzillaCrawler(String[] seedUrls) {
		super(seedUrls);
	}

	/* (non-Javadoc)
	 * @see com.froggermtp.chh_data_collector.WebCrawler#onVisit(org.jsoup.nodes.Document)
	 */
	@Override
	public void onVisit(Document doc) {
		super.onVisit(doc);
		
		// TODO: Remove this
		tempCounter++;
		
		if(tempCounter == 10) {
			stop();
		}
		
		if(shouldProcessUrl(doc.location())) {	
			ScrapedData musicData = ScrapedDataFactory.getScrapedData("MusicData", doc);
			
			logger.info("Scraped new music data: {}", musicData.toString());
		}
	}
	
	/**
	 * Determines if the html should be scraped for the wanted data.
	 * 
	 * @param url  the current url being visited by the web crawler
	 * @return true if the html should be scraped, otherwise returns false
	 */
	private boolean shouldProcessUrl(String url) {
		logger.debug("Entering shouldProcessUrl(url={})", url);
		
		Matcher match = URL_PATTERN.matcher(url);
		
		if(match.find()) {
			logger.debug("Leaving shouldProcessUrl(): true");
			
			return true;
		}
		else {
			logger.debug("Leaving shouldProcessUrl(): false");
			
			return false;
		}
	}
}
