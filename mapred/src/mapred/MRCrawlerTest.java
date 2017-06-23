package mapred;

import java.io.IOException;
import java.util.Arrays;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.*;


public class MRCrawlerTest {

   // mapper test
   @Test
   public void processesValidRecord()
         throws IOException, InterruptedException
   {
      
      Text value = 
            new Text("http://www.techhive.com/article/3187758/streaming-media/how-to-ensure-your-internet-connection-and-home-network-are-primed-for-cord-cutting.html");
      
      new MapDriver<LongWritable, Text, Text, Text>()
         .withMapper(new DomainNameMapper())
         .withInput(new LongWritable(0), value)
         .withOutput(new Text("www.techhive.com"), new Text("/article/3187758/streaming-media/how-to-ensure-your-internet-connection-and-home-network-are-primed-for-cord-cutting.html"))
         .runTest();
      
   }
   
   // reducer test
   @Test
   public void crawlDomainPath() 
         throws IOException, InterruptedException
   {
      Text crawlResult = new Text("https://disorderlylabs.github.io/# https://disorderlylabs.github.io/#research https://disorderlylabs.github.io/#projects https://disorderlylabs.github.io/#publications https://disorderlylabs.github.io/#talks https://disorderlylabs.github.io/#team https://disorderlylabs.github.io/#press https://disorderlylabs.github.io/#contact https://disorderlylabs.github.io/# https://disorderlylabs.github.io/# https://disorderlylabs.github.io/# http://bloom-lang.net/ http://programmability.us/ https://people.ucsc.edu/~palvaro/socc16.pdf https://people.ucsc.edu/~palvaro/molly.pdf https://www2.eecs.berkeley.edu/Pubs/TechRpts/2009/EECS-2009-173.html https://www.infoq.com/presentations/failure-test-research-netflix http://disorderlyprogramming.org https://sites.google.com/site/researchkamala/ https://users.soe.ucsc.edu/~kdahlgren/ http://ashutoshraina.github.io https://people.ucsc.edu/~sborland/ http://db.cs.berkeley.edu/jmh/ http://www.theregister.co.uk/2016/02/01/netflix_tries_molly_in_quest_for_enhanced_fault_finding_perception/ http://techblog.netflix.com/2016/01/automated-failure-testing.html");
      new ReduceDriver<Text, Text, Text, Text>()
         .withReducer(new CrawlDomainReducer())
         .withInput(new Text("disorderlylabs.github.io"),
               Arrays.asList(new Text("/"), new Text("/robots.txt")))
         .withOutput(new Text("/robots.txt"), crawlResult)
         .runTest();
   }

}
