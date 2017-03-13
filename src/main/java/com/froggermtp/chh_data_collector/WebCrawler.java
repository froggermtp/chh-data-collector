package com.froggermtp.chh_data_collector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
	private static final Logger logger = Logger.getLogger(WebCrawler.class.getName());
	
	/**
	 * Sets timeout for {@code Jsoup#connect(String)}.
	 * <p>
	 * Might want to look at {@link Connection#timeout()}.
	 */
	private int timeout;
	
	/**
	 * If true the crawler will only visit a link if the URL begins exactly with one of the seed 
	 * URLs. 
	 * Otherwise the crawler will visit any URL.
	 */
	private boolean followExternalLinks;
	
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
	
	/**
	 * Contains all the URLs the crawler will visit (unless the crawler is stop prematurely).
	 * This queue is first populated with the {@code WebCrawler#seedUrls}. 
	 * Additional URLs are added by scraping links from each successive URL that is visited.
	 */
	private ArrayDeque<String> linksToCrawl;
	
	/**
	 * Contains all the URLs that have already been visited by the web crawler. 
	 * Any URL in this list will not be visited again. 
	 */
	private HashSet<String> visitedUrls;
	/**
	 * Any link that is scraped from a URL will not added to {@code WebCrawler#linksToCrawl} if it
	 * ends with any of the suffixes defined in the pattern.
	 * This means that URL cannot be a valid URL to visit.
	 * <p>
	 * See {@link WebCrawler#shouldVisit(String)}
	 */
	private final static Pattern IGNORE_SUFFIX_PATTERN = Pattern.compile(
			".*(\\.(css|js|gif|jpg|png|mp3|mp3|zip|gz))$");
	
	/**
	 * Keeps track of the total amount of links that are visited by the web crawler.
	 */
	private long totalLinksVisited;
	
	public WebCrawler(String[] seedUrls) {
		this.timeout = 3000;
		this.followExternalLinks = false;
		
		this.shouldCrawl = true;
		this.seedUrls = seedUrls;
		this.linksToCrawl = new ArrayDeque<>();
		this.visitedUrls = new HashSet<>();
		
		this.totalLinksVisited = 0;
		
		logger.log(Level.INFO, "seed urls are {0}", seedUrls.toString());
		
		for(String url : seedUrls) {
			linksToCrawl.add(url);
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
		logger.log(Level.INFO, "starting the web crawler...");
		
		while(!linksToCrawl.isEmpty()) {
			String urlToCrawl = linksToCrawl.poll();
			
			if(visitedUrls.add(urlToCrawl)) {
				try {
					Document doc = Jsoup.connect(urlToCrawl).timeout(timeout).get();
					ArrayList<Element> links = doc.select("a[href]");
					
					totalLinksVisited++;
					
					onVisit(doc);
					
					if(!shouldCrawl) {
						break;
					}
					
					for(Element link : links) {
						String absoluteHref = link.attr("abs:href");
						
						if(shouldVisit(absoluteHref)) {
							if(!visitedUrls.contains(absoluteHref)) {
								if(!linksToCrawl.contains(absoluteHref)) {
									linksToCrawl.add(absoluteHref);
									
									logger.log(Level.INFO, "added {0} to the queue", absoluteHref);
								}
							}
						}
					}
				}
				catch(IOException e) {
					logger.log(Level.SEVERE, e.toString(), e);
				}
			}
		}

		logger.log(Level.INFO, "the web crawler has finished");
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
		Matcher match = IGNORE_SUFFIX_PATTERN.matcher(url);
		boolean validUrl = isValidUrl(url);
		boolean followUrl = false;
		
		if(!followExternalLinks) {
			for(String seed : seedUrls) {
				if(url.startsWith(seed)) {
					followUrl = true;
				}
			}
		}
		else {
			followUrl = true;
		}
		
		return (!match.matches() && validUrl && followUrl);
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
		URL urlTest = null;
		
		try {
			urlTest = new URL(url);
		} catch (MalformedURLException e) {
			return false;
		}
		
		try {
			urlTest.toURI();
		} catch (URISyntaxException e) {
			return false;
		} catch(NullPointerException e) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Runs each time a URL is processed by the web crawler.
	 * <p>
	 * This method provides a reference to the {@link Document} currently being processed.
	 * 
	 * @param doc  the {@code Document} currently being processed by the web crawler
	 */
	private void onVisit(Document doc) {
		logger.log(Level.INFO, "currently visiting {0}", doc.location());
	}
	
	/**
	 * Stops the web crawler before it finishes processing all the links in 
	 * {@code WebCrawler#linksToCrawl}.
	 */
	public void stop() {
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
