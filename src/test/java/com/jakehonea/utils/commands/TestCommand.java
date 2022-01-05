package com.jakehonea.utils.commands;

import com.jakehonea.utils.Utils;
import com.jakehonea.utils.command.Command;
import com.jakehonea.utils.command.SubCommand;
import com.jakehonea.utils.config.ConfigFile;
import com.jakehonea.utils.config.ConfigValue;
import com.jakehonea.utils.messages.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TestCommand extends ConfigFile {

    @ConfigValue
    Message helpMessage = Message.of(
            "&aHelp Message:",
            " &8> /&atest fly - toggle fly",
            " &8> /&atest heal - heal yourself"
    );
    @ConfigValue
    Message flyNoPerm = Message.builder()
            .setActionBar(ChatColor.RED + "You do not have permission to toggle fly!")
            .build();
    @ConfigValue
    Message enabledFly = Message.builder()
            .setActionBar(ChatColor.GREEN + "Flight enabled!")
            .build();
    @ConfigValue
    Message disabledFly = Message.builder()
            .setActionBar(ChatColor.RED + "Flight disabled!")
            .build();
    @ConfigValue
    Message healed = Message.builder()
            .setActionBar(ChatColor.RED + "You have healed yourself!")
            .build();

    public TestCommand(Utils utils) {
        super(utils, "messages");
        utils.getCommandManager().registerCommand(Command.builder()
                .name("test")
                .helpMessage(helpMessage)
                .addArgument(SubCommand.builder()
                        .name("fly")
                        .onRun(packet -> {
                            if (packet.getSender() instanceof Player) {
                                Player player = packet.getSenderAs(Player.class);
                                (player.isFlying() ? disabledFly : enabledFly).sendMessage(player);
                                player.setFlying(!player.isFlying());
                            }
                        })
                        .permission("admin.perm")
                        .noPermissionMessage(flyNoPerm)
                        .build(SubCommand.class)
                )
                .addArgument(SubCommand.builder()
                        .name("heal")
                        .onRun(packet -> {
                            if (packet.getSender() instanceof Player) {
                                Player player = packet.getSenderAs(Player.class);
                                player.setHealth(player.getMaxHealth());
                                healed.sendMessage(player);
                            }
                        })
                        .build(SubCommand.class)
                ).build()
        );
    }

}
