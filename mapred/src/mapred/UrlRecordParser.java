package mapred;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.io.Text;

public class UrlRecordParser {

   final String urlRegexComponents = 
         "(^(([^:/?#]+):)?(//([^/?#]*))?)(([^?#]*)(\\?([^#]*))?(#(.*))?)";
   private String domain; // domain name
   private String path; //path
   
   public boolean parse(String url) {
      url = removeSpace(url);
      URI uri = null;
      try {
         uri = new URI(url);
         domain = uri.getHost();
         path = uri.getPath();
      } catch (URISyntaxException e) {
         // do something later
         return false;
      }
      return true;
   }
   
   public boolean parse(Text record) {
      return parse(record.toString());
   }
   
   public final String removeSpace(String url) {
      url = trimUrl(url);;
      return url.replaceAll(" ", "%20");
   }
   
   public String getDomain() {
      return domain;
   }
   
   public String getPath() {
      return path;
   }
   
   //removes final / from url if it is present
   public static final String trimUrl(String url){
      url = url.trim();
      if (url != null && url.length() > 0 && url.charAt(url.length()-1)=='/') {
         url = url.substring(0, url.length()-1);
          }
      return url;
      
   }

}
