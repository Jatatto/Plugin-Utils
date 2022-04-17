package com.jakehonea.utils.gui;

import com.jakehonea.utils.utils.Possible;
import lombok.Getter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;

import java.lang.reflect.Field;
import java.util.Optional;

@Getter
public class Button {

    private Optional<Integer> slot;
    private Optional<ItemStack> item;
    private Optional<Consumer<InventoryClickEvent>> onClick;
    private Optional<Consumer<InventoryClickEvent>> onOtherButtonClick;

    protected Button() {
    }

    public static ButtonBuilder builder() {
        return new ButtonBuilder();
    }

    public static class ButtonBuilder {

        private int slot;
        private ItemStack item;
        private Consumer<InventoryClickEvent> onClick;
        private Consumer<InventoryClickEvent> onOtherButtonClick;

        public ButtonBuilder slot(int slot) {
            this.slot = slot;
            return this;
        }

        public ButtonBuilder item(ItemStack item) {
            this.item = item;
            return this;
        }

        public ButtonBuilder onClick(Consumer<InventoryClickEvent> onClick) {
            this.onClick = onClick;
            return this;
        }

        public ButtonBuilder onOtherButtonClick(Consumer<InventoryClickEvent> onOtherButtonClick) {
            this.onOtherButtonClick = onOtherButtonClick;
            return this;
        }

        public Button build() {
            try {
                Button button = new Button();
                for (Field field : getClass().getDeclaredFields()) {
                    Field f = button.getClass().getDeclaredField(field.getName());
                    f.setAccessible(true);
                    f.set(button, Possible.of(field.get(this)));
                }
                return button;
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
            return null;
        }
    }
}
