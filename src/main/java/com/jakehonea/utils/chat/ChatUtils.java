package com.jakehonea.utils.chat;

import com.jakehonea.utils.Utils;
import com.jakehonea.utils.config.ConfigHandler;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatUtils {

    public static final char CHAT_COLOR_SYMBOL = '&';

    static {
        ConfigHandler.insert(ConfigHandler.DECODER_MAP, String.class, obj -> color((String) obj));
        ConfigHandler.insert(ConfigHandler.DECODER_MAP, List.class, obj -> {
            List<?> list = (List<?>) obj;
            if (list.size() > 0 && list.get(0) instanceof String) {
                return list.stream().map(s -> color((String) s)).collect(Collectors.toList());
            }
            return list;
        });
    }

    public static String replaceVariables(String string, CommandSender player,
            Map<String, String> variables) {
        if (string == null) {
            return "";
        }
        if (variables != null) {
            for (Map.Entry<String, String> variable : variables.entrySet()) {
                string = string.replace(variable.getKey(), variable.getValue());
            }
        }
        if (Utils.HAS_PLACEHOLDER_API && player instanceof Player) {
            string = PlaceholderAPI.setPlaceholders((Player) player, string);
        }
        return string;
    }

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes(CHAT_COLOR_SYMBOL, string);
    }

}
