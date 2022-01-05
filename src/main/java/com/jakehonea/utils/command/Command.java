package com.jakehonea.utils.command;

import com.google.common.collect.Maps;
import com.jakehonea.utils.messages.Message;
import com.jakehonea.utils.utils.Possible;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class Command {

    @Getter
    @NonNull
    private Optional<String> name;
    @Getter
    @NonNull
    private Optional<String> usage;
    @Getter
    @NonNull
    private Optional<String> permission;
    @Getter
    @NonNull
    private Optional<String> description;
    @Getter
    @NonNull
    private Optional<Message> helpMessage;
    @Getter
    @NonNull
    private Optional<TabCompleter> tabCompleter;
    @Getter
    @NonNull
    private Optional<Message> noPermissionMessage;
    @Getter
    @NonNull
    private Optional<Map<String, Argument>> arguments;
    @Getter
    @NonNull
    private Optional<BiConsumer<CommandSender, String[]>> onRun;

    private Command() {

    }

    public static CommandBuilder builder() {
        return new CommandBuilder();
    }

    public interface Argument {
        void run(CommandSender sender, String[] bits);
    }

    public static class CommandBuilder {

        private String name;
        private String usage;
        private String permission;
        private String description;
        private Message helpMessage;
        private TabCompleter tabCompleter;
        private Message noPermissionMessage;
        private Map<String, Argument> arguments;
        private BiConsumer<CommandSender, String[]> onRun;

        public CommandBuilder name(String name) {
            this.name = name;
            return this;
        }

        public CommandBuilder usage(String usage) {
            this.usage = usage;
            return this;
        }

        public CommandBuilder permission(String permission) {
            this.permission = permission;
            return this;
        }

        public CommandBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CommandBuilder helpMessage(Message message) {
            this.helpMessage = message;
            return this;
        }

        public CommandBuilder addArgument(String name, Argument argument) {
            if (this.arguments == null) {
                this.arguments = Maps.newHashMap();
            }
            arguments.put(name, argument);
            return this;
        }

        public CommandBuilder tabCompleter(TabCompleter tabCompleter) {
            this.tabCompleter = tabCompleter;
            return this;
        }

        public CommandBuilder noPermissionMessage(Message message) {
            this.noPermissionMessage = message;
            return this;
        }

        public CommandBuilder onRun(BiConsumer<CommandSender, String[]> consumer) {
            this.onRun = consumer;
            return this;
        }

        public Command build() {
            Command command = new Command();
            try {
                for (Field field : getClass().getDeclaredFields()) {
                    if (!field.canAccess(this)) {
                        field.setAccessible(true);
                    }
                    Field f = command.getClass().getDeclaredField(field.getName());
                    if (!f.canAccess(command)) {
                        f.setAccessible(true);
                    }
                    f.set(command, Possible.of(field.get(this)));
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
            return command;
        }

    }

}
