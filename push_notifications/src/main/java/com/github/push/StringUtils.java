package com.github.push;

public class StringUtils {

    //camel-case to snake-case
    public static String convertToSnakeCase(String camelCase) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append("_");
                sb.append(Character.toLowerCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    //snake-case to camel-case
    public static String convertToCamelCase(String snakeCase) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < snakeCase.length(); i++) {
            char c = snakeCase.charAt(i);
            if (c == '_') {
                i++;
                sb.append(Character.toUpperCase(snakeCase.charAt(i)));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    //capitalize first letter
    public static String capitalizeFirstLetter(String original) {
        if (original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    //massage strings
    public static String capitalizeString(String str) {
        str = convertToCamelCase(str);
        str = convertToSnakeCase(str);
        String[] arr = str.split("_");
        str = "";
        StringBuilder sb = new StringBuilder();
        for (String s : arr) {
            sb.append(capitalizeFirstLetter(s));
            sb.append(" ");
        }
        return sb.toString().trim();
    }
}
