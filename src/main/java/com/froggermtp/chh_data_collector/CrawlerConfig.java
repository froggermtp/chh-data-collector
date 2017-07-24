package com.froggermtp.chh_data_collector;

import java.util.ArrayList;
import java.util.List;

public class CrawlerConfig {
	private List<String> seedUrls = new ArrayList<>();
	private boolean followExternalLinks = true;
	private boolean scrapeSeedUrls = true;
	
	/**
	 * @return the seedUrls
	 */
	public List<String> getSeedUrls() {
		return seedUrls;
	}
	/**
	 * @param seedUrls the seedUrls to set
	 */
	public void setSeedUrls(List<String> seedUrls) {
		this.seedUrls = seedUrls;
	}
	
	public void addSeedUrls(String... seedUrls) {
		for(String seedUrl : seedUrls) {
			this.seedUrls.add(seedUrl);
		}
	}
	
	/**
	 * @return the followExternalLinks
	 */
	public boolean shouldFollowExternalLinks() {
		return followExternalLinks;
	}
	/**
	 * @param followExternalLinks the followExternalLinks to set
	 */
	public void setFollowExternalLinks(boolean followExternalLinks) {
		this.followExternalLinks = followExternalLinks;
	}
	/**
	 * @return the scrapeSeedUrls
	 */
	public boolean shouldScrapeSeedUrls() {
		return scrapeSeedUrls;
	}
	/**
	 * @param scrapeSeedUrls the scrapeSeedUrls to set
	 */
	public void setScrapeSeedUrls(boolean scrapeSeedUrls) {
		this.scrapeSeedUrls = scrapeSeedUrls;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("CrawlerConfig [seedUrls=")
		.append(seedUrls)
		.append(", followExternalLinks=")
		.append(followExternalLinks)
		.append(", scrapeSeedUrls=")
		.append(scrapeSeedUrls)
		.append("]");
		
		return builder.toString();
	}
	
}
