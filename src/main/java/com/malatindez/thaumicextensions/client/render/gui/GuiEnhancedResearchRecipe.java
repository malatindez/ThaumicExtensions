package com.malatindez.thaumicextensions.client.render.gui;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.malatindez.thaumicextensions.client.render.misc.gui.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.client.gui.GuiResearchBrowser;

@SuppressWarnings("EmptyMethod")
@SideOnly(Side.CLIENT)
public class GuiEnhancedResearchRecipe extends EnhancedGuiScreen {
    public void initGui() {}

    protected void actionPerformed(GuiButton par1GuiButton) {
        super.actionPerformed(par1GuiButton);
    }

    protected void keyTyped(char par1, int par2) {
        if(!keyTyped1(par1, par2)) {
            if (par2 == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
                this.mc.displayGuiScreen(null);
                this.mc.setIngameFocus();
            } else {
                if (par2 == 1)
                    GuiResearchBrowser.highlightedItem.clear();
                super.keyTyped(par1, par2);
            }
        }
    }

    public void onGuiClosed() {
        super.onGuiClosed();
    }

    public void drawScreen(int mx, int my, float tick) {
        super.drawScreen(mx,my,tick);
    }
    static Collection collection = null;
    public GuiEnhancedResearchRecipe(ResearchItem research, int page, double x, double y) {
        //if(collection == null) {
        collection = EnhancedGuiScreen.loadFromFile(this,
                new ResourceLocation(ThaumicExtensions.MODID, "gui/test_gui.json"));
  //  }
        this.gui = collection;
    }
    public void defaultResearchClicked(DefaultGuiObject obj, int id) {
        obj.setCoordinates(
                Vector2f.add(obj.getCoordinates(),
                        new Vector2f(1,1), null)
        );
        obj.checkBorders();
    }
}
