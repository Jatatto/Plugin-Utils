package com.jakehonea.utils.command;

import lombok.NonNull;
import org.bukkit.command.TabCompleter;

import java.util.Optional;

public class SubCommand extends Command {

    @Override
    public @NonNull Optional<TabCompleter> getTabCompleter() {
        return Optional.empty();
    }

}
