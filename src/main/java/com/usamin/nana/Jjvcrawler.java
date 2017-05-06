package com.usamin.nana;

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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;

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
		test="https://www.equestriadaily.com";
		// test= "https://sites.google.com/site/daviddeyellwatercolor/";
		HashMap<String, DomainCrawler> map = 
		      new HashMap<String, DomainCrawler>();
		PriorityQueue<String> frontier = new PriorityQueue<String> ();

		/*** output file name ***/
		String filename = "unwrap.nana";
		

		
		
		DomainCrawler seedDomain = new DomainCrawler(test);
		map.put("hi", seedDomain);
		LinkedList<String> links = seedDomain.crawl();
		Set<String> s = new LinkedHashSet<>(links);

        System.out.println(s);
        seedDomain.addURL(links.get(10));
        System.out.println(seedDomain.crawl());
        
        
        
	}
	

}