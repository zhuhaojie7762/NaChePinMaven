package com.aichebaba.rooftop.utils;

import java.text.DecimalFormat;

public class StringUtils {

    private static final DecimalFormat numberFormat = new DecimalFormat("00000");

    public static String likeValue(String val) {
        if (val == null || val.trim().length() == 0) {
            return val;
        } else {
            val = val.trim();
            return val.replaceAll("%", "") + "%";
        }
    }

    public static String likeAllValue(String val) {
        if (val == null || val.trim().length() == 0) {
            return val;
        } else {
            val = val.trim();
            return "%" + val.replaceAll("%", "") + "%";
        }
    }

    public static String digitFormat(long v) {
        return numberFormat.format(v);
    }

    public static String inSql(int size) {
        StringBuilder sb = new StringBuilder();
        sb.append(" (");
        for (int i = 0; i < size; i++) {
            sb.append("?,");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }

    /**
     * <p>
     * Checks if a CharSequence is whitespace, empty ("") or null.
     * </p>
     * <p>
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is null, empty or whitespace
     * @since 3.0 Changed signature from isBlank(String) to isBlank(CharSequence)
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * Checks if a CharSequence is not empty (""), not null and not whitespace only.
     * </p>
     * <p>
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param cs the CharSequence to check, may be null
     * @return {@code true} if the CharSequence is
     * not empty and not null and not whitespace
     * @since 3.0 Changed signature from isNotBlank(String) to isNotBlank(CharSequence)
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }
}
