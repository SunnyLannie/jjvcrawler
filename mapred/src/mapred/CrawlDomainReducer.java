package mapred;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlDomainReducer
      extends Reducer<Text, Text, Iterable<Text>, Text>
{
   
   public void reduce(Text key, Iterable<Text> values, Context context) 
         throws IOException, InterruptedException
   {
      Iterator<Text> iter = values.iterator();
      if(iter.hasNext()) {
         Text value = iter.next();
         Text crawlResult = 
               crawl(new Text("https://" + key.toString() + value.toString()));
         iter.remove();
         context.write(values, crawlResult);
      }
   }
   
   /** check and enforce politeness by sleeping the thread for the necessary duration */
   private void timeout(Instant startNext) {
     Instant currentTime = Instant.now();
      if(startNext.isAfter(currentTime)){
         try {
            Duration wait = Duration.between(currentTime, startNext);
            Thread.sleep(wait.toMillis());
         } catch (InterruptedException e1) {
            Thread.currentThread().interrupt();
            return;
         }
      }

      
   }
   
   /** remove first element from FIFO queue and extract links found */
   private Text crawl(Text value) {
      
      
      /** list to store undiscovered urls on this page */

      String url = value.toString();
      String result = "";
      if(url == null) return null;
      
      
      /** initializing httpclient components */
      CloseableHttpClient httpclient = HttpClients.createDefault();
      CloseableHttpResponse response;
        System.err.println("url is" +url);
      HttpGet httpget = new HttpGet(url);
      
      try {

         /*** process response ***/
         response = httpclient.execute(httpget);
         HttpEntity entity = response.getEntity();

         /*** extract page content ***/
         if (entity != null) {
            InputStream instream = entity.getContent();
            Document doc = Jsoup.parse(instream, "utf-8", url);
            /***
             * extract html elements that contain the <a> tag with href
             * attribute
             ***/
            Elements links = doc.select("a[href]");
            /*** Extract and store undiscovered links ***/
            for (Element link : links) {
               
               if(link.attr("abs:href").matches("^mailto.*")){ //make sure it is not a mailto link like "mailto:limeworks@ucsc.edu"
                  continue;
               }
               result = result + link.attr("abs:href") + " ";
            }
            instream.close();
         }

         response.close();

      } catch (IOException e) {
         return null;
      }

      //System.err.println(result);
      //dl = System.currentTimeMillis() - startTime;
      //System.err.println("time in milliseconds is: " + dl);

      return new Text(result);

   }

}
