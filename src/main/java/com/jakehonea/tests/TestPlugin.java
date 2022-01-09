package com.jakehonea.tests;

import com.jakehonea.utils.Utils;

public class TestPlugin extends Utils {
    @Override
    public void load() {
        new TestCommand(this);
    }

    @Override
    public void unload() {

    }
}
