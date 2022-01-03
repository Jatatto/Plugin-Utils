package com.jakehonea.utils;

import com.jakehonea.utils.command.CommandManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Utils extends JavaPlugin {

    public static boolean HAS_PLACEHOLDER_API;

    @Getter
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        HAS_PLACEHOLDER_API = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

        this.commandManager = new CommandManager(this);

        load();
    }

    @Override
    public void onDisable() {
        unload();
    }

    public abstract void load();

    public abstract void unload();

}
