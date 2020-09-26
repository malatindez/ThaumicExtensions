package com.malatindez.thaumicextensions.client.render.gui;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.malatindez.thaumicextensions.client.render.misc.gui.Collection;
import com.malatindez.thaumicextensions.client.render.misc.gui.ContextMenuField;
import com.malatindez.thaumicextensions.client.render.misc.gui.EnhancedGuiScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;
import thaumcraft.client.gui.GuiResearchBrowser;

public class GuiEditor extends EnhancedGuiScreen {
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
    static Collection collection = null;
    public GuiEditor() {
        if(collection == null) {
            collection = EnhancedGuiScreen.loadFromFile(this,
                    new ResourceLocation(ThaumicExtensions.MODID, "gui/gui_editor.json"));
        }
        this.gui = collection;
    }
    public void openContextMenu(Object object, Vector2f mousePosition) {
        ((ContextMenuField)object).icon.show();
        ((ContextMenuField)object).icon.setCoordinates(mousePosition);
    }
    public void closeContextMenu(Object object) {
        ((ContextMenuField)object).icon.hide();
    }
}
