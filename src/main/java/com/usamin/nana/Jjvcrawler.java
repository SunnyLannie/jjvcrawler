package com.usamin.nana;

import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.usamin.nana.extractor.DomainCrawler;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
//This is main
/*** process that downloads and parse webpage for hyperlinks ***/

public class Jjvcrawler {

	public static void main(String[] args) throws URISyntaxException {
		System.out.println("ran");
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		String test =
		 "https://en.wikipedia.org/wiki/Cassiopeia_(constellation)";
		test = "https://disorderlylabs.github.io/";
		String reddit = "https://www.reddit.com/";
		String facebook = "https://www.facebook.com";
		//test="https://www.equestriadaily.com";
		String test2="https://www.macrumors.com/";
		//test="https://www.equestriadaily.com";
		// test= "https://sites.google.com/site/daviddeyellwatercolor/";
		HashMap<String, DomainCrawler> map = 
		      new HashMap<String, DomainCrawler>();
		PriorityQueue<String> frontier = new PriorityQueue<String> ();

		/*** output file name ***/
		
		String filename = "unwrap.nana";
		
		
		crawlTop top=new crawlTop(test);  //crawl top is where the actual crawling takes place inside of a for loop. 
		//something where we have multiple threads there will be ideal for it
		//each thread could crawl a different domain
		//DomainCrawler dc = new DomainCrawler(test);
		//top.executeCrawl(dc);
		
		final long startTime = System.currentTimeMillis();

		
		crawlThreading thread= new crawlThreading(top, test);
		crawlThreading thread1= new crawlThreading(top, test);

		thread.start();
		thread1.start();
		
		
		try{thread.join();
        }catch(Exception e){
            System.err.println(e);
        }
		
		try{thread1.join();
        }catch(Exception e){
            System.err.println(e);
        }    

		
		final long endTime = System.currentTimeMillis();
      
		System.out.println("Total execution time: " + (endTime - startTime) );

		//System.out.println(dc.crawl());
		
		/*
		DomainCrawler seedDomain = new DomainCrawler(test);
		map.put("hi", seedDomain);
		LinkedList<String> links = seedDomain.crawl();
		Set<String> s = new LinkedHashSet<>(links);

        System.out.println(s);
        seedDomain.addURL(links.get(2));
        System.out.println(seedDomain.crawl());
        */

	}
	

}