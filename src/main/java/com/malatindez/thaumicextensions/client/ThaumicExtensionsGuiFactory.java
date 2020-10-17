package com.malatindez.thaumicextensions.client;


import cpw.mods.fml.client.IModGuiFactory;
import java.util.Set;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import thaumcraft.client.gui.ThaumcraftGuiConfig;

@SideOnly(Side.CLIENT)
public class ThaumicExtensionsGuiFactory implements IModGuiFactory {
    public void initialize(Minecraft minecraftInstance) {}

    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return ThaumcraftGuiConfig.class;
    }

    public Set<IModGuiFactory.RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    public IModGuiFactory.RuntimeOptionGuiHandler getHandlerFor(IModGuiFactory.RuntimeOptionCategoryElement element) {
        return null;
    }
}
