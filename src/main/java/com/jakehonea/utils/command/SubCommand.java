package com.jakehonea.utils.command;

import lombok.NonNull;
import org.bukkit.command.TabCompleter;

import java.util.Optional;

public class SubCommand extends Command {

    protected SubCommand() {
    }

    /**
     * Override the tab completer since this
     * is a {@link Command} exclusive feature
     * </p>
     *
     * @return an empty optional tab completer
     */
    @Override
    public @NonNull Optional<TabCompleter> getTabCompleter() {
        return Optional.empty();
    }

}
