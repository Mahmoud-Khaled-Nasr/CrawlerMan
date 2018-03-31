package crawler.robot;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RobotMonitor {

    final private Map <String, Robot> robots;
    final private String USER_AGENT = "Crawler Man";

    public RobotMonitor (){
        this.robots = new HashMap<>();
    }

    public boolean isAllowed (String url){
        String baseURL ="";
        try {
            URL temp = new URL(url);
            baseURL = temp.getProtocol()+ "://" + temp.getHost();
        } catch (MalformedURLException e) {
            System.out.println("Usage: RobotMonitor -> isAllowed Message: Malformed baseURL");
            e.printStackTrace();
        }
        Robot robot = getRobot(baseURL);
        if (robot == null){
            return true;
        }
        return robot.isAllowed(url);
    }

    private Robot getRobot (String baseURL){
        String key ="";
        synchronized (robots){
            if (!robots.containsKey(baseURL)){
                robots.put(baseURL, null);

                for (String robotKey: robots.keySet()) {
                    if (robotKey.equals(baseURL)){
                        key = new String(robotKey);
                        break;
                    }
                }
            }else {
                key = baseURL;
            }
        }
        synchronized (key){
            if (robots.get(key) == null){
                robots.put(key, new Robot(USER_AGENT, key));
            }
            return robots.get(key);
        }
    }
}
