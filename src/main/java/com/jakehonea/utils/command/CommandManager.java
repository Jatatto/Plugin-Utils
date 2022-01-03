package com.jakehonea.utils.command;

import com.google.common.collect.Lists;
import com.jakehonea.utils.Utils;
import com.jakehonea.utils.messages.Message;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

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
                .addArgument("apply", (p, args) -> p.sendMessage("you have ran the apply subarg"))
                .build()
        );

    }

    public void registerCommand(Command command) {
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

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull org.bukkit.command.Command command, @NotNull String s, @NotNull String[] strings) {

        Optional<Command> found =
                commands.stream().filter(cmd -> cmd.getName().get().equalsIgnoreCase(command.getName())).findFirst();

        if (found.isPresent()) {
            Command custom = found.get();
            if (!custom.getPermission().isPresent() && !commandSender.hasPermission(custom.getPermission().get())) {
                custom.getNoPermissionMessage().ifPresent(msg -> msg.sendMessage(commandSender));
                return true;
            }
            if (strings.length == 0) {
                custom.getOnRun().ifPresentOrElse(action -> action.accept(commandSender, strings),
                        () -> custom.getHelpMessage().ifPresent(msg -> msg.sendMessage(commandSender)));
            } else {
                custom.getArguments().ifPresentOrElse(map -> {
                    if (map.containsKey(strings[0])) {
                        map.get(strings[0]).run(commandSender, strings);
                    }
                }, () -> custom.getOnRun().ifPresentOrElse(action -> action.accept(commandSender, strings),
                        () -> invalidArg.sendMessage(commandSender)));
            }
        }

        return true;
    }

}
