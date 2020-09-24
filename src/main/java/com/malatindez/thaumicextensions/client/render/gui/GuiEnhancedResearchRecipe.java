package com.malatindez.thaumicextensions.client.render.gui;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.malatindez.thaumicextensions.client.render.misc.GUI.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.client.gui.GuiResearchBrowser;

import java.lang.reflect.Method;

@SideOnly(Side.CLIENT)
public class GuiEnhancedResearchRecipe extends EnhancedGuiScreen {
    public void initGui() {}

    protected void actionPerformed(GuiButton par1GuiButton) {
        super.actionPerformed(par1GuiButton);
    }

    protected void keyTyped(char par1, int par2) {
        if (par2 == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            GuiResearchBrowser.highlightedItem.clear();
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        } else {
            if (par2 == 1)
                GuiResearchBrowser.highlightedItem.clear();
            super.keyTyped(par1, par2);
        }
    }

    @Override
    public Method getMethod(String method, Class<?> parameterTypes) {
        return null;
    }

    public void onGuiClosed() {
        super.onGuiClosed();
    }

    public void drawScreen(int mx, int my, float tick) {
        super.drawScreen(mx,my,tick);
    }
    public GuiEnhancedResearchRecipe(ResearchItem research, int page, double x, double y) {
        super(new ResourceLocation(ThaumicExtensions.MODID, "gui/test_gui.json"));
    }
    public void defaultResearchClicked(Object obj, int id) {

    }
}
