package com.usamin.nana;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.HashSet;
import java.util.Set;

import com.usamin.nana.extractor.CrawlerUniversal;
import com.usamin.nana.extractor.DomainCrawler;

public class CrawlMap extends CrawlerUniversal {

   /** Priority queue, sort crawler threads by the earliest time
    * that they can politely execute the next request
    */
   PriorityBlockingQueue<DomainCrawler> frontier;
   Set<String> discovered;
   ConcurrentHashMap<String, DomainCrawler> map;
   
   public CrawlMap() {
      discovered = ConcurrentHashMap.<String>newKeySet();
      frontier = new PriorityBlockingQueue<DomainCrawler>();
      map = new ConcurrentHashMap<String, DomainCrawler>();
   }
   
   public CrawlMap(String seed) {
      frontier = new PriorityBlockingQueue<DomainCrawler>();
      
      DomainCrawler crawler = new DomainCrawler(seed);
      map.put(crawler.getHostname(), crawler);
      
   }
   
   /** retrieve crawler corresponding to the url's domain
    * add to that domain's FIFO queue
    * insert crawler to priority queue
    */
   public void exploreFrontier(String url) {
      String hostname = getHost(url);
      if(!map.containsKey(hostname)) {
         DomainCrawler crawler = new DomainCrawler(url);
         map.put(hostname, crawler);
         frontier.add(crawler);
      } else {
         DomainCrawler crawler = map.get(hostname);
         crawler.addURL(url);
         frontier.add(crawler);
      }      
   }
   
   public boolean discover(String url) {
      url = DomainCrawler.removeSpace(url);
      return discovered.add(url);
   }
   
   public DomainCrawler nextCrawl() {
      return frontier.poll();
   }
   
}
