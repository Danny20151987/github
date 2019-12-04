package com.constellation.cancer.core.utils;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hudejian
 * @DESC
 * @date 2019/11/20 5:37 PM
 */
public class StringUtils {
    public static final String WHITE_SPACE = " \t\n\r";
    private static Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\s*\\{?\\s*([\\._0-9a-zA-Z]+)\\s*\\}?");

    public StringUtils() {
    }

    public static String captureName(String name) {
        char[] cs = name.toCharArray();
        cs[0] = (char)(cs[0] - 32);
        return String.valueOf(cs);
    }

    public static boolean isWhitespace(String s) {
        if (isEmpty(s)) {
            return true;
        } else {
            for(int i = 0; i < s.length(); ++i) {
                char c = s.charAt(i);
                if (WHITE_SPACE.indexOf(c) == -1) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static String replaceProperty(String expression, Properties params) {
        if (expression != null && expression.length() != 0 && expression.indexOf(36) >= 0) {
            Matcher matcher = VARIABLE_PATTERN.matcher(expression);

            StringBuffer sb;
            String value;
            for(sb = new StringBuffer(); matcher.find(); matcher.appendReplacement(sb, Matcher.quoteReplacement(value))) {
                String key = matcher.group(1);
                value = System.getenv(key);
                if (value == null || value.trim().equals("")) {
                    value = System.getProperty(key);
                }

                if (value == null && params != null) {
                    value = (String)params.get(key);
                }

                if (value == null) {
                    value = "";
                }
            }

            matcher.appendTail(sb);
            return sb.toString();
        } else {
            return expression;
        }
    }
}
