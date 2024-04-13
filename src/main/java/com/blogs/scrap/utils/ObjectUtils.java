package com.blogs.scrap.utils;

public class ObjectUtils {

    public static boolean isNonNull(final Object value) {
        return value != null;
    }

    public static boolean areNonNull(final Object... values) {
        if (!isNonNull(values) || values.length == 0) {
            return false;
        }
        for (Object value : values) {
            if (!isNonNull(value)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValid(final String value) {
        return isNonNull(value) && !value.trim().isEmpty();
    }

    public static boolean areValid(final String... values) {
        if (!isNonNull(values) || values.length == 0) {
            return false;
        }
        for (String value : values) {
            if (!isValid(value)) {
                return false;
            }
        }
        return true;
    }
}
