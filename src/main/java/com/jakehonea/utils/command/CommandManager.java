package com.jakehonea.utils.command;

import com.google.common.collect.Lists;
import com.jakehonea.utils.Utils;
import com.jakehonea.utils.messages.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CommandManager implements CommandExecutor {

    private Utils utils;
    private List<Command> commands;

    private Message invalidArg = Message.builder()
            .setMessage("&cInvalid argument!")
            .build();

    public CommandManager(Utils utils) {
        this.utils = utils;
        this.commands = Lists.newArrayList();
    }

    /**
     * Registers a command into the Bukkit command
     *
     * @param command the command to register
     */
    public void registerCommand(Command command) {
        if (command != null) {
            command.getName().ifPresent(name -> {
                this.commands.add(command);
                PluginCommand pluginCommand = utils.getCommand(name);
                pluginCommand.setExecutor(this);
                pluginCommand.setName(name);
                command.getUseBasicTabCompleter().ifPresentOrElse(val -> pluginCommand.setTabCompleter(buildCompleter(command)),
                        () -> command.getTabCompleter().ifPresent(pluginCommand::setTabCompleter));
                command.getDescription().ifPresent(pluginCommand::setDescription);
                command.getUsage().ifPresent(pluginCommand::setUsage);
            });
        }
    }

    /**
     * Builds a tab completer based on the subarguments that the command contains.
     * The function assumes if the current command/subcommand has no arguments, that
     * the command wants a username input.
     * </p>
     * {@link com.jakehonea.utils.command.Command.CommandBuilder#useBasicTabCompleter(boolean true)}
     *
     * @param command
     * @return
     */
    private TabCompleter buildCompleter(Command command) {
        return (sender, cmd, alias, args) -> {
            List<String> completions = new ArrayList<>();
            Command starting = command;
            if (args.length > 1) {
                boolean notFound = false;
                int depth = 0;
                while (!notFound && depth < args.length - 1 && starting.getArguments().isPresent()) {
                    int temp = depth;
                    for (SubCommand sub : starting.getArguments().get()) {
                        if (args[depth].equalsIgnoreCase(sub.getName().get())) {
                            starting = sub;
                            depth++;
                            break;
                        }
                    }
                    if (temp == depth) {
                        notFound = true;
                    }
                }
                // falls out of the subcommand tree, return empty
                if (notFound) {
                    return new ArrayList<>();
                }
            }

            starting.getArguments().ifPresentOrElse(subargs -> {
                for (SubCommand arg : subargs) {
                    if (!arg.getPermission().isPresent() ||
                            (arg.getPermission().isPresent() && sender.hasPermission(arg.getPermission().get()))) {
                        completions.add(arg.getName().get());
                    }
                }
            }, () -> completions.addAll(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList())));


            return StringUtil.copyPartialMatches(args[args.length - 1], completions, new ArrayList<>());
        };
    }

    /**
     * Traverses through all the command's possible arguments to find
     * a proper way to execute the given command ({@link Consumer<CommandPacket>})
     *
     * @param command the appropriate command data given the command line
     * @param packet  information regarding the environment and command line
     * @param depth   the subargument layer at which the function is current at
     * @return an optional that is present if the traversal was successful
     * an empty optional if the function didn't find a proper way execution of the
     * given command
     */
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


}
