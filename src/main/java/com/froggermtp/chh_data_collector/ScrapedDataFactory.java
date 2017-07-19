package com.froggermtp.chh_data_collector;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScrapedDataFactory {
	private static final Logger logger = LoggerFactory.getLogger(ScrapedDataFactory.class);
	
	public static ScrapedData getScrapedData(String type, Document doc) {
		if("MusicData".equalsIgnoreCase(type)) {
			logger.debug("Creating new MusicData");
			
			MusicData musicData = new MusicData(
					doc.location(),
					RapzillaHelper.scrapeProject(doc),
					RapzillaHelper.scrapeArtist(doc),
					RapzillaHelper.scrapeDate(doc)
					);
			
			logger.debug("{}", musicData.toString());
			
			return musicData;
		}
		
		logger.debug("Factory failed to create an object for type: {}", type);
		
		return null;
	}
}
