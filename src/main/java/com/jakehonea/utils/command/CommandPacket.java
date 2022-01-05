package com.jakehonea.utils.command;

import lombok.Getter;
import org.bukkit.command.CommandSender;

public class CommandPacket implements AutoCloseable {

    @Getter
    private CommandSender sender;
    @Getter
    private String[] args;

    public CommandPacket(CommandSender sender, String[] args) {
        this.sender = sender;
        this.args = args;
    }

    public <T extends CommandSender> T getSenderAs(Class<T> type) {
        return (T) (sender);
    }

    @Override
    public void close() {
        this.sender = null;
        this.args = null;
    }

}
