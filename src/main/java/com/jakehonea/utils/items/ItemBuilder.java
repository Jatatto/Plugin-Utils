package com.jakehonea.utils.items;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jakehonea.utils.chat.ChatUtils;
import com.jakehonea.utils.config.ConfigHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public final class ItemBuilder {

    public static final Player NO_PLAYER = null;

    static {
        ConfigHandler.insert(ConfigHandler.ENCODER_MAP, ItemBuilder.class, builder ->
                convertItem(((ItemBuilder) builder).build()));
        ConfigHandler.insert(ConfigHandler.ENCODER_MAP, ItemStack.class, item ->
                convertItem((ItemStack) item));
        ConfigHandler.insert(ConfigHandler.DECODER_MAP, ItemStack.class, section ->
                new ItemBuilder((ConfigurationSection) section).build());
        ConfigHandler.insert(ConfigHandler.DECODER_MAP, ItemBuilder.class, section ->
                new ItemBuilder((ConfigurationSection) section));
    }

    private int amount;
    private String name;
    private List<String> lore;
    private Material material;
    private boolean enchanted = false;
    private short customModel = -1;
    private final Map<String, String> variables = Maps.newHashMap();

    public ItemBuilder(Material material, int amount) {
        this.material = material;
        this.amount = amount;
    }

    public ItemBuilder(Material material) {
        this(material, 1);
    }

    public ItemBuilder(ConfigurationSection section) {
        this.material = Material.valueOf(section.getString("material", "STONE").toUpperCase());
        this.amount = section.getInt("amount", 1);
        this.name = section.getString("name");
        this.customModel = (short) section.getInt("custom-model", -1);
        this.enchanted = section.getBoolean("enchanted", false);
        this.lore = section.getStringList("lore");
    }

    public ItemBuilder addVariable(String key, String value) {
        variables.put(key, value);
        return this;
    }

    public ItemBuilder material(Material material) {
        this.material = material;
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder lore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemBuilder lore(String... lines) {
        if (this.lore == null) {
            this.lore = Lists.newArrayList();
        }
        lore.addAll(Arrays.asList(lines));
        return this;
    }

    public ItemBuilder enchanted(boolean enchanted) {
        this.enchanted = enchanted;
        return this;
    }

    public ItemStack build() {
        return this.build(NO_PLAYER);
    }

    public ItemStack build(Player player) {
        ItemStack item = new ItemStack(this.material, this.amount);
        ItemMeta itemMeta = item.getItemMeta();
        if (this.name != null) {
            itemMeta.setDisplayName(parseString(player,this.name));
        }
        if (this.lore != null) {
            List<String> itemLore = Lists.newArrayList();
            this.lore.stream().map(s->parseString(player, s))
                    .map(line -> Arrays.asList(line.split("\\n")))
                    .forEach(itemLore::addAll);
            itemMeta.setLore(itemLore);
        }
        if (this.enchanted) {
            itemMeta.addEnchant(Enchantment.DURABILITY, 0, true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        if (this.customModel != -1) {
            item.setDurability(this.customModel);
            itemMeta.setUnbreakable(true);
            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        return item;
    }

    private String parseString(Player player, String string) {
        if (string == null) {
            return null;
        }
        return ChatUtils.color(ChatUtils.replaceVariables(string, player, variables));
    }

    public static Map<String, Object> convertItem(ItemStack item) {
        Map<String, Object> info = new HashMap<>();
        info.put("material", item.getType().name());
        info.put("amount", item.getAmount());
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasDisplayName())
                info.put("name", meta.getDisplayName());
            if (meta.hasLore())
                info.put("lore", meta.getLore());
            if (meta.isUnbreakable())
                info.put("custom-model", item.getDurability());
        }
        return info;
    }

}
