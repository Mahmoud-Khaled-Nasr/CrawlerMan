package crawler.robot;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RobotRule {

    static final String USER_AGENT_OPTION ="user-agent";
    static final String ALLOW_OPTION ="allow";
    static final String DISALLOW_OPTION ="disallow";
    private static final Set<String> neededOptions = new HashSet<>(Arrays.asList(USER_AGENT_OPTION, ALLOW_OPTION, DISALLOW_OPTION));
    private String option, rule;

    String getOption() {
        return option;
    }

    String getRule() {
        return rule;
    }

    RobotRule(String rule) {
        int indexOfColon = rule.indexOf(':');
        this.option = rule.substring(0, indexOfColon).toLowerCase();
        this.rule = rule.substring(indexOfColon + 1, rule.length());
    }

    static boolean isNeededOption (String rule){
        int colonIndex = rule.indexOf(':');
        if (colonIndex == -1){
            //To handle wrong robots.txt malformation
            return false;
        }
        String option = rule.substring(0, colonIndex).toLowerCase();
        return neededOptions.contains(option);
    }

    boolean pass(String relativeURL) {

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
        Pattern rulePattern;
        try {
            rulePattern = Pattern.compile(modifiedRule);
        }catch (Exception e){
            e.printStackTrace();
            rulePattern = Pattern.compile("a");
        }
        Matcher matcher = rulePattern.matcher(relativeURL);

        return matcher.find();
    }

    int getRuleRank() {
        return (rule.split("/")).length;
    }
}
