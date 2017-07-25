package com.froggermtp.chh_data_collector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a controller that bridges the pieces of the web crawler together.
 * <p>
 * The controller bridges together the configuration and the public api of the web crawler.
 */
public class WebCrawlerController {
	private static final Logger logger = LoggerFactory.getLogger(WebCrawlerController.class);
	
	/** 
	 * Stores all the links scraped and retrieves the next URL to visit.
	 */
	private final UrlQueue urlQueue = new UrlQueue();
	/** 
	 * Holds the configuration for the web crawler.
	 */
	private final CrawlerConfig config;
	/** 
	 * The public api for the web crawler.
	 */
	private final WebCrawler crawler;
	
	/** 
	 * Keeps track of the total amount of links that are visited by the web crawler.
	 */
	private long totalLinksVisited = 0;
	
	public WebCrawlerController(CrawlerConfig config, WebCrawler crawler) {
		this.config = config;
		this.crawler = crawler;
		
		// Populate the urlQueue initially with the seed urls
		config.getSeedUrls().forEach(urlQueue::addUrl);
	}
	
	/**
	 * Starts the web crawler.
	 * <p>
	 * The web crawler will continue running until it runs out of links to process, or the public
	 * api arbitrarily causes it to stop.
	 */
	public void crawl() {
		logger.info("Starting the web crawler...");
		logger.info("Seed urls: {}", config.getSeedUrls().toString());
		
		while (!urlQueue.isEmpty()) {
			String urlToCrawl = urlQueue.getUrl();
			Document doc = getDocument(urlToCrawl);
			
			// If the connection fails, then skip scraping the document
			if (doc == null) {
				continue;
			}

			totalLinksVisited++;
			
			if (!config.shouldScrapeSeedUrls() && config.getSeedUrls().contains(urlToCrawl)) {
				logger.info("Not scraping seed url : {}", urlToCrawl);
			} else {
				crawler.onVisit(doc);
			}
			

			if (!crawler.isRunning()) {
				break;
			}

			List<String> links = getLinks(doc);

			links
			.stream()
			.filter(this::shouldVisit)
			.filter(crawler::shouldVisit)
			.forEach(urlQueue::addUrl);
		}

		logger.info("The web crawler has finished");
	}
	
	/**
	 * Delays the application for {@code TIME_DELAY} milliseconds.
	 * <p>
	 * The delay is courtesy so that the website is not overloaded with requests from the crawler.
	 */
	private void sleep() {
		final int TIME_DELAY = 1000;
		
		logger.debug("Sleeping for {} milliseconds", TIME_DELAY);
		
		try {
			Thread.sleep(TIME_DELAY);
		} catch (InterruptedException e) {
			logger.error("Thread was interrupted", e);
		}
	}
	
	/**
	 * Returns a {@code Document} for the given URL.
	 * <p>
	 * If {@code JSoup} cannot connect the server in {@code TIMEOUT} milliseconds,
	 * then the connection will timeout, and {@code null} will be returned.
	 * 
	 * @param urlToCrawl  the URL for which the {@code Document} will be fetched, not null
	 * @return the {@code Document}, null if connection fails
	 */
	private Document getDocument(String urlToCrawl) {
		final int TIMEOUT = 3000;
		
		sleep();
		
		try {
			Document doc = Jsoup.connect(urlToCrawl).timeout(TIMEOUT).get();
			
			return doc;
		} catch (IOException e) {
			logger.error("JSoup failed to connect to the url {}", urlToCrawl, e);
			
			return null;
		}
	}
	
	/**
	 * Returns a list of all the links for the given {@link Document}.
	 * 
	 * @param doc  the document in which to scrape the links, not null
	 * @return a list of all the links in the {@code Document}, not null
	 */
	private List<String> getLinks(Document doc) {
		return doc.select("a[href]")
				.stream()
				.map(d -> d.attr("abs:href"))
				.collect(Collectors.toList());
	}
	
	/**
	 * Determines if the web crawler should visit a URL.
	 * <p>
	 * A link is only processed if it meets these three conditions:
	 * <p><ul>
	 * <li>It has a valid extension.
	 * <li>The URL should be followed due to it being an external link.
	 * <li>It must be a valid URL.
	 * <ul>
	 * 
	 * @param url  the URL that may or may not be visited
	 * @return true if the URL should be processed, otherwise returns false
	 */
	private boolean shouldVisit(String url) {
		logger.debug("Entering shouldVisit(url={})", url);
		
		if (shouldFollowLink(url) && hasValidExtension(url) && isValidUrl(url)) {
			logger.debug("Leaving shouldVisit(): true");
			return true;
		}
		
		logger.debug("Leaving shouldVisit(): false");
		return false;
	}
	
	/**
	 * Determines whether a URL has valid extension.
	 * <p>
	 * This prevents the web crawler from visiting URLs that do not point to html pages.
	 * Examples of URLs to avoid are those that point to things like css, images, sound files, etc.
	 * 
	 * @param url  the URL to validate, not null
	 * @return true if valid URL, otherwise returns false
	 */
	private boolean hasValidExtension(String url) {
		final String IGNORE_SUFFIX_PATTERN = ".*(\\.(css|js|gif|jpg|png|mp3|mp3|zip|gz))$";
		
		
		if (url.matches(IGNORE_SUFFIX_PATTERN)) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Determines whether the web crawler should follow a URL.
	 * <p>
	 * If a URL is an external URL, then it may or may not be followed.
	 * If the configuration set by {@link CrawlConfig} is set to follow external URLs, then any URL
	 * will be followed.
	 * Otherwise, only URLs that are not external will be followed by the web crawler.
	 * 
	 * @param url  the URL that may or may not be followed
	 * @return true if the URL should be followed, otherwise returns false
	 */
	private boolean shouldFollowLink(String url) {
		if (!config.shouldFollowExternalLinks() && isExternalLink(url)) {
			return false;
		}
			
		return true;
	}
	
	/**
	 * Determines whether the link is an external link or not.
	 * <p>
	 * If a link does not begin exactly as one of the seed URLs, then it is an external link.
	 * 
	 * @param url  the url to check, not null
	 * @return true if external link, otherwise returns false
	 */
	private boolean isExternalLink(String url) {
		return !config.getSeedUrls()
				.stream()
				.anyMatch(url::startsWith);
	}
	
	/**
	 * Validates whether a URL can visited.
	 * <p>
	 * This method uses {@link URL} to check if URLs are valid.
	 * If the {@code URL} can be instantiated and be converted to a URI using {@link URL#toURI()},
	 * then the URL is considered to be valid.
	 * If either of these operations fails, then URL is not considered to be valid.
	 * 
	 * @param url  the URL to be validated, not null
	 * @return true if the URL is valid, otherwise returns false
	 */
	private boolean isValidUrl(String url) {
		logger.debug("Entering isValidUrl(url={})", url);
		
		try {
			URL urlTest = new URL(url);
			urlTest.toURI();
		} catch (MalformedURLException | URISyntaxException | NullPointerException e) {
			logger.error("Url is not valid", e);
			logger.debug("Leaving isValidUrl(): false");
			return false;
		} 
		
		logger.debug("Leaving isValidUrl(): true");
		return true;
	}
	
	/**
	 * @return the total amount of links the web crawler has visited
	 */
	public long getTotalLinksVisited() {
		return totalLinksVisited;
	}
}
