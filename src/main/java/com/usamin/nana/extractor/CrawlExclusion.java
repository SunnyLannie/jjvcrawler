package com.usamin.nana.extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlExclusion extends CrawlerUniversal {

   String hostname;
   Instant expiration;
   HashSet<String> disallow;
   HashSet<String> allow;
   
   public CrawlExclusion() {
      disallow = new HashSet<String>();
      allow = new HashSet<String>();
   }
   
   private void allow(String path) {
      allow.add(path);
   }
   
   private void disallow(String path) {
      disallow.add(path);
   }
   
   public static CrawlExclusion getExclusions(String url) {
      CrawlExclusion ex = new CrawlExclusion();
      
      ex.hostname = getHost(url);
      System.out.println(ex.hostname);
      String robotsDotTxt = "https://" + ex.hostname + "/robots.txt";
      
      /** initializing httpclient components */
      CloseableHttpClient httpclient = HttpClients.createDefault();
      CloseableHttpResponse response;

      HttpGet httpget = new HttpGet(robotsDotTxt);

      try {

         /*** process response ***/
         response = httpclient.execute(httpget);
         HttpEntity entity = response.getEntity();

         /*** retrieve robots.txt ***/
         if (entity != null) {
            InputStream instream = entity.getContent();
            BufferedReader reader =
                  new BufferedReader(new InputStreamReader(instream));
            
            String line;
            boolean hasRules = false;
            
            String regex_keyword = "[\\s]*([\\w-?]+)[\\s]*:[\\s]*"; // group1
            String regex_allAgents = "([*])|([\\w]+)"; //group2-3
            String regex_path = "((?:[\\/?\\w]+[\\w\\-\\.]?[^#?\\s]?)*)";        //"([\\/]?[\\w]*)*"; //([\\s]*#+[\\w]*)*

            // "[\\s]*([\\w-?]+)[\\s]*:[\\s]*(([*])|([\\w]+)|((?:[\\/?\\w]+[\\w\\-\\.]?[^#?\\s]?)*))"
            String regex_entry = regex_keyword + "(" +
                                 regex_allAgents + "|" +
                                 regex_path + ")";
            Pattern entry = Pattern.compile(regex_entry);
            
            while( (line = reader.readLine()) != null ) {
               
               Matcher entryMatch = entry.matcher(line);
               if(entryMatch.matches()){
                  String keyword = entryMatch.group(1).toLowerCase();
                  String path = null;
                  switch (keyword) {
                  case "user-agent":
                     if(entryMatch.group(3) != null) hasRules = true;
                     else hasRules = false;
                     break;
                  case "allow":
                     if(!hasRules) break;
                     path = entryMatch.group(5);
                     if(path != null && hasRules){
                        ex.allow(path);
                     }
                     break;
                  case "disallow":
                     if(!hasRules) break;
                     path = entryMatch.group(5);
                     if(path != null && hasRules){
                        ex.disallow(path);
                     }
                     break;
                  default:
                     break;
                  }
               }

            }
            
            instream.close();
         }

         response.close();

      } catch (IOException e) {
         e.printStackTrace();
      }
      
      return ex;
   }
   
   public String toString() {
      String robotstxt = "";
      String newline = System.getProperty("line.separator");
      
      robotstxt = robotstxt + "Disallow:" + newline;
      for(Iterator<String> it = disallow.iterator(); it.hasNext();) {
         robotstxt = robotstxt + it.next() + newline;
      }
      
      robotstxt = robotstxt + "Allow:" + newline;
      for(Iterator<String> it = allow.iterator(); it.hasNext();) {
         robotstxt = robotstxt + it.next() + newline;
      }
      
      return robotstxt;
   }
   

}
