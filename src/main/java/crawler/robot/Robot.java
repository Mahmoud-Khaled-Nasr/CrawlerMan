package crawler.robot;

import org.jsoup.Jsoup;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Robot {
    final private String baseURL;
    final private String userAgent;
    private List<RobotRule> disallowedRules, allowedRules;
    private Pattern baseURLPattern;


    Robot (String userAgent, String baseURL){
        this.baseURL = baseURL;
        this.userAgent = userAgent;
        this.allowedRules = null;
        this.disallowedRules = null;
        this.baseURLPattern = Pattern.compile(baseURL);
    }

    private void getRobotsTxt(){
        String robotTxtFile="";
        try {
            robotTxtFile = Jsoup.connect(baseURL + "/robots.txt").get().wholeText();
        } catch (IOException e) {
            //if the robots.txt wasn't found Add Disallow nothing Rule to disallowedRules
            allowedRules.add(new RobotRule(RobotRule.DISALLOW_OPTION + ":"));
        }
        List<String> robotTxtFileLines = new ArrayList<>(Arrays.asList(robotTxtFile.split("\n")));
        List<String> temp = new ArrayList<>();

        for (String line: robotTxtFileLines) {
            int hashIndex = line.indexOf('#');
            if (hashIndex != -1){
                line = line.substring(0, hashIndex);
            }
            line = line.replaceAll("\\s", "");
            line = line.trim();
            if (!line.equals("")) {
                if (RobotRule.isNeededOption(line)) {
                    temp.add(line);
                }
            }
        }
        createRules(temp);
    }

    private void createRules (List<String> rules){
        boolean foundProperUserAgent = false;
        int firstRuleIndex = 0, lastRuleIndex = 0;
        for (int i=0; i<rules.size(); i++){
            String line = rules.get(i);
            RobotRule robotRule = new RobotRule(line);
            if (robotRule.getOption().equals(RobotRule.USER_AGENT_OPTION) &&
                    (robotRule.getRule().equals(userAgent) || robotRule.getRule().equals("*")) ){

                    if (foundProperUserAgent && robotRule.getOption().equals(RobotRule.USER_AGENT_OPTION)){
                        lastRuleIndex = i - 1;
                        break;
                    }
                    foundProperUserAgent = true;
                    firstRuleIndex = i + 1;
            }
            if (foundProperUserAgent && i == rules.size()-1){
                lastRuleIndex = i;
                break;
            }
        }

        if (lastRuleIndex == 0) {
            disallowedRules.add(new RobotRule("disallow:/"));
            return;
        }
        disallowedRules.add(new RobotRule("disallowed:*/*.*"));
        allowedRules.add(new RobotRule("disallowed:*/*.html"));
        allowedRules.add(new RobotRule("disallowed:*/*.htm"));
        for (int i = firstRuleIndex; i<= lastRuleIndex; i++){
            RobotRule robotRule = new RobotRule(rules.get(i));
            if (robotRule.getOption().equals(RobotRule.ALLOW_OPTION)){
                allowedRules.add(robotRule);
            }
            if (robotRule.getOption().equals(RobotRule.DISALLOW_OPTION)){
                disallowedRules.add(robotRule);
            }
        }
    }

    synchronized private void downloadRobotsTxt (){
        if (allowedRules == null && disallowedRules == null) {
            allowedRules = new LinkedList<>();
            disallowedRules = new LinkedList<>();
            getRobotsTxt();
        }
    }

    boolean isAllowed (String url) {
        if (allowedRules == null || disallowedRules == null) {
            downloadRobotsTxt();
        }

        Matcher matcher = baseURLPattern.matcher(url);
        String relativeURL = matcher.replaceFirst("");

        boolean allowed = false, disallowed = false;
        RobotRule allowingRule = null, disallowingRule = null;
        for (RobotRule rule : allowedRules) {
            if (rule.pass(relativeURL)) {
                allowingRule = rule;
                allowed = true;
                break;
            }
        }

        for (RobotRule rule : disallowedRules) {
            if (rule.pass(relativeURL)) {
                disallowingRule = rule;
                disallowed = true;
                break;
            }
        }

        if (!disallowed) {
            return true;
        } else if (!allowed) {
            return false;
        } else {
            return allowingRule.getRuleRank() > disallowingRule.getRuleRank();
        }
    }
}