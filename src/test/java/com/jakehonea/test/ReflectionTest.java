package com.jakehonea.test;

import java.lang.reflect.Field;

public class ReflectionTest {

    public ReflectionTest() throws Exception {
        System.out.println("reading fields");
        for (Field f : getClass().getDeclaredFields()) {
            System.out.println(f.getName() + " = " + f.get(this));
        }
    }

    public static void main(String[] args) throws Exception {
        new ReflectionTestEx();
    }

}
