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

    /**
     * Allows a way for a cleaner and direct way for the user to cast the sender
     *
     * @param type the class to cast the {@link CommandSender} instance to
     * @param <T>  the type to return
     * @return     a casted instance of {@link CommandSender} to  type {@link T}
     */
    public <T extends CommandSender> T getSenderAs(Class<T> type) {
        if (!(type.isInstance(sender))) {
            throw new ClassCastException("cannot cast sender as " + type.getName());
        }
        return type.cast(sender);
    }

    /**
     * To prevent memory leaks, we must null out the CommandSender instance
     */
    @Override
    public void close() {
        this.sender = null;
        this.args = null;
    }

}
