package com.jakehonea.utils;

import com.jakehonea.utils.command.CommandManager;
import com.jakehonea.utils.gui.Gui;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Utils extends JavaPlugin {

    public static Utils INSTANCE;
    public static boolean HAS_PLACEHOLDER_API;

    @Getter
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        INSTANCE = this;
        HAS_PLACEHOLDER_API = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        this.commandManager = new CommandManager(this);
        load();
    }

    @Override
    public void onDisable() {
        if (Gui.ACTIVE_GUIS.size() > 0) {
            Gui.ACTIVE_GUIS.stream().filter(gui -> gui.getInventory() != null)
                    .forEach(Gui::close);
        }
        unload();
    }

    public abstract void load();

    public abstract void unload();

}
