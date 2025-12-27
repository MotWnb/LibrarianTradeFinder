package de.greenman999.librariantradefinder.screens;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class EnchantmentEntry extends AbstractSelectionList.Entry<EnchantmentEntry> {

    final EditBox maxPriceField;
    final EditBox levelField;
    private final EnchantmentState state;
    private int x;
    private int y;
    private int entryWidth;
    private int entryHeight;

    public EnchantmentEntry(EnchantmentState state) {
        this.state = state;

        this.maxPriceField = new EditBox(
                Minecraft.getInstance().font,
                0, 0, 20, 14,
                Component.translatable("tradefinderui.enchantments.price.name")
        );
        this.maxPriceField.setMaxLength(2);
        this.maxPriceField.setValue(String.valueOf(state.maxPrice));

        this.levelField = new EditBox(
                Minecraft.getInstance().font,
                0, 0, 14, 14,
                Component.translatable("tradefinderui.enchantments.level.name")
        );
        this.levelField.setMaxLength(1);
        this.levelField.setValue(String.valueOf(state.level));
    }

    @Override
    public void renderContent(@NonNull GuiGraphics context, int mouseX, int mouseY, boolean hovered, float deltaTicks) {
        this.x = super.getX();
        this.y = super.getY();
        this.entryWidth = super.getWidth();
        this.entryHeight = super.getHeight();

        Font textRenderer = Minecraft.getInstance().font;
        Component enchantmentText = state.enchantment.description();

        // clamp values
        if (!maxPriceField.getValue().isEmpty() && !maxPriceField.canConsumeInput()) {
            int v = Integer.parseInt(maxPriceField.getValue());
            v = Mth.clamp(v, 5, 64);
            maxPriceField.setValue(String.valueOf(v));
        }

        if (!levelField.getValue().isEmpty() && !levelField.canConsumeInput()) {
            int v = Integer.parseInt(levelField.getValue());
            v = Mth.clamp(v, 1, state.enchantment.getMaxLevel());
            levelField.setValue(String.valueOf(v));
        }

        // sync UI → state
        state.maxPrice = Integer.parseInt(maxPriceField.getValue());
        state.level = Integer.parseInt(levelField.getValue());

        int maxPriceX = x + entryWidth - 21;
        int levelX = maxPriceX - 15 - 14;

        // background
        if (state.enabled) {
            context.fill(x, y, x + entryWidth, y + entryHeight - 4, 0x3F00FF00);
            context.drawString(textRenderer, Component.nullToEmpty("$:"), maxPriceX - 10, y + 4, 0xFFFFFFFF);
            context.drawString(textRenderer, Component.nullToEmpty("LVL:"), levelX - 23, y + 4, 0xFFFFFFFF);
        } else {
            context.fill(x, y, x + entryWidth, y + entryHeight - 4, 0x1AC7C0C0);
        }

        context.drawString(textRenderer, enchantmentText, 8, y + 4, 0xFFFFFFFF);

        // render fields
        maxPriceField.setVisible(state.enabled);
        levelField.setVisible(state.enabled);

        maxPriceField.setX(maxPriceX);
        maxPriceField.setY(y + 1);
        maxPriceField.render(context, mouseX, mouseY, deltaTicks);

        levelField.setX(levelX);
        levelField.setY(y + 1);
        levelField.render(context, mouseX, mouseY, deltaTicks);

        // tooltips
        if (maxPriceField.canConsumeInput()) {
            context.setComponentTooltipForNextFrame(
                    Minecraft.getInstance().font,
                    List.of(
                            Component.translatable("tradefinderui.enchantments.price.tooltip.1").withStyle(ChatFormatting.GRAY),
                            Component.translatable("tradefinderui.enchantments.price.tooltip.2").withStyle(ChatFormatting.GRAY),
                            Component.translatable("tradefinderui.enchantments.price.tooltip.3").withStyle(ChatFormatting.GRAY),
                            Component.translatable("tradefinderui.enchantments.price.tooltip.4").withStyle(ChatFormatting.GRAY),
                            Component.translatable("tradefinderui.enchantments.price.tooltip.5").withStyle(ChatFormatting.GRAY)
                    ),
                    maxPriceField.getX() - 8, y + 32
            );
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent click, boolean doubled) {
        double mouseX = click.x();
        double mouseY = click.y();

        int i = state.enabled ? (21 + 15 + 14) : 0;

        // toggle enable
        if (mouseX > this.x && mouseX < this.x + this.entryWidth - i && mouseY > y && mouseY < y + entryHeight - 4) {
            state.enabled = !state.enabled;
            return true;
        }

        // disable button
        if (mouseX > this.x + entryWidth - 21 - 10 - 4 && mouseX < this.x + this.entryWidth - 21 &&
                mouseY > y && mouseY < y + entryHeight - 4 && state.enabled) {
            state.enabled = false;
            return true;
        }

        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        maxPriceField.mouseMoved(mouseX, mouseY);
        levelField.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent click) {
        return maxPriceField.mouseReleased(click) || levelField.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (maxPriceField.isMouseOver(mouseX, mouseY)) {
            state.maxPrice = Mth.clamp(state.maxPrice + (int) verticalAmount, 5, 64);
            maxPriceField.setValue(String.valueOf(state.maxPrice));
            return true;
        }
        if (levelField.isMouseOver(mouseX, mouseY)) {
            state.level = Mth.clamp(state.level + (int) verticalAmount, 1, state.enchantment.getMaxLevel());
            levelField.setValue(String.valueOf(state.level));
            return true;
        }
        return false;
    }
}
