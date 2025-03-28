package com.aichebaba.rooftop.ue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathFormat {
    private static final String TIME = "time";
    private static final String FULL_YEAR = "yyyy";
    private static final String YEAR = "yy";
    private static final String MONTH = "mm";
    private static final String DAY = "dd";
    private static final String HOUR = "hh";
    private static final String MINUTE = "ii";
    private static final String SECOND = "ss";
    private static final String RAND = "rand";
    private static final char[] radixDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a',
            'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
    private static Date currentDate = null;

    public static String parse(String input, Date date) {
        Pattern pattern = Pattern.compile("\\{([^\\}]+)\\}", 2);
        Matcher matcher = pattern.matcher(input);

        currentDate = date;

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, getString(matcher.group(1)));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    public static String parse(String input) {
        Pattern pattern = Pattern.compile("\\{([^\\}]+)\\}", 2);
        Matcher matcher = pattern.matcher(input);

        currentDate = new Date();

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, getString(matcher.group(1)));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    public static String format(String input) {
        return input.replace("\\", "/");
    }

    public static String parse(String input, String filename) {
        Pattern pattern = Pattern.compile("\\{([^\\}]+)\\}", 2);
        Matcher matcher = pattern.matcher(input);
        String matchStr = null;

        currentDate = new Date();

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matchStr = matcher.group(1);
            if (matchStr.indexOf("filename") != -1) {
                filename = filename.replace("$", "\\$").replaceAll("[\\/:*?\"<>|]", "");
                matcher.appendReplacement(sb, filename);
            } else {
                matcher.appendReplacement(sb, getString(matchStr));
            }
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    private static String getString(String pattern) {
        pattern = pattern.toLowerCase();
        if (pattern.indexOf("time") != -1) {
            return getTimestamp();
        }
        if (pattern.indexOf("yyyy") != -1) {
            return getFullYear();
        }
        if (pattern.indexOf("yy") != -1) {
            return getYear();
        }
        if (pattern.indexOf("mm") != -1) {
            return getMonth();
        }
        if (pattern.indexOf("dd") != -1) {
            return getDay();
        }
        if (pattern.indexOf("hh") != -1) {
            return getHour();
        }
        if (pattern.indexOf("ii") != -1) {
            return getMinute();
        }
        if (pattern.indexOf("ss") != -1) {
            return getSecond();
        }
        if (pattern.indexOf("rand") != -1) {
            return getRandom(pattern);
        }
        return pattern;
    }

    private static String getTimestamp() {
        return Long.toString(System.currentTimeMillis());
    }

    private static String getFullYear() {
        return new SimpleDateFormat("yyyy").format(currentDate);
    }

    private static String getYear() {
        return new SimpleDateFormat("yy").format(currentDate);
    }

    private static String getMonth() {
        return new SimpleDateFormat("MM").format(currentDate);
    }

    private static String getDay() {
        return new SimpleDateFormat("dd").format(currentDate);
    }

    private static String getHour() {
        return new SimpleDateFormat("HH").format(currentDate);
    }

    private static String getMinute() {
        return new SimpleDateFormat("mm").format(currentDate);
    }

    private static String getSecond() {
        return new SimpleDateFormat("ss").format(currentDate);
    }

    private static String getRandom(String pattern) {

        int length = 0;
        pattern = pattern.split(":")[1].trim();

        length = Integer.parseInt(pattern);
        String randomString = "";
        for(int i = 0; i < length; i++){
            randomString += String.valueOf(radixDigits[(int) (Math.random() * 36)]);
        }

        return randomString;
    }

    public static void main(String[] args) {
    }
}
