package com.pei.plaformplugin.annotation;

public class ExtraFinder {

    private static final BooleanFinder BOOLEAN_FINDER = new BooleanFinder();
    private static final IntegerFinder INTEGER_FINDER = new IntegerFinder();
    private static final LongFinder LONG_FINDER = new LongFinder();
    private static final DoubleFinder DOUBLE_FINDER = new DoubleFinder();
    private static final StringFinder STRING_FINDER = new StringFinder();
    private static final ClassFinder CLASS_FINDER = new ClassFinder();

    public static boolean findBoolean(Extra[] extras, String key, boolean defaultValue) {
        return find(extras, key, BOOLEAN_FINDER, defaultValue);
    }

    public static int findInteger(Extra[] extras, String key, int defaultValue) {
        return find(extras, key, INTEGER_FINDER, defaultValue);
    }

    public static long findLong(Extra[] extras, String key, long defaultValue) {
        return find(extras, key, LONG_FINDER, defaultValue);
    }

    public static double findDouble(Extra[] extras, String key, double defaultValue) {
        return find(extras, key, DOUBLE_FINDER, defaultValue);
    }

    public static String findString(Extra[] extras, String key, String defaultValue) {
        return find(extras, key, STRING_FINDER, defaultValue);
    }

    public static Class<?> findClass(Extra[] extras, String key) {
        return find(extras, key, CLASS_FINDER, null);
    }

    public static <T> T find(Extra[] extras, String key, Finder<T> finder, T defaultValue) {
        for (Extra extra : extras) {
            if (extra.key().equals(key)) return finder.find(extra);
        }
        return defaultValue;
    }

    public interface Finder<T> {
        T find(Extra extra);
    }

    static class BooleanFinder implements Finder<Boolean> {
        @Override
        public Boolean find(Extra extra) {
            return extra.booleanValue();
        }
    }

    static class IntegerFinder implements Finder<Integer> {
        @Override
        public Integer find(Extra extra) {
            return extra.intValue();
        }
    }

    static class LongFinder implements Finder<Long> {
        @Override
        public Long find(Extra extra) {
            return extra.longValue();
        }
    }

    static class DoubleFinder implements Finder<Double> {

        @Override
        public Double find(Extra extra) {
            return extra.doubleValue();
        }
    }

    static class StringFinder implements Finder<String> {

        @Override
        public String find(Extra extra) {
            return extra.stringValue();
        }
    }

    static class ClassFinder implements Finder<Class<?>> {

        @Override
        public Class<?> find(Extra extra) {
            return extra.classValue();
        }
    }
}
