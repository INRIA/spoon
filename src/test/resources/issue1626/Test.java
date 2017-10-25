package com.company.test;

public class Test {
    public Test() {
        final ClassLoader loader = getClass().getClassLoader();
        loader.getResourceA‌sStream("filename.xsl");
    }
}