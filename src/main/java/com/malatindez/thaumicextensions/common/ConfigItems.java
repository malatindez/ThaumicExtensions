package com.malatindez.thaumicextensions.common;

import com.malatindez.thaumicextensions.common.items.ItemEnhancedThaumonomicon;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class ConfigItems {
    public static Item itemThaumonomicon;
    public static void init() {
        initializeItems();
    }
    private static void initializeItems() {
        itemThaumonomicon = (new ItemEnhancedThaumonomicon()).setUnlocalizedName("ItemThaumonomicon");
        GameRegistry.registerItem(itemThaumonomicon, "ItemThaumonomicon", "Thaumcraft");
    }
}
