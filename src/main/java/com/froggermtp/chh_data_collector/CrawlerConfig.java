package com.froggermtp.chh_data_collector;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides the configuration settings for the web crawler.
 */
public class CrawlerConfig {
	/**
	 * The seed URLs for the web crawler.
	 */
	private List<String> seedUrls = new ArrayList<>();
	/**
	 * Determines whether the web crawler will follow external links.
	 * <p>
	 * If true, the crawler will follow all links.
	 * If false, the crawler will only follow the link if it's a child link of one of the seed URLs.
	 * A child link begins with exactly one of the seed URLs.
	 */
	private boolean followExternalLinks = true;
	/**
	 * Determines whether the web crawler will process the seed URLS.
	 * <p>
	 * If false, then the crawler will scrape the seed URLs for links to process, but won't process
	 * the actual seed URLs themselves.
	 */
	private boolean scrapeSeedUrls = true;
	
	/**
	 * @return the seedUrls, not null
	 */
	public List<String> getSeedUrls() {
		return seedUrls;
	}
	
	/**
	 * Adds a seed URL to initialize the web crawler.
	 * <p>
	 * Multiple seeds can be added to the web crawler.
	 * 
	 * @param url  the URL to use as a seed, not null
	 * @return true if the URL was added, otherwise returns false
	 */
	public boolean addSeedUrl(String url) {
		return seedUrls.add(url);
	}
	
	/**
	 * Remove a seed URL from the web crawler.
	 * 
	 * @param url  the URL to remove, not null
	 * @return true if the seed was removed, otherwise returns false
	 */
	public boolean removeSeedUrl(String url) {
		return seedUrls.remove(url);
	}
	
	/**
	 * @return the followExternalLinks, not null
	 */
	public boolean shouldFollowExternalLinks() {
		return followExternalLinks;
	}
	/**
	 * Sets whether the web crawler will follow external links.
	 * <p>
	 * If true, the crawler will follow all links.
	 * If false, the crawler will only follow the link if it's a child link of one of the seed URLs.
	 * A child link begins with exactly one of the seed URLs.
	 * 
	 * @param followExternalLinks the followExternalLinks to set
	 */
	public void setFollowExternalLinks(boolean followExternalLinks) {
		this.followExternalLinks = followExternalLinks;
	}
	/**
	 * @return the scrapeSeedUrls, not null
	 */
	public boolean shouldScrapeSeedUrls() {
		return scrapeSeedUrls;
	}
	/**
	 * Sets whether the web crawler will process the seed URLS.
	 * <p>
	 * If false, then the crawler will scrape the seed URLs for links to process, but won't process
	 * the actual seed URLs themselves.
	 * 
	 * @param scrapeSeedUrls the scrapeSeedUrls to set, not null
	 */
	public void setScrapeSeedUrls(boolean scrapeSeedUrls) {
		this.scrapeSeedUrls = scrapeSeedUrls;
	}

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
