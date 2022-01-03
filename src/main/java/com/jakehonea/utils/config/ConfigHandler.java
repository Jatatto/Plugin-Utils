package com.jakehonea.utils.config;

import com.jakehonea.utils.utils.Possible;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class ConfigHandler {

    public static final String EMPTY_PATH = "";
    public static final boolean DECODER_MAP = false;
    public static final boolean ENCODER_MAP = true;

    private static final Map<Class<?>, Function<Object, ?>> DECODER;
    private static final Map<Class<?>, Function<Object, ?>> ENCODER;

    static {
        DECODER = new HashMap<>();
        ENCODER = new HashMap<>();
    }

    public static void insert(boolean encoder, Class<?> type, Function<Object, ?> mapper) {
        (encoder ? ENCODER : DECODER).put(type, mapper);
    }

    private static Optional<Object> fetch(Class<?> fieldType, Object value,
                                          final Map<Class<?>, Function<Object, ?>> map) {
        return Possible.of(
                map.containsKey(fieldType) ?
                        map.get(fieldType).apply(value) :
                        value
        );
    }

    public static void reload(ConfigFile configFile, String path) {
        try {
            reload(path, configFile, configFile.getFile());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setPresets(ConfigFile configFile, String path) {
        try {
            setPresets(path, configFile, configFile.getFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> void reload(String path, T clazz, File file) throws IllegalAccessException {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        for (Field f : clazz.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(ConfigValue.class)) {
                ConfigValue configAnnotation = f.getAnnotation(ConfigValue.class);
                String key = configAnnotation.value().length() == 0 ? f.getName() :
                        configAnnotation.value();
                if (!configuration.isSet(path + key))
                    continue;
                Object value = configuration.get(path + key);
                if (!f.canAccess(clazz)) {
                    f.setAccessible(true);
                }
                f.set(clazz, fetch(f.getType(), value, DECODER));
            }
        }
    }

    public static <T> void setPresets(String path, T clazz, File file) throws Exception {
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
        AtomicBoolean updated = new AtomicBoolean(false);
        for (Field f : clazz.getClass().getDeclaredFields()) {
            if (f.isAnnotationPresent(ConfigValue.class)) {
                ConfigValue configAnnotation = f.getAnnotation(ConfigValue.class);
                String key = configAnnotation.value().length() == 0 ? f.getName() :
                        configAnnotation.value();
                if (!configuration.isSet(path + key)) {
                    Object value = f.get(clazz);
                    if (!f.canAccess(clazz)) {
                        f.setAccessible(true);
                    }
                    fetch(f.getType(), value, ENCODER).ifPresent(obj -> {
                        configuration.set(path + key, obj);
                        updated.set(true);
                    });
                }
            }
        }
        if (updated.get()) {
            configuration.save(file);
        }
    }

}