package com.usamin.nana.extractor;

import java.net.URI;
import java.net.URISyntaxException;
//commonly used functions are placed in this file to allow code reuse

public class CrawlerUniversal {
	private static  boolean DEBUG = false;
     private static int NUM_ITER_CRAWL=200;
     
     
     
     
	public CrawlerUniversal() {
	}

	public static void setNumberIterationsCrawl(int crawl){
		NUM_ITER_CRAWL=crawl;
	}
	
	public static int getNumberIterCrawl(){
		return NUM_ITER_CRAWL;
	}
	
	public static void setDebugTrue(){
		DEBUG=true;
	}
	public static void setDebugFalse(){
		DEBUG=false;
	}
	public static boolean getDebugFlag(){
		return DEBUG;
	}
	
	public static final String removeSpace(String url) {
		return url.replaceAll(" ", "%20");
	}

	public static final String getHost(String url) {
		url = removeSpace(url);
		URI uri = null;
		try {
			uri = new URI(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		String hostname = uri.getHost();
		return hostname;
	}

	// returns true if they are the same domain, and false otherwise
	public static final boolean areSameDomain(String url1, String url2) {
		url1 = getHost(url1);
		url2 = getHost(url2);
		if (url1 == null || url2 == null) {
			return false;
		}
		return url1.equals(url2);
	}

	// removes final / from url if it is present
	public static final String trimUrl(String url) {
		url = url.trim();
		if (url != null && url.length() > 0 && url.charAt(url.length() - 1) == '/') {
			url = url.substring(0, url.length() - 1);
		}
		return url;

	}

	public  void debugPrint(String s) {
		if (DEBUG) {
			System.err.println(s);
		}

	}


}
