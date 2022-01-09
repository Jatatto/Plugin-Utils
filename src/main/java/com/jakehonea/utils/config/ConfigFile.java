package com.jakehonea.utils.config;

import com.jakehonea.utils.Utils;
import com.jakehonea.utils.utils.Possible;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;

public class ConfigFile {

    private final Utils utils;

    private final File file;
    private final YamlConfiguration yamlConfiguration;

    public ConfigFile(Utils utils, String fileName) {
        this.utils = utils;
        file = new File(utils.getDataFolder() + "/" + fileName + ".yml");
        checkFile(fileName);
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        setup();
    }

    public void setup() {
        Possible.of(getClass().getAnnotation(ConfigInfo.class))
                .ifPresentOrElse(
                        info -> reload(info.value()),
                        () -> reload(ConfigHandler.EMPTY_PATH)
                );
    }

    private void reload(String path){
        ConfigHandler.setPresets(this, path);
        ConfigHandler.reload(this, path);
    }

    protected void save() throws IOException {
        getConfig().save(getFile());
    }

    protected File getFile() {
        return file;
    }

    protected YamlConfiguration getConfig() {
        return yamlConfiguration != null ? yamlConfiguration : YamlConfiguration.loadConfiguration(file);
    }

    private boolean checkFile(String fileName) {
        if (!file.exists()) {
            if (!utils.getDataFolder().exists()) {
                utils.getDataFolder().mkdir();
            }
            if (utils.getResource(fileName + ".yml") != null) {
                utils.saveResource(fileName + ".yml", true);
            } else {
                try {
                    file.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
        return false;
    }

}
