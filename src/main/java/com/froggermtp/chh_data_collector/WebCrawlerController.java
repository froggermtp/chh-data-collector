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
 * This class is a simple web crawler built using {@link Jsoup}.
 * <p>
 * The web crawler has three basic operations:<ul>
 * <li>{@code WebCrawler#crawl()} which starts the web crawler
 * <li>{@code WebCrawler#stop()} which stops the web crawler
 * <li>{@code WebCrawler#onVisit(Document)} runs every time the crawler visits a URL
 * </ul>
 * The web crawler will begin crawling at the {@code WebCrawler#seedUrls} and will stop when
 * {@code WebCrawler#linksToCrawl} is empty or {@code WebCrawler#stop()} is called.
 * After a URL has been visited, it is added to {@code WebCrawler#visitedUrls} and will not be
 * visited again.
 */
public class WebCrawlerController {
	private static final Logger logger = LoggerFactory.getLogger(WebCrawlerController.class);
	
	private UrlQueue urlQueue = new UrlQueue();
	
	private WebCrawler crawler;
	
	private CrawlerConfig config;
	
	/**
	 * Keeps track of the total amount of links that are visited by the web crawler.
	 */
	private long totalLinksVisited = 0;
	
	public WebCrawlerController(CrawlerConfig config, WebCrawler crawler) {
		this.config = config;
		this.crawler = crawler;
		
		config.getSeedUrls().forEach(urlQueue::addUrl);
	}
	
	/**
	 * Starts the web crawler.
	 * <p>
	 * The crawler will run as long as there are URLs in {@code WebCrawler#linksToCrawl}.
	 * The crawler will stop prematurely if {@code WebCrawler#stop()} is called.
	 * Links are only added to {@code WebCrawler#linksToCrawl} if 
	 * {@code WebCrawler#shouldVisit(String)} returns true.
	 * After a URL has been visited, it is added to {@code WebCrawler#visitedUrls} and will not be
	 * visited again.
	 */
	public void crawl() {
		logger.info("Starting the web crawler...");
		logger.info("Seed urls: {}", config.getSeedUrls().toString());
		
		while(!urlQueue.isEmpty()) {
			String urlToCrawl = urlQueue.getUrl();
			Document doc = getDocument(urlToCrawl);

			if(doc == null) {
				continue;
			}

			totalLinksVisited++;
			
			if(!config.shouldScrapeSeedUrls() && config.getSeedUrls().contains(urlToCrawl)) {
				logger.info("Not scraping seed url : {}", urlToCrawl);
			}
			else {
				crawler.onVisit(doc);
			}
			

			if(!crawler.isRunning()) {
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
	
	private Document getDocument(String urlToCrawl) {
		final int TIMEOUT = 3000;
		
		sleep();
		
		try {
			Document doc = Jsoup.connect(urlToCrawl).timeout(TIMEOUT).get();
			
			return doc;
		}
		catch(IOException e) {
			logger.error("JSoup failed to connect to the url {}", urlToCrawl, e);
			
			return null;
		}
	}
	
	private List<String> getLinks(Document doc) {
		return doc.select("a[href]")
				.stream()
				.map(d -> d.attr("abs:href"))
				.collect(Collectors.toList());
	}
	
	/**
	 * Determines if the web crawler should process a URL.
	 * <p>
	 * A link is only processed if it meets these three conditions:<ul>
	 * <li>It doesn't end in any of the prefixes defined in {@code WebCrawler#IGNORE_SUFFIX_PATTERN}
	 * <li>If {@code WebCrawler#followExternalLinks} is true, the the link must start with one of 
	 * URLs in {@code seedUrls}
	 * <li>It must be a valid URL (See {@link WebCrawlerController#isValidUrl(String)}}
	 * <ul>
	 * 
	 * @param url  the URL that may or may not be processed
	 * @return true if the URL should be processed, otherwise returns false
	 */
	private boolean shouldVisit(String url) {
		logger.debug("Entering shouldVisit(url={})", url);
		
		if(shouldFollowLink(url) && hasValidExtension(url) && isValidUrl(url)) {
			logger.debug("Leaving shouldVisit(): true");
			
			return true;
		}
		
		logger.debug("Leaving shouldVisit(): false");
		
		return false;
	}
	
	private boolean hasValidExtension(String url) {
		final String IGNORE_SUFFIX_PATTERN = ".*(\\.(css|js|gif|jpg|png|mp3|mp3|zip|gz))$";
		
		return url.matches(IGNORE_SUFFIX_PATTERN) ? false : true;
	}
	
	private boolean shouldFollowLink(String url) {
		if(!config.shouldFollowExternalLinks() && isExternalLink(url)) {
			return false;
		}
			
		return true;
	}
	
	private boolean isExternalLink(String url) {
		return !config.getSeedUrls()
				.stream()
				.anyMatch(url::startsWith);
	}
	
	/**
	 * Validates whether a URL can be visited by the web crawler.
	 * <p>
	 * This method uses {@link URL} to check if URLs are valid.
	 * If the {@code URL} can be instantiated and be converted to a URI using {@link URL#toURI()},
	 * then the URL is considered to be valid.
	 * If either of these operations fails, then URL is not considered to be valid.
	 * 
	 * @param url  the URL to be validated
	 * @return true if the URL is valid, otherwise returns false
	 */
	private boolean isValidUrl(String url) {
		logger.debug("Entering isValidUrl(url={})", url);
		
		URL urlTest = null;
		
		try {
			urlTest = new URL(url);
		} catch (MalformedURLException e) {
			logger.debug("Leaving isValidUrl(): false");
			return false;
		}
		
		try {
			urlTest.toURI();
		} catch (URISyntaxException | NullPointerException e) {
			logger.debug("Leaving isValidUrl(): false");
			return false;
		}
		
		logger.debug("Leaving isValidUrl(): true");
		return true;
	}
	
	/**
	 * Gets the total amount of links the web crawler has visited.
	 * 
	 * @return the total amount of links the web crawler has visited
	 */
	public long getTotalLinksVisited() {
		return totalLinksVisited;
	}
}
