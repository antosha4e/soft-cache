package com.tosh;

import java.util.List;

/**
 * User: arsentyev
 * Date: 20.07.12
 */
public class Test {
    public static void main(String... args) throws Exception {
        List<Object> objects = new SoftList<Object>();
        objects.add(new Object());
        objects.get(0);
    }
}