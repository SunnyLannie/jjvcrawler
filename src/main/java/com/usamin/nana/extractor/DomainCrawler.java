package com.usamin.nana.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
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

public class DomainCrawler {

	String hostname;
	LinkedList<String> undiscovered;
	long k=50; // politeness factor
	long dl = 0; // duration of the last download from this domain. initially 0

	public DomainCrawler(String hostname) {
		this.hostname = hostname;
		undiscovered = new LinkedList<String>();
	}

	public DomainCrawler(String hostname, String url) {
		this.hostname = hostname;

		undiscovered = new LinkedList<String>();
		undiscovered.add(url);
	}
	
	
	public long getCurrentPoliteness(){
		return dl;
	}

	public LinkedList<String> crawl() {
		/** remove url from FIFO queue */
		String url = undiscovered.poll();

		/** initializing httpclient components */
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response;

		HttpGet httpget = new HttpGet(url);

		/** list to store undiscovered urls on this page */
		LinkedList<String> result = new LinkedList<String>();
		
		//to avoid dos server, need to wait for a time. 
		//politeness factor*duration of last download+processing
		try {
			Thread.sleep(dl*k);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		final long startTime = System.currentTimeMillis();

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

		System.out.println(result);
		dl = System.currentTimeMillis() - startTime;
		System.out.println("time in miliseconds is: " + dl);

		return result;

	}
	
	public void addURL(String url) {
      undiscovered.add(url);
   }

}
