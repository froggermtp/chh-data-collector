package com.froggermtp.chh_data_collector;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RapzillaWebCrawler extends WebCrawler {
	private static final Logger logger = LoggerFactory.getLogger(RapzillaWebCrawler.class);
	
	private int tempCounter = 0;

	/* (non-Javadoc)
	 * @see com.froggermtp.chh_data_collector.WebCrawler#shouldVisit(java.lang.String)
	 */
	@Override
	public boolean shouldVisit(String url) {
		logger.debug("Entering shouldVisit(url={})", url);
		
		if(url.matches("http://www.rapzilla.com/rz/music/freemp3s/\\d+.+")) {
			logger.debug("Leaving shouldVisit(): true");
			
			return true;
		}
		
		logger.debug("Leaving shouldVisit(): false");
		
		return false;
	}

	/* (non-Javadoc)
	 * @see com.froggermtp.chh_data_collector.WebCrawler#onVisit(org.jsoup.nodes.Document)
	 */
	@Override
	public void onVisit(Document doc) {
		logger.info("Currenly visiting url: {}", doc.location());
		
		tempCounter++;

		if(tempCounter == 10) {
			stop();
		}

		ScrapedData musicData = ScrapedDataFactory.getScrapedData("MusicData", doc);

		logger.info("Scraped new music data: {}", musicData.toString());
	}

}
