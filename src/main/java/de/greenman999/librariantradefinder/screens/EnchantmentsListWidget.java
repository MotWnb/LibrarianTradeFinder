package de.greenman999.librariantradefinder.screens;

import com.mojang.blaze3d.platform.InputConstants;
import de.greenman999.librariantradefinder.LibrarianTradeFinder;
import de.greenman999.librariantradefinder.config.TradeFinderConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import org.joml.Matrix3x2fStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class EnchantmentsListWidget extends AbstractSelectionList<EnchantmentEntry> {

    private final List<EnchantmentState> states = new ArrayList<>();
    public GrayButtonWidget resetButton;
    public int top;

    public EnchantmentsListWidget(Minecraft client, int width, int height, int top, int itemHeight) {
        super(client, width, height, top, itemHeight);
        this.top = top;

        for (var entry : LibrarianTradeFinder.getConfig().enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            TradeFinderConfig.EnchantmentOption option = entry.getValue();

            EnchantmentState state = new EnchantmentState(enchantment, option);
            states.add(state);

            this.addEntry(new EnchantmentEntry(state));
        }

        this.resetButton = GrayButtonWidget.builder(
                        Component.translatable("tradefinderui.reset"),
                        (buttonWidget) -> {
                            for (EnchantmentState state : states) {
                                state.maxPrice = 64;
                                state.level = state.enchantment.getMaxLevel();
                                state.enabled = false;
                            }

                            // 刷新 UI
                            this.clearEntries();
                            for (EnchantmentState state : states) {
                                this.addEntry(new EnchantmentEntry(state));
                            }
                        })
                .color(0x5FC7C0C0)
                .bounds(this.width - 45, 5, 50, 15)
                .tooltip(Tooltip.create(Component.translatable("tradefinderui.reset.tooltip")))
                .build();
    }

    @Override
    public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.setSelected(null);
        Matrix3x2fStack matrices = context.pose();
        matrices.pushMatrix();

        context.fill(5, 5, this.width + 5, 20, 0xAFC7C0C0);
        context.drawString(Minecraft.getInstance().font,
                Component.translatable("tradefinderui.enchantments.title"),
                9, 9, 0xFFFFFFFF);

        matrices.popMatrix();
        super.renderWidget(context, mouseX, mouseY, delta);
    }

    @Override
    protected void renderListBackground(@NonNull GuiGraphics context) {}

    @Override
    protected void renderListSeparators(@NonNull GuiGraphics context) {}

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput builder) {}

    @Override
    public int getRowWidth() {
        return this.width - 12;
    }

    @Override
    public int getRight() {
        return this.width + 7;
    }

    @Override
    protected int scrollBarX() {
        return this.width;
    }

    @Override
    public int getRowTop(int index) {
        return this.getY() - (int)this.scrollAmount() + index * this.defaultEntryHeight;
    }

    @Override
    public int getRowLeft() {
        return this.getX() + this.width / 2 - this.getRowWidth() / 2 - 1;
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent click, boolean doubled) {
        resetButton.mouseClicked(click, doubled);
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean keyPressed(KeyEvent input) {
        int keyCode = input.input();
        if (!(keyCode == InputConstants.KEY_BACKSPACE ||
                (keyCode >= InputConstants.KEY_0 && keyCode <= InputConstants.KEY_9) ||
                keyCode == InputConstants.KEY_LEFT ||
                keyCode == InputConstants.KEY_RIGHT))
            return false;

        for (EnchantmentEntry entry : children()) {
            if (entry.maxPriceField.isFocused()) return entry.maxPriceField.keyPressed(input);
            if (entry.levelField.isFocused()) return entry.levelField.keyPressed(input);
        }
        return super.keyPressed(input);
    }

    @Override
    public boolean keyReleased(KeyEvent input) {
        int keyCode = input.input();
        if (!(keyCode == InputConstants.KEY_BACKSPACE ||
                (keyCode >= InputConstants.KEY_0 && keyCode <= InputConstants.KEY_9) ||
                keyCode == InputConstants.KEY_LEFT ||
                keyCode == InputConstants.KEY_RIGHT))
            return false;

        for (EnchantmentEntry entry : this.children()) {
            entry.maxPriceField.keyReleased(input);
            entry.levelField.keyReleased(input);
        }
        return super.keyReleased(input);
    }

    @Override
    public boolean charTyped(CharacterEvent input) {
        if (!input.isAllowedChatCharacter()) return false;

        for (EnchantmentEntry entry : this.children()) {
            entry.maxPriceField.charTyped(input);
            entry.levelField.charTyped(input);
        }
        return super.charTyped(input);
    }

    public void saveToConfig() {
        for (EnchantmentState state : states) {
            TradeFinderConfig.EnchantmentOption option =
                    LibrarianTradeFinder.getConfig().enchantments.get(state.enchantment);

            option.setEnabled(state.enabled);
            option.setMaxPrice(state.maxPrice);
            option.setLevel(state.level);
        }
    }
}
