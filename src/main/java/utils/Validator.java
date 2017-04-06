package utils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    public static boolean isValidLogin(String login) {
        Matcher regexMatcher = Pattern.compile("[a-z|A-Z]+").matcher(login);
        return regexMatcher.find();
    }


}
