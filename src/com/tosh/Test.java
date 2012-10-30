package com.tosh;

import com.tosh.data.SoftArrayList;

import java.util.List;

/**
 * User: arsentyev
 * Date: 20.07.12
 */
public class Test {
    public static void main(String... args) throws Exception {
        List<MyClass> objects = new SoftArrayList<MyClass>();

        for(int i = 0; i < 10; i++) {
            objects.add(new MyClass(i));
        }

        objects.add(new MyClass(1));
        objects.add(new MyClass(3));

        objects.remove(1);

        System.out.println(objects.get(2).id);
        System.out.println(objects.size());
    }

    private static class MyClass {
        int id;

        MyClass(int id) {
            this.id = id;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj!= null && obj.getClass() == MyClass.class) {
                return ((MyClass)obj).id == this.id;
            }
            return false;
        }
    }
}