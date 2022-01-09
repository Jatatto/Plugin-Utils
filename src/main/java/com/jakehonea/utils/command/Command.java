package com.jakehonea.utils.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jakehonea.utils.messages.Message;
import com.jakehonea.utils.utils.Possible;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Command {

    @Getter
    @NonNull
    protected Optional<String> name;
    @Getter
    @NonNull
    protected Optional<String> usage;
    @Getter
    @NonNull
    protected Optional<String> permission;
    @Getter
    @NonNull
    protected Optional<String> description;
    @Getter
    @NonNull
    protected Optional<Message> helpMessage;
    @Getter
    @NonNull
    protected Optional<TabCompleter> tabCompleter;
    @Getter
    @NonNull
    protected Optional<List<SubCommand>> arguments;
    @Getter
    @NonNull
    protected Optional<Message> noPermissionMessage;
    @Getter
    @NonNull
    protected Optional<Boolean> useBasicTabCompleter;
    @Getter
    @NonNull
    protected Optional<Consumer<CommandPacket>> executor;

    protected Command() {
    }

    public static CommandBuilder builder() {
        return new CommandBuilder();
    }

    public static class CommandBuilder {

        private String name;
        private String usage;
        private String permission;
        private String description;
        private Message helpMessage;
        private TabCompleter tabCompleter;
        private List<SubCommand> arguments;
        private Message noPermissionMessage;
        private boolean useBasicTabCompleter;
        private Consumer<CommandPacket> executor;

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

        public CommandBuilder addArgument(SubCommand subCommand) {
            if (this.arguments == null) {
                this.arguments = Lists.newArrayList();
            }
            arguments.add(subCommand);
            return this;
        }

        public CommandBuilder tabCompleter(TabCompleter tabCompleter) {
            this.tabCompleter = tabCompleter;
            return this;
        }

        public CommandBuilder useBasicTabCompleter(boolean value) {
            this.useBasicTabCompleter = value;
            return this;
        }

        public CommandBuilder noPermissionMessage(Message message) {
            this.noPermissionMessage = message;
            return this;
        }

        public CommandBuilder onRun(Consumer<CommandPacket> consumer) {
            this.executor = consumer;
            return this;
        }

        public Command build() {
            return build(Command.class);
        }

        public <T extends Command> T build(Class<T> type) {
            T command = null;
            try {
                Constructor<?> constructor = type.getDeclaredConstructors()[0];
                constructor.setAccessible(true);
                command = (T) constructor.newInstance();
                Class<?> basis = command.getClass().getSuperclass() == Command.class ?
                        command.getClass().getSuperclass() : command.getClass();
                for (Field field : getClass().getDeclaredFields()) {
                    Field f = basis.getDeclaredField(field.getName());
                    f.setAccessible(true);
                    f.set(command, Possible.of(field.get(this)));
                }
            } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException
                    | InstantiationException ignored) {
            }
            return command;
        }
    }

}
