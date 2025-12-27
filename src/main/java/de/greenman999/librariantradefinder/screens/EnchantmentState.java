package de.greenman999.librariantradefinder.screens;

import de.greenman999.librariantradefinder.config.TradeFinderConfig;
import net.minecraft.world.item.enchantment.Enchantment;

public class EnchantmentState {
    public final Enchantment enchantment;
    public int maxPrice;
    public int level;
    public boolean enabled;

    public EnchantmentState(Enchantment enchantment, TradeFinderConfig.EnchantmentOption option) {
        this.enchantment = enchantment;
        this.maxPrice = option.getMaxPrice();
        this.level = option.getLevel();
        this.enabled = option.isEnabled();
    }
}
