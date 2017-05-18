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


//parses robots.txt in order to gain url that should not be crawled
public class CrawlExclusion extends CrawlerUniversal {

   final String urlRegexComponents = 
         "(^(([^:/?#]+):)?(//([^/?#]*))?)(([^?#]*)(\\?([^#]*))?(#(.*))?)";
   final String exclusionEntryComponents;
   final Pattern urlPattern;
   final Pattern exclusionEntry; 
   
   String hostname;
   Instant expiration;
   HashSet<String> disallow;
   HashSet<String> allow;
   
   
   private CrawlExclusion() {
      disallow = new HashSet<String>();
      allow = new HashSet<String>();
      
      /** a robots.txt regex entry is represented by
       * (regex_keyword) + ((regex_allAgents) + (regex_path + regex_file)) */
      String regex_keyword = "\\s*([\\w-]+)\\s*:\\s*"; // group(1)
      String regex_allAgents = "(?:(\\*)|\\w+)"; //group(3)
      String regex_path = "(?:(?:/[\\w*]+)*/?)*"; 
      String regex_file = "(?:\\*|[\\w\\-\\.\\_]*[^#?\\s]*)(?:\\.\\w+(?:[-_]\\w+)*)*";
      
      String group4 = "(" + regex_path + regex_file + ")";
      String group2 = "(" + regex_allAgents + "|" + group4 + ")";
      
      
//      String regex_path = "((?:[\\/?\\w]+[\\w\\-\\.]?[^#?\\s]?)*)";        //"([\\/]?[\\w]*)*"; //([\\s]*#+[\\w]*)*

      // "[\\s]*([\\w-?]+)[\\s]*:[\\s]*(([*])|([\\w]+)|((?:[\\/?\\w]+[\\w\\-\\.]?[^#?\\s]?)*))"
      exclusionEntryComponents = regex_keyword + group2;
      exclusionEntry = Pattern.compile(exclusionEntryComponents);
      urlPattern = Pattern.compile(urlRegexComponents);
   }
   
   public HashSet<String> disallow() {
      return disallow;
   }
   
   public HashSet<String> allow() {
      return allow;
   }
   
   public boolean isExcluded(String url) {
      url = removeSpace(url);
      System.out.println(url);
      Matcher urlComponents = urlPattern.matcher(url);
      String path = null;
      try {
         path = urlComponents.group(6);
      } catch (IllegalStateException e) {
         System.err.println("This URL " + url + " has no path component");
         return false;
      }
      
      boolean excluded = false;
      
      for(String disallowed: disallow) {
         
         excluded = path.contains(disallowed);
         
         if(excluded) {
            for(String allowed: allow) {
               if(url.equals(allowed)){
                  excluded = false;
                  return excluded;
               }
            }
            break;
         }
      }
      
      return excluded;
   }
   
   public static CrawlExclusion getExclusions(String url) {
      CrawlExclusion ex = new CrawlExclusion();
      
      ex.hostname = getHost(url);
      //System.out.println(ex.hostname);
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

            while( (line = reader.readLine()) != null ) {
               
               Matcher entryMatch = ex.exclusionEntry.matcher(line);
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
                     path = entryMatch.group(4);
                     if(path != null && hasRules){
                        ex.allow.add(path);
                     }
                     break;
                  case "disallow":
                     if(!hasRules) break;
                     path = entryMatch.group(4);
                     if(path != null && hasRules){
                        ex.disallow.add(path);
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
