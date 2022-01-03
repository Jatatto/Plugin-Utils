package com.jakehonea.utils.messages;

import com.jakehonea.utils.Utils;
import com.jakehonea.utils.config.ConfigFile;

public abstract class Messages extends ConfigFile {

    public Messages(Utils utils, String fileName) {
        super(utils, fileName);
    }
}
