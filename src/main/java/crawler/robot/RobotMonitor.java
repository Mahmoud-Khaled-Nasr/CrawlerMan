package crawler.robot;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class RobotMonitor {

    private Map <String, Robot> robots;
    final private String USER_AGENT = "Crawler Man";
    private static final Logger LOGGER = Logger.getLogger(RobotMonitor.class.getName());

    public RobotMonitor (){
        this.robots = new ConcurrentHashMap<>();
    }

    public boolean isAllowed (String url) {
        String baseURL = "";
        try {
            URL temp = new URL(url);
            baseURL = temp.getProtocol() + "://" + temp.getHost();
        } catch (MalformedURLException e) {
            LOGGER.warning("Usage: RobotMonitor -> isAllowed Message: Malformed baseURL");
            e.printStackTrace();
            return true;
        }

        Robot robot = getRobot(baseURL);
        return robot == null || robot.isAllowed(url);
    }

    private Robot getRobot (String baseURL) {
        robots.putIfAbsent(baseURL, new Robot(USER_AGENT, baseURL));
        return robots.get(baseURL);
    }
}
