package com.malatindez.thaumicextensions;

import net.minecraft.item.Item;

public class ModItem extends Item {
    public ModItem(String itemName) {
        this.setUnlocalizedName(itemName);
        this.setTextureName(ThaumicExtensions.MODID + ":" + itemName);
        this.setCreativeTab(ThaumicExtensions.ThaumicExtensionsTab);
    }
}
