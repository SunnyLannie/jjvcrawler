package com.usamin.nana.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
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

public class DomainCrawler implements Comparable<DomainCrawler>{

	String hostname;
	LinkedList<String> undiscovered; // FIFO queue of the pages to crawl on this domain
	long k=50; // politeness factor
	Instant startNext; // start time of the next url download for this domain
	Instant prevFinished; // time when the last download on this domain was finished
	Duration dlDuration; // duration of the last download

	public DomainCrawler(String url) {
		this.hostname = getHost(url);

		undiscovered = new LinkedList<String>();
		undiscovered.add(url);
		
		resetTime();
		dlDuration = Duration.ZERO;
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
	
	/*** add url to FIFO queue, discard if it is not from the same domain
	 * initialize hostname if isn't already initialized **/
   public void addURL(String url) {
      if(this.hostname != null) {
         if(getHost(url) != this.hostname) return;
         undiscovered.add(removeSpace(url));
      } else {
         this.hostname = getHost(url);
         undiscovered.add(url);
      }
      
   }
   
   /** remove first element from FIFO queue and extract links found */
	public LinkedList<String> crawl() {
	   
	   timeout();
	   resetTime();
	   
		/** remove url from FIFO queue */
		String url = undiscovered.poll();

		/** initializing httpclient components */
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response;

		HttpGet httpget = new HttpGet(url);

		/** list to store undiscovered urls on this page */
		LinkedList<String> result = new LinkedList<String>();

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
		startNext = prevFinished.plus( dlDuration.multipliedBy(k) );
		

		//System.out.println(result);
		//dl = System.currentTimeMillis() - startTime;
		//System.out.println("time in miliseconds is: " + dl);

		return result;

	}
	
	private String removeSpace(String url) {
	   return url.replaceAll(" ", "%20");
	}
	
	private String getHost(String url) {
	   removeSpace(url);
      URI uri = null;
      try {
         uri = new URI(url);
      } catch (URISyntaxException e) {
         e.printStackTrace();
      }
      String hostname = uri.getHost();
      return hostname;
   }
   
	/** check and enforce politeness */
   private void timeout() {
      Instant currentTime = Instant.now();
      if(startNext.isAfter(currentTime)){
         try {
            Duration wait = Duration.between(currentTime, startNext);
            Thread.sleep(wait.toMillis());
         } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
         }
      }
      
   }

   @Override
   public int compareTo(DomainCrawler other) {
      return this.startNext.compareTo(other.startNext);
   }

}
