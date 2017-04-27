package com.usamin.nana;

import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.usamin.nana.extractor.DomainCrawler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;

/*** process that downloads and parse webpage for hyperlinks ***/

public class Jjvcrawler {

	public static void main(String[] args) {

		
		// String test =
		// "https://en.wikipedia.org/wiki/Cassiopeia_(constellation)";
		String test = "https://disorderlylabs.github.io/";
		// test= "https://sites.google.com/site/daviddeyellwatercolor/";
		HashMap<String, DomainCrawler> map = 
		      new HashMap<String, DomainCrawler>();
		PriorityQueue<String> frontier = new PriorityQueue<String> ();

		/*** output file name ***/
		String filename = "unwrap.nana";
		

		/*
		String seed = getHostname(test);
		
		DomainCrawler seedDomain = new DomainCrawler(seed, test);
		map.put(seed, seedDomain);
		LinkedList<String> links = seedDomain.crawl();
      
      for(String link: links) {
         String hostname = getHostname(link);
         if(!map.containsKey(hostname)) {
            DomainCrawler crawler = new DomainCrawler(hostname, link);
            map.put(hostname, crawler);
         } else {
            DomainCrawler crawler = map.get(hostname);
            crawler.addURL(link);
         }
         
      }
      System.out.println(map.keySet());*/
	}
	


}