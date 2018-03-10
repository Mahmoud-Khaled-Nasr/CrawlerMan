package crawler;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.*;

public class Robot {
    final private String baseURL;
    final private String userAgent;
    private List<RobotRule> disallowedRules, allowedRules;


    public Robot (String userAgent, String baseURL){
        this.baseURL = baseURL;
        this.userAgent = userAgent;
        this.allowedRules = new ArrayList<>();
        this.disallowedRules = new ArrayList<>();
        getRobotsTxtRules(baseURL);
        //parse it to disallowed and allowed

    }

    private void getRobotsTxtRules(String baseURL){
        String robotTxtFile="";
        try {
            robotTxtFile = Jsoup.connect(baseURL + "/robots.txt").get().wholeText();
        } catch (IOException e) {
            //TODO do something to make everything work fine if robot not found
        }
        List<String> robotTxtFileLines = new ArrayList<>(Arrays.asList(robotTxtFile.split("\n")));
        List<String> temp = new ArrayList<>();

        for (String line: robotTxtFileLines) {
            line = line.trim();
            int hashIndex = line.indexOf('#');
            if (hashIndex != -1){
                line = line.substring(0, hashIndex);
            }
            line = line.replaceAll("\\s", "");
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
                    (robotRule.getValue().equals(userAgent) || robotRule.getValue().equals("*")) ){

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
        //TODO handle the case in which no user-agent is found to block the crawler return false always
        for (int i = firstRuleIndex; i<= lastRuleIndex; i++){
            RobotRule robotRule = new RobotRule(rules.get(i));
            if (robotRule.getOption().equals(RobotRule.ALLOW_OPTION)){
                allowedRules.add(robotRule);
            }
            if (robotRule.getOption().equals(RobotRule.DISALLOW_OPTION)){
                disallowedRules.add(robotRule);
            }
        }

        for (RobotRule robotRule: allowedRules){
            System.out.println(robotRule.getOption() + " " + robotRule.getValue());
        }
        for (RobotRule robotRule: disallowedRules){
            System.out.println(robotRule.getOption() + " " + robotRule.getValue());
        }
    }
}

class RobotRule {

    public static final String USER_AGENT_OPTION ="user-agent";
    public static final String ALLOW_OPTION ="allow";
    public static final String DISALLOW_OPTION ="disallow";
    private static final Set<String> neededOptions = new HashSet<>(Arrays.asList(USER_AGENT_OPTION, ALLOW_OPTION, DISALLOW_OPTION));
    private String option, value;

    public String getOption() {
        return option;
    }

    public String getValue() {
        return value;
    }

    public RobotRule(String rule){
        //TODO throw an exception if the rule has something wrong
        int indexOfColon = rule.indexOf(':');
        option = rule.substring(0, indexOfColon).toLowerCase();
        value = rule.substring(indexOfColon + 1, rule.length());
    }

    public static boolean isNeededOption (String rule){
        String option = rule.substring(0, rule.indexOf(':')).toLowerCase();
        return neededOptions.contains(option);
    }
}
