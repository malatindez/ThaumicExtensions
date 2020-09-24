package com.malatindez.thaumicextensions.common;

import com.malatindez.thaumicextensions.common.items.ItemEnhancedThaumonomicon;
import com.malatindez.thaumicextensions.common.items.ItemGuiEditor;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class ConfigItems {
    public static Item itemThaumonomicon;
    public static Item itemGuiEditor;
    public static void init() {
        initializeItems();
    }
    private static void initializeItems() {
        itemThaumonomicon = (new ItemEnhancedThaumonomicon()).setUnlocalizedName("ItemThaumonomicon");
        GameRegistry.registerItem(itemThaumonomicon, "ItemThaumonomicon", "Thaumcraft");
        itemGuiEditor = (new ItemGuiEditor()).setUnlocalizedName("ItemGuiEditor");
        GameRegistry.registerItem(itemGuiEditor, "ItemGuiEditor", "Thaumcraft");
    }
}
