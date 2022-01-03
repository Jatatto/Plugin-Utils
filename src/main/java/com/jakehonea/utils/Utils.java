package com.jakehonea.utils;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Utils extends JavaPlugin {

    public static boolean HAS_PLACEHOLDER_API;

    @Override
    public void onEnable() {
        HAS_PLACEHOLDER_API = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        load();
    }

    @Override
    public void onDisable() {
        unload();
    }

    public abstract void load();

    public abstract void unload();

}
