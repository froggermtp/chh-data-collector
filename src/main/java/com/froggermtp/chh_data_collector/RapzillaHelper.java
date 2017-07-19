package com.froggermtp.chh_data_collector;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RapzillaHelper {
	private static final Logger logger = LoggerFactory.getLogger(RapzillaHelper.class);
	
	private RapzillaHelper() {
		
	}
	
	public static String scrapeArtist(Document doc) {
		logger.debug("Entering scrapeArtist()");
		
		Elements titleElements = doc.select("title");
		
		if(titleElements.size() != 1) {
			logger.debug("Multiple title elements were scraped: {}" + titleElements.toString());
			
			logger.debug("Leaving scrapeArtist(): null");
			return null;
		}

		String titleString = titleElements.first().toString();
		titleString = StringHelper.replaceDashes(titleString);
		
		if(titleString.contains("-")) {
			String artistString = 
					titleString.replaceAll("(<title>Free Download:\\s+)|(\\s*-.*)", "");
			
			logger.debug("Leaving scrapeArtist(): {}", artistString);
			return artistString;
		}
		
		logger.debug("The title tag did not contain a dash: {}" + titleString);
		
		logger.debug("Leaving scrapeArtist(): null");
		return null;
	}
	
	public static String scrapeProject(Document doc) {
		logger.debug("Entering scrapeProject()");
		
		Elements titleElements = doc.select("title");
		
		if(titleElements.size() != 1) {
			logger.debug("Multiple title elements were scraped: {}", titleElements.toString());
			
			logger.debug("Leaving scrapeProject(): null");
			return null;
		}
		
		String titleString = titleElements.first().toString();
		titleString = StringHelper.replaceDashes(titleString);
		
		if(titleString.contains("-")) {
			String projectNameString = titleString.replaceAll("(.*-\\s*)|(\\s*</title>)", "");
			
			logger.debug("Leaving scrapeProject(): {}", projectNameString);
			return projectNameString;
		}
		
		logger.debug("The title tag did not contain a dash: {}", titleString);
		
		logger.debug("Leaving scrapeProject(): null");
		return null;
	}
	
	public static String scrapeDate(Document doc) {
		logger.debug("Entering scrapeDate()");
		
		Elements timeElements = doc.select("time");
		
		if(timeElements.size() != 1) {
			logger.debug("Multiple time elements were scraped: {}", timeElements.toString());
			
			logger.debug("Leaving scrapeDate(): null");
			return null;
		}
		
		String dateString = timeElements.html().replaceAll("Created:\\s*", "");
		
		logger.debug("Leaving scrapeDate(): {}", dateString);
		return dateString;
	}
}
