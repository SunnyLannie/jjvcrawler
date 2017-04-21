package com.usamin.nana;

import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;

/*** process that downloads and parse webpage for hyperlinks ***/

public class Jjvcrawler {

   public static void main(String[] args) {
      
      /*** Send GET request ***/
      CloseableHttpClient httpclient = HttpClients.createDefault();
//      String test = "https://en.wikipedia.org/wiki/Cassiopeia_(constellation)";
      String test = "https://disorderlylabs.github.io/";
      HttpGet httpget = new HttpGet(test);
//      HttpGet httpget = new HttpGet("https://www.example.com");
//      HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1,
//            HttpStatus.SC_OK, "OK");
      CloseableHttpResponse response;
      
      /*** output file name ***/
      String filename = "unwrap.nana";
      
      try {
         /*** writer for dl'ed page to file ***/
         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
               new FileOutputStream(filename), "UTF-8"));
         
         /*** process response ***/
         response = httpclient.execute(httpget);
         HttpEntity entity = response.getEntity();
         if (entity != null) {
            /*** extract page content ***/
            InputStream instream = entity.getContent();
            Document doc = Jsoup.parse(instream, "UTF-8", test);
            
            /*** extract html elements that contain the <a> tag with href attribute ***/
            Elements links = doc.select("a[href]");
            
//            String readline;
//            System.out.println(links.outerHtml());
            
            
            /*** Writing html elements to file ***/
            links.unwrap();
//            Element e = links.last();
//            writer.write(e.toString());
            for(Element link : links){
               writer.write(link.attr("abs:href") + '\n');
//               System.out.println(link);
            }

            writer.close();
//            links.remove();
//            System.out.println(doc);
                        
            /*** reading the content ***/
            
          /*  BufferedReader br = new BufferedReader(new InputStreamReader(instream));
            try{
               
               int i = 0;
               while( ((readline = br.readLine()) != null) ) {
                  writer.write(readline);
                  writer.newLine();
               }
            } finally {
               instream.close();
               writer.close();
            } */
         }
         response.close();
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
     
         
   }
      
}

