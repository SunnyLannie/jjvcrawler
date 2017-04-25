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
   float k; // politeness factor
   Duration dl; // duration of the last download from this domain
   
   public DomainCrawler(String url) throws URISyntaxException {
      URI uri = new URI(url);
      hostname = uri.getHost();
      
      undiscovered = new LinkedList<String>();
      undiscovered.add(url);
      
      k = 10;
   }
   
   public LinkedList<String> crawl(){
      
      /** remove url from FIFO queue */
      String url = undiscovered.poll();
      
      /** initializing httpclient components */
      CloseableHttpClient httpclient = HttpClients.createDefault();
      CloseableHttpResponse response;
      HttpGet httpget = new HttpGet(url);
      
      /** list to store undiscovered urls on this page*/
      LinkedList<String> result = new LinkedList<String>();
      
      try {
         
         /*** process response ***/
         response = httpclient.execute(httpget);
         HttpEntity entity = response.getEntity();
         if (entity != null){
            /*** extract page content ***/
            InputStream instream = entity.getContent();
            Document doc = Jsoup.parse(instream, "utf-8", url);
            /***
             * extract html elements that contain the <a> tag with href
             * attribute
             ***/
            Elements links = doc.select("a[href]");

            /*** Extract links from html and store undiscovered links***/
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
      
      return result;
      
   }
   
   

}
