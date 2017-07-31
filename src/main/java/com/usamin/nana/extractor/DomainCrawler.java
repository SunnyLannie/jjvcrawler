package com.usamin.nana.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

//this crawls a domain, it first looks at that domains robot file
//it also accounts for politeness factor to avoid spamming server with too many requests
public class DomainCrawler extends CrawlerUniversal implements Comparable<DomainCrawler> {

	String hostname;
	LinkedList<String> undiscovered; // FIFO queue of the pages to crawl on this
										// domain
	long k = 50; // politeness factor
	Instant startNext; // start time of the next url download for this domain
	Instant prevFinished; // time when the last download on this domain was
							// finished
	Duration dlDuration; // duration of the last download
	CrawlExclusion robotstxt;

	public DomainCrawler(String url) {
		this.hostname = getHost(url);

		undiscovered = new LinkedList<String>();
		undiscovered.add(url);

		resetTime();
		dlDuration = Duration.ZERO;
		robotstxt = CrawlExclusion.getExclusions(url);
		// debugPrint(robotstxt.disallow.toString());
		// debugPrint(robotstxt.toString());
	}

	public DomainCrawler() {
		undiscovered = new LinkedList<String>();

		resetTime();
		dlDuration = Duration.ZERO;
	}

	public String getHostname() {
		return this.hostname;
	}

	public void resetTime() {
		startNext = Instant.now();
		prevFinished = Instant.now();
	}

	// obtains everything robots.txt says not to crawl
	public HashSet<?> getCrawlExclusion() {
		return robotstxt.disallow();
	}

	/***
	 * add url to FIFO queue, discard if it is not from the same domain
	 * initialize hostname if isn't already initialized
	 **/
	public void addURL(String url) {
		if (this.hostname != null) {
			if (!getHost(url).equals(this.hostname))
				return;
			undiscovered.add(removeSpace(url));
		} else {
			this.hostname = getHost(url);
			undiscovered.add(url);
		}

	}

	public String getNextCrawlUrl() {
		return undiscovered.peek();
	}

	public void removeNextCrawlUrl() {
		undiscovered.poll();
	}

	/** remove first element from FIFO queue and extract links found */
	public LinkedList<String> crawl() {

		timeout();
		resetTime();

		/** list to store undiscovered urls on this page */
		LinkedList<String> result = new LinkedList<String>();

		/**
		 * remove url from FIFO queue If FIFO is empty, just return nothing
		 */
		String url = null;
		while (url == null) {
			if (undiscovered.isEmpty())
				return null;
			url = undiscovered.poll();
			if (robotstxt.isExcluded(url)) {
				url = null;
			}
		}

		/** initializing httpclient components */
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response;
		System.err.println("url is" + url);
		HttpGet httpget = new HttpGet(url);

		try {

			/*** process response ***/
			response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();

			/*** extract page content ***/
			if (entity != null) {
				InputStream instream = entity.getContent();
				Document doc = Jsoup.parse(instream, "utf-8", url);
				/***
				 * extract html elements that contain the <a> tag with href
				 * attribute
				 ***/
				Elements links = doc.select("a[href]");
				/*** Extract and store undiscovered links ***/
				for (Element link : links) {

					if (link.attr("abs:href").matches("^mailto.*")) { // make
																		// sure
																		// it is
																		// not a
																		// mailto
																		// link
																		// like
																		// "mailto:limeworks@ucsc.edu"
						continue;
					}
					result.add(link.attr("abs:href"));
				}
				instream.close();
			}

			response.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		prevFinished = Instant.now();
		dlDuration = Duration.between(startNext, prevFinished);
		startNext = prevFinished.plus(dlDuration.multipliedBy(k));

		// System.err.println(result);
		// dl = System.currentTimeMillis() - startTime;
		// System.err.println("time in milliseconds is: " + dl);

		return result;

	}

	/**
	 * check and enforce politeness by sleeping the thread for the necessary
	 * duration
	 */
	private void timeout() {
		System.err.print("start timeout: ");
		Instant currentTime = Instant.now();
		System.err.println(Duration.between(currentTime, startNext));
		if (startNext.isAfter(currentTime)) {
			try {
				Duration wait = Duration.between(currentTime, startNext);
				Thread.sleep(wait.toMillis());
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
		System.err.println("end timeout");

	}

	@Override
	public int compareTo(DomainCrawler other) {
		return this.startNext.compareTo(other.startNext);
	}

}
