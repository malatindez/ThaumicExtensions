package com.malatindez.thaumicextensions.client.render.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.client.gui.GuiResearchRecipe;

@SideOnly(Side.CLIENT)
public class GuiEnhancedResearchRecipe extends GuiResearchRecipe {

    protected void keyTyped(char par1, int par2) {
        if (par2 == this.mc.gameSettings.keyBindInventory.getKeyCode() || par2 == 1) {
            history.clear();
            this.mc.displayGuiScreen(new GuiEnhancedResearchBrowser(this.guiMapX, this.guiMapY));
        } else {
            super.keyTyped(par1, par2);
        }
    }

    public void onGuiClosed() {
        super.onGuiClosed();
    }

    public GuiEnhancedResearchRecipe(ResearchItem research, int page, double x, double y) {
        super(research,page,x,y);
    }
}
