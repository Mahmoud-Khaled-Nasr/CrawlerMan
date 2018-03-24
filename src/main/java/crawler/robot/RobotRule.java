package crawler.robot;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RobotRule {

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

    RobotRule(String rule) {
        int indexOfColon = rule.indexOf(':');
        this.option = rule.substring(0, indexOfColon).toLowerCase();
        this.rule = rule.substring(indexOfColon + 1, rule.length());
    }

    public static boolean isNeededOption (String rule){
        int colonIndex = rule.indexOf(':');
        if (colonIndex == -1){
            //To handle wrong robots.txt malformation
            return false;
        }
        String option = rule.substring(0, colonIndex).toLowerCase();
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

        //TODO replace the w* with [A-Za-z0-9]and [_]
        //TODO take care of special characters of the regex (?)
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
