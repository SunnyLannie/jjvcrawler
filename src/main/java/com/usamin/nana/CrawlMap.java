package com.usamin.nana;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;

import com.usamin.nana.extractor.DomainCrawler;

public class CrawlMap extends HashMap<String, DomainCrawler>{

   /** Priority queue, sort crawler threads by the earliest time
    * that they can politely execute the next request
    */
   PriorityBlockingQueue<DomainCrawler> frontier;
   
   public CrawlMap() {
      super();
      frontier = new PriorityBlockingQueue<DomainCrawler>();
   }
   
   public CrawlMap(String seed) {
      super();
      frontier = new PriorityBlockingQueue<DomainCrawler>();
      
      DomainCrawler crawler = new DomainCrawler(seed);
      this.put(crawler.getHostname(), crawler);
      
   }
   
   
   /** retrieve crawler corresponding to the url's domain
    * add to that domain's FIFO queue
    * insert crawler to priority queue
    */
   public void exploreFrontier(String url) {
      String hostname = getHost(url);
      if(!this.containsKey(hostname)) {
         DomainCrawler crawler = new DomainCrawler(url);
         this.put(hostname, crawler);
         frontier.add(crawler);
      } else {
         DomainCrawler crawler = this.get(hostname);
         crawler.addURL(url);
         frontier.add(crawler);
      }      
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
   
}
