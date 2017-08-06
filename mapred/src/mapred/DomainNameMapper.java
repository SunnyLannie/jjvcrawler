package mapred;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class DomainNameMapper
      extends Mapper <LongWritable, Text, Text, Text> {
   
   private UrlRecordParser parser = new UrlRecordParser();

   public void map(LongWritable key, Text value, Context context) 
         throws IOException, InterruptedException {
      
      if(parser.parse(value)) {
         
         try {
            context.write(new Text(parser.getDomain()),
                  new Text(parser.getPath()));
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
         }
      }
   }

}
