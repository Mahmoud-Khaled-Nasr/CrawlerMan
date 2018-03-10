package crawler.robot;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import com.panforge.robotstxt.RobotsTxt;
import org.jsoup.Jsoup;

public class RobotMonitor {

    final private Map <String, RobotsTxt> robots;
    final private String USER_AGENT = "Crawler Man";

    public RobotMonitor (){
        this.robots = new HashMap<>();
    }

    public boolean isAllowed (String url){
        //System.out.println("URL is (" + url +")");
        String baseURL ="";
        try {
            URL temp = new URL(url);
            baseURL = temp.getProtocol()+ "://" + temp.getHost();
        } catch (MalformedURLException e) {
            System.out.println("Usage: RobotMonitor -> isAllowed Message: Malformed baseURL");
            e.printStackTrace();
        }
        //System.out.println("baseURL is (" + baseURL +")");
        RobotsTxt robotsTxt = getRobot(baseURL);
        if (robotsTxt == null){
            return true;
        }
        return robotsTxt.query(USER_AGENT, url);

    }

    private RobotsTxt getRobot (String baseURL){
        String key ="";
        synchronized (robots){
            if (!robots.containsKey(baseURL)){
                robots.put(baseURL, null);

                for (String robotKey: robots.keySet()) {
                    if (robotKey.equals(baseURL)){
                        key = robotKey;
                        break;
                    }
                }
            }
            /*else {
                key = baseURL;
            }*/
        }
        synchronized (key){
            if (robots.get(key) == null){
                //System.out.println("key of map in if condition is (" + key +")");
                robots.put(key, fetchRobot(key + "/robots.txt"));
            }
            //System.out.println("key of map is (" + key +")");
            return robots.get(key);
        }
    }

    private RobotsTxt fetchRobot(String url) {
        //System.out.println("url of fetching robot is (" + url +")");
        InputStream robotsTxtStream = null;
        RobotsTxt robotsTxt = null;
        try {
            robotsTxtStream = new URL(url).openStream();
        } catch (IOException e) {
            //System.out.println("Usage: RobotMonitor -> fetchRobot Message: Malformed baseURL for getting robots.txt" +
                    //" baseURL = ("+ url + ")");
            //e.printStackTrace();
            return null;
        }

        try {
            robotsTxt = RobotsTxt.read(robotsTxtStream);
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }

        return robotsTxt;
    }

    //For Testing
    public void printMap (){
        for (String key : robots.keySet()) {
            System.out.println(key);
        }
    }
}
