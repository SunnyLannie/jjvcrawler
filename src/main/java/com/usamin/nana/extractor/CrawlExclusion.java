package com.usamin.nana.extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
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

public class CrawlExclusion {

   String hostname;
   Instant expiration;
   List<String> disallow;
   List<String> allow;
   
   public CrawlExclusion() {
   
   }
   
   public static CrawlExclusion getExclusions(String url) {
      CrawlExclusion ex = new CrawlExclusion();
      url.replaceAll(" ", "%20");
      URI uri = null;
      try {
         uri = new URI(url);
      } catch (URISyntaxException e) {
         e.printStackTrace();
      }
      ex.hostname = uri.getHost();
      System.out.println(ex.hostname);
      String robotsDotTxt = ex.hostname + "/robots.txt";
      
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
            String regex_path = "[ ]*([\\/]?[\\w]*)*$"; //([\\s]*#+[\\w]*)*
            Pattern getPath = Pattern.compile(regex_path, Pattern.CASE_INSENSITIVE);
            String regex_allAgent = "[\\s]*[*]";
            Pattern getAllAgent = Pattern.compile(regex_allAgent, Pattern.CASE_INSENSITIVE);
            
            /* DO NOT TOUCH THIS
            
            while( (line = reader.readLine()) != null ) {
               
               line.toLowerCase();
               if(hasRules) {
                  if( line.matches("allow:[.]*") ){
                     
                     Matcher matcher = p.matcher(line);
                     if(matcher.matches()){
                        System.out.println(matcher.group(1));
                     }
                  }
               } else if(hasRules && line.matches("user-agent:[\\s]*[\\w]+")) {
                  hasRules = false;
               } else if(!hasRules && line.matches("user-agent:[\\s]*[*]")) {
                  hasRules = true;
               }
               
            }*/
            
            instream.close();
         }

         response.close();

      } catch (IOException e) {
         e.printStackTrace();
      }
      
      return ex;
   }
   

}
