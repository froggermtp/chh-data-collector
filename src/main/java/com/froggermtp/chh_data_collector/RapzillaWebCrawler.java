package com.froggermtp.chh_data_collector;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the logic to scrape Rapzilla's music directory.
 */
public class RapzillaWebCrawler extends WebCrawler {
	private static final Logger logger = LoggerFactory.getLogger(RapzillaWebCrawler.class);
	
	// TODO: Remove this counter
	private int tempCounter = 0;

	@Override
	public boolean shouldVisit(String url) {
		logger.debug("Entering shouldVisit(url={})", url);
		
		if (url.matches("http://www.rapzilla.com/rz/music/freemp3s/\\d+.+")) {
			logger.debug("Leaving shouldVisit(): true");
			
			return true;
		}
		
		logger.debug("Leaving shouldVisit(): false");
		
		return false;
	}

	@Override
	public void onVisit(Document doc) {
		logger.info("Currenly visiting url: {}", doc.location());
		
		// TODO: Remove this counter
		tempCounter++;

		if (tempCounter == 10) {
			stop();
		}

		MusicData musicData = new MusicData(
				doc.location(), 
				scrapeProject(doc), 
				scrapeArtist(doc), 
				scrapeDate(doc)
				);

		logger.info("Scraped new music data: {}", musicData.toString());
	}
	
	/**
	 * Scrapes the artist name from the web page.
	 * 
	 * @param doc  the {@code Document} to scrape
	 * @return the artist name, null if name couldn't be found
	 */
	private String scrapeArtist(Document doc) {
		logger.debug("Entering scrapeArtist()");
		
		Elements titleElements = doc.select("title");
		
		if (titleElements.size() != 1) {
			logger.debug("Multiple title elements were scraped: {}" + titleElements.toString());
			
			logger.debug("Leaving scrapeArtist(): null");
			return null;
		}

		String titleString = titleElements.first().toString();
		/**
		 * There are two different kinds of dashes.
		 * All the dashes are converted to one type to simplify the logic.
		 */
		titleString = StringHelper.replaceDashes(titleString);
		titleString = StringHelper.replaceAmperstamp(titleString);
		
		if (titleString.contains("-")) {
			String artistString = 
					titleString.replaceAll("(<title>Free.*:\\s+)|(\\s*-.*)", "");
			
			logger.debug("Leaving scrapeArtist(): {}", artistString);
			return artistString;
		}
		
		logger.debug("The title tag did not contain a dash: {}" + titleString);
		
		logger.debug("Leaving scrapeArtist(): null");
		return null;
	}
	
	/**
	 * Scrapes the project name from the web page.
	 * 
	 * @param doc  the {@code Document} to scrape
	 * @return the project name, null if name couldn't be found
	 */
	private String scrapeProject(Document doc) {
		logger.debug("Entering scrapeProject()");
		
		Elements titleElements = doc.select("title");
		
		if (titleElements.size() != 1) {
			logger.debug("Multiple title elements were scraped: {}", titleElements.toString());
			
			logger.debug("Leaving scrapeProject(): null");
			return null;
		}
		
		String titleString = titleElements.first().toString();
		/**
		 * There are two different kinds of dashes.
		 * All the dashes are converted to one type to simplify the logic.
		 */
		titleString = StringHelper.replaceDashes(titleString);
		titleString = StringHelper.replaceAmperstamp(titleString);
		
		if (titleString.contains("-")) {
			String projectNameString = titleString.replaceAll("(.*-\\s*)|(\\s*</title>)", "");
			
			logger.debug("Leaving scrapeProject(): {}", projectNameString);
			return projectNameString;
		}
		
		logger.debug("The title tag did not contain a dash: {}", titleString);
		
		logger.debug("Leaving scrapeProject(): null");
		return null;
	}
	
	/**
	 * Scrape the date from the web page.
	 * 
	 * @param doc  the {@code Document} to scrape
	 * @return the date, null if the date couldn't be found
	 */
	private String scrapeDate(Document doc) {
		logger.debug("Entering scrapeDate()");
		
		Elements timeElements = doc.select("time");
		
		if (timeElements.size() != 1) {
			logger.debug("Multiple time elements were scraped: {}", timeElements.toString());
			
			logger.debug("Leaving scrapeDate(): null");
			return null;
		}
		
		String dateString = timeElements.html().replaceAll("Created:\\s*", "");
		
		logger.debug("Leaving scrapeDate(): {}", dateString);
		return dateString;
	}

}
