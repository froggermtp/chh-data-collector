package com.froggermtp.chh_data_collector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
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
public class WebCrawler {
	private static final Logger logger = LoggerFactory.getLogger(WebCrawler.class);
	
	/**
	 * If false the crawler will only visit a link if the URL begins exactly with one of the seed 
	 * URLs. 
	 * Otherwise the crawler will visit any URL.
	 */
	private final boolean FOLLOW_EXTERNAL_LINKS = false;;
	
	/**
	 * If true the web crawler will run as long as there are URLs to process. 
	 * Otherwise the web crawler will stop. 
	 * This value is only set to false by {@code WebCrawler#stop()}.
	 */
	private boolean shouldCrawl;
	
	/**
	 * A list containing the seed URLs for the web crawler. 
	 * The web crawler will start crawling by visiting these URLs.
	 */
	private String[] seedUrls;
	
	private UrlQueue urlQueue;
	
	/**
	 * Keeps track of the total amount of links that are visited by the web crawler.
	 */
	private long totalLinksVisited;
	
	public WebCrawler(String[] seedUrls) {
		this.shouldCrawl = true;
		this.seedUrls = seedUrls;
		this.urlQueue = new UrlQueue();
		
		this.totalLinksVisited = 0;
		
		logger.info("Seed urls are {}", Arrays.toString(seedUrls));
		
		for(String url : seedUrls) {
			urlQueue.addUrl(url);
		}
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
		
		while(!urlQueue.isEmpty()) {
			String urlToCrawl = urlQueue.getUrl();

			try {
				Document doc = getDocument(urlToCrawl);

				totalLinksVisited++;

				onVisit(doc);

				if(!shouldCrawl) {
					break;
				}

				List<String> links = getLinks(doc);

				links
				.stream()
				.filter(this::shouldVisit)
				.forEach(urlQueue::addUrl);
			}
			catch(IOException e) {
				logger.error("JSoup failed to connect to the url {}", urlToCrawl, e);
			}
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
	
	private Document getDocument(String urlToCrawl) throws IOException {
		final int TIMEOUT = 3000;
		
		sleep();
		
		return Jsoup.connect(urlToCrawl).timeout(TIMEOUT).get();
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
	 * <li>It must be a valid URL (See {@link WebCrawler#isValidUrl(String)}}
	 * <ul>
	 * 
	 * @param url  the URL that may or may not be processed
	 * @return true if the URL should be processed, otherwise returns false
	 */
	private boolean shouldVisit(String url) {
		logger.debug("Entering shouldVisit(url={})", url);
		
		final String IGNORE_SUFFIX_PATTERN = ".*(\\.(css|js|gif|jpg|png|mp3|mp3|zip|gz))$";
		
		boolean followUrl = false;
		
		if(!FOLLOW_EXTERNAL_LINKS) {
			for(String seed : seedUrls) {
				if(url.startsWith(seed)) {
					followUrl = true;
				}
			}
		}
		else {
			followUrl = true;
		}
				
		// These conditions are checked in this order for efficiency
		if(!followUrl) {	
			logger.debug("Leaving shouldVisit(): false");
			return false;
		}
		else if(url.matches(IGNORE_SUFFIX_PATTERN)) {
			logger.debug("Leaving shouldVisit(): false");
			return false;
		}
		else if(!isValidUrl(url)) {		
			logger.debug("Leaving shouldVisit(): false");
			return false;
		}
		
		logger.debug("Leaving shouldVisit(): true");
		return true;
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
	 * Runs each time a URL is processed by the web crawler.
	 * <p>
	 * This method provides a reference to the {@link Document} currently being processed.
	 * 
	 * @param doc  the {@code Document} currently being processed by the web crawler
	 */
	public void onVisit(Document doc) {
		logger.info("Currently visiting {}", doc.location());
	}
	
	/**
	 * Stops the web crawler before it finishes processing all the links in 
	 * {@code WebCrawler#linksToCrawl}.
	 */
	public void stop() {
		logger.info("The web crawler is stopping...");
		shouldCrawl = false;
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
