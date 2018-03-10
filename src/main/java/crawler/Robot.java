package crawler;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Robot {
    final private String baseURL;
    final private String userAgent;
    private List<RobotRule> disallowedRules, allowedRules;
    private Pattern baseURLPattern;


    public Robot (String userAgent, String baseURL){
        this.baseURL = baseURL;
        this.userAgent = userAgent;
        this.allowedRules = new ArrayList<>();
        this.disallowedRules = new ArrayList<>();
        this.baseURLPattern = Pattern.compile(this.baseURL);
        getRobotsTxt(baseURL);
    }

    private void getRobotsTxt(String baseURL){
        String robotTxtFile="";
        try {
            robotTxtFile = Jsoup.connect(baseURL + "/robots.txt").get().wholeText();
        } catch (IOException e) {
            //TODO Testing is needed
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

        //TODO Testing is needed
        if (lastRuleIndex == 0) {
            disallowedRules.add(new RobotRule("disallow:/"));
            return;
        }
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
            System.out.println(robotRule.getOption() + " " + robotRule.getRule());
        }
        for (RobotRule robotRule: disallowedRules){
            System.out.println(robotRule.getOption() + " " + robotRule.getRule());
        }
    }

    public boolean isAllowed (String url){
        Matcher matcher = baseURLPattern.matcher(url);
        String relativeURL = matcher.replaceFirst("");

        //TODO check if the rule is applied on the url
        boolean allowed = false, disallowed =false;
        RobotRule allowingRule = null, disallowingRule = null;
        for (RobotRule rule: allowedRules){
            if (rule.pass(relativeURL)){
                allowingRule = rule;
                allowed = true;
                break;
            }
        }

        for (RobotRule rule: disallowedRules){
            if (rule.pass(relativeURL)){
                disallowingRule = rule;
                disallowed = true;
                break;
            }
        }

        if (!disallowed){
            return true;
        }else if (!allowed){
            return false;
        }else {
            return allowingRule.getRuleRank() > disallowingRule.getRuleRank();
        }
    }


}

class RobotRule {

    public static final String USER_AGENT_OPTION ="user-agent";
    public static final String ALLOW_OPTION ="allow";
    public static final String DISALLOW_OPTION ="disallow";
    private static final Set<String> neededOptions = new HashSet<>(Arrays.asList(USER_AGENT_OPTION, ALLOW_OPTION, DISALLOW_OPTION));
    private String option, rule;

    public String getOption() {
        return option;
    }

    public String getRule() {
        return rule;
    }

    public RobotRule(String rule) {
        int indexOfColon = rule.indexOf(':');
        this.option = rule.substring(0, indexOfColon).toLowerCase();
        this.rule = rule.substring(indexOfColon + 1, rule.length());
    }

    public static boolean isNeededOption (String rule){
        String option = rule.substring(0, rule.indexOf(':')).toLowerCase();
        return neededOptions.contains(option);
    }

    public boolean pass(String relativeURL) {

        if (rule.equals("")){
            return true;
        }
        if (rule.equals("/")){
            return false;
        }
        int relativeURLRank = relativeURL.split("/").length;
        if (relativeURLRank < getRuleRank()){
            return false;
        }

        //Handling the * rules: Replace all the * in rule with \w* to work with the Matcher class
        String modifiedRule = rule.replaceAll("\\*", "\\\\\\w*");
        Pattern rulePattern = Pattern.compile(modifiedRule);
        Matcher matcher = rulePattern.matcher(relativeURL);

        return matcher.find();
    }

    public int getRuleRank() {
        return (rule.split("/")).length;
    }

}
