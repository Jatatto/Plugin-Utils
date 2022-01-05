package com.jakehonea.utils.command;

import com.google.common.collect.Lists;
import com.jakehonea.utils.Utils;
import com.jakehonea.utils.messages.Message;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class CommandManager implements CommandExecutor {

    private Utils utils;
    private List<Command> commands;

    private Message invalidArg = Message.builder()
            .setMessage("&cInvalid argument!")
            .build();

    public CommandManager(Utils utils) {
        this.utils = utils;
        this.commands = Lists.newArrayList();

        registerCommand(Command.builder()
                .name("test")
                .permission("Permission.test")
                .addArgument(
                        SubCommand.builder()
                                .name("this")
                                .onRun(packet -> packet.getSender().sendMessage("you ran the test" +
                                        " subarg"))
                                .build(SubCommand.class)
                )
                .build()
        );

    }

    public void registerCommand(Command command) {
        if (command != null) {
            command.getName().ifPresent(name -> {
                this.commands.add(command);
                PluginCommand pluginCommand = utils.getCommand(name);
                pluginCommand.setExecutor(this);
                pluginCommand.setName(name);
                command.getTabCompleter().ifPresent(pluginCommand::setTabCompleter);
                command.getDescription().ifPresent(pluginCommand::setDescription);
                command.getUsage().ifPresent(pluginCommand::setUsage);
            });
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command command, @NotNull String s, @NotNull String[] strings) {
        Optional<Command> found =
                commands.stream().filter(cmd -> cmd.getName().get().equalsIgnoreCase(command.getName())).findFirst();

        if (found.isPresent()) {
            Command custom = found.get();
            try (CommandPacket packet = new CommandPacket(commandSender, strings)) {

                Optional<Consumer<CommandPacket>> response =
                        attemptArgs(custom, packet, 0);

                response.ifPresentOrElse(
                        consumer -> consumer.accept(packet),
                        () -> invalidArg.sendMessage(packet.getSender())
                );
            }
        }
        return true;
    }

    private Optional<Consumer<CommandPacket>> attemptArgs(Command command, CommandPacket packet,
                                                          int depth) {
        if (command.getArguments().isPresent() && packet.getArgs().length > depth) {
            if (command.getPermission().isPresent() && !packet.getSender().hasPermission(command.getPermission().get())) {
                return Optional.of(p -> command.getNoPermissionMessage().ifPresent(msg -> msg.sendMessage(packet.getSender())));
            }
            Optional<SubCommand> found = command.getArguments().get().stream()
                    .filter(arg -> arg.getName().isPresent() && packet.getArgs()[depth].equalsIgnoreCase(arg.getName().get()))
                    .findFirst();
            if (found.isPresent()) {
                return attemptArgs(found.get(), packet, depth + 1);
            }
        }
        if (command.getPermission().isPresent() && !packet.getSender().hasPermission(command.getPermission().get())) {
            return Optional.of(p -> command.getNoPermissionMessage().ifPresent(msg -> msg.sendMessage(packet.getSender())));
        }
        if (command.getExecutor().isPresent()) {
            return Optional.of(command.getExecutor().get());
        }
        if (command.getArguments().isPresent() && command.getArguments().get().size() > 0) {
            return Optional.of(p -> command.getHelpMessage().ifPresent(msg -> msg.sendMessage(packet.getSender())));
        }
        return Optional.empty();
    }


}
