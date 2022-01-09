package com.jakehonea.utils.gui;

import com.google.common.collect.Lists;
import com.jakehonea.utils.Utils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class Gui {

    public static final Set<Gui> ACTIVE_GUIS;

    static {
        ACTIVE_GUIS = new HashSet<>();
    }

    @Getter
    @Setter
    private Inventory inventory;
    private List<Button> buttons;
    @Getter
    @Setter
    private Listener listener;

    public Gui() {
        this.buttons = Lists.newArrayList();
    }

    public void register() {
        if (listener != null) {
            Bukkit.getPluginManager().registerEvents(listener, Utils.INSTANCE);
            ACTIVE_GUIS.add(this);
            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onClick(InventoryClickEvent e) {
                    if (e.getInventory().equals(inventory)) {
                        if (!ACTIVE_GUIS.contains(Gui.this)) {
                            HandlerList.unregisterAll(this);
                        } else {
                            AtomicBoolean buttonClicked = new AtomicBoolean(false);
                            buttons.stream().filter(button -> button.getSlot().isPresent() && button.getSlot().get() == e.getSlot())
                                    .forEach(button -> {
                                        if (button.getOnClick().isPresent()) {
                                            button.getOnClick().get().accept(e);
                                            buttonClicked.set(true);
                                        }
                                    });
                            if (buttonClicked.get()) {
                                buttons.forEach(button -> button.getOnOtherButtonClick().ifPresent(consumer -> consumer.accept(e)));
                            }
                        }
                    }
                }
            }, Utils.INSTANCE);
        }
    }


    public void unregister() {
        if (listener != null) {
            HandlerList.unregisterAll(listener);
            ACTIVE_GUIS.remove(this);
        }
    }

    public void addButton(Button button) {
        this.buttons.add(button);
    }

    public void close() {
        inventory.getViewers().forEach(HumanEntity::closeInventory);
    }
}