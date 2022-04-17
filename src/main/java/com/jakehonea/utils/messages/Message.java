package com.jakehonea.utils.messages;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jakehonea.utils.chat.ChatUtils;
import com.jakehonea.utils.config.ConfigHandler;
import com.jakehonea.utils.utils.Possible;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class Message {

    private static final Map<String, BiConsumer<Object, CommandSender>> HANDLERS;
    private static final Message EMPTY = builder().build();

    static {
        ConfigHandler.insert(ConfigHandler.ENCODER_MAP, Message.class, data -> {
                    if (data instanceof Message) {
                        return ((Message) data).map;
                    }
                    return data;
                }
        );
        ConfigHandler.insert(ConfigHandler.DECODER_MAP, Message.class, data -> {
                    if (data instanceof ConfigurationSection) {
                        Map<String, Object> map = Maps.newHashMap();
                        ((ConfigurationSection) data).getKeys(false).forEach(key -> {
                            map.put(key, ((ConfigurationSection) data).get(key));
                        });
                        return new Message(map);
                    }
                    return data;
                }
        );

        HANDLERS = Maps.newHashMap();
        {
            HANDLERS.put("message", (data, player) -> {
                if (data instanceof String) {
                    player.sendMessage(ChatUtils.replaceVariables((String) data, player, null));
                } else if (data instanceof List) {
                    for (Object obj : (List) data) {
                        if (obj instanceof String) {
                            player.sendMessage(ChatUtils.replaceVariables((String) obj, player,
                                    null));
                        }
                    }
                }
            });
            HANDLERS.put("title", (data, player) -> {
                if (data instanceof Map && player instanceof Player) {
                    Map<String, Object> titleData = (Map<String, Object>) data;
                    String header = Possible.of((String) titleData.get("header")).orElse("");
                    String footer = Possible.of((String) titleData.get("footer")).orElse("");
                    int fadeIn = (int) titleData.getOrDefault("fade-in", 10);
                    int stay = (int) titleData.getOrDefault("stay", 40);
                    int fadeOut = (int) titleData.getOrDefault("fade-out", 10);
                    ((Player) player).sendTitle(
                            ChatUtils.replaceVariables(header, player, null),
                            ChatUtils.replaceVariables(footer, player, null),
                            fadeIn, stay, fadeOut
                    );
                }
            });
            HANDLERS.put("action-bar", (data, player) -> {
                if (data instanceof String && player instanceof Player) {
                    ((Player) player).spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            TextComponent.fromLegacyText(ChatUtils.replaceVariables((String) data,
                                    player, null)));
                }
            });
        }
    }

    private Map<String, Object> map;

    private Message(Map<String, Object> map) {
        this.map = map;
    }

    public static Message empty() {
        return EMPTY;
    }

    public static Message of(String... lines) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", List.of(lines));

        return new Message(map);
    }

    public void sendMessage(CommandSender player) {
        map.entrySet().stream()
                .filter(e -> HANDLERS.containsKey(e.getKey()))
                .forEach(e -> HANDLERS.get(e.getKey()).accept(e.getValue(), player));
    }

    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    public static class MessageBuilder {

        private Map<String, Object> mapBuilder;

        private MessageBuilder() {
            this.mapBuilder = Maps.newHashMap();
        }

        public MessageBuilder setActionBar(String bar) {
            mapBuilder.put("action-bar", bar);
            return this;
        }

        public MessageBuilder setMessage(String... message) {
            mapBuilder.put("message", List.of(message));
            return this;
        }

        public MessageBuilder addMessage(String message) {
            List<String> list = (List<String>) mapBuilder.get("message");
            if (list == null) {
                list = Lists.newArrayList();
                mapBuilder.put("message", list);
            }
            list.add(message);
            return this;
        }

        public MessageBuilder setTitle(String header, String footer) {
            setTitle(header, footer, 10, 40, 10);
            return this;
        }

        public MessageBuilder setTitle(String header, String footer, int fadeIn, int stay, int fadeOut) {
            Map<String, Object> titleData = Maps.newHashMap();
            titleData.put("header", header);
            titleData.put("footer", footer);
            titleData.put("fadeIn", fadeIn);
            titleData.put("stay", stay);
            titleData.put("fadeOut", fadeOut);
            mapBuilder.put("title", titleData);
            return this;
        }

        public Message build() {
            return new Message(mapBuilder);
        }

    }


}
