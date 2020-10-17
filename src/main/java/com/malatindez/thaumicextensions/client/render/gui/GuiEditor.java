package com.malatindez.thaumicextensions.client.render.gui;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.malatindez.thaumicextensions.client.render.gui.GuiEditorMisc.GuiBrowser;
import com.malatindez.thaumicextensions.client.render.misc.gui.*;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;
import thaumcraft.client.gui.GuiResearchBrowser;

import java.util.HashMap;


public class GuiEditor extends EnhancedGuiScreen {
    public interface Editable {
        /**
         * Should return "name": {"type": "Type", coordinates: [1,1], etc. } JSONObject
         * @return full JSON mapping with every element
         */
        JSONObject getFullJSON();
        /**
         * should return {"type": "Type", coordinates: [1,1], etc. } JSONObject
         * @return default JSON with already preset elements
         */
        JSONObject getTemplateJSON();
    }
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
    @SuppressWarnings("rawtypes")
    public static final HashMap<String, Class> editor_parts = new HashMap<String, Class>(){{
        put("Collection", Collection.class);
        put("ContextMenuField", ContextMenuField.class);
        put("ScrollBar", ScrollBar.class);
        put("Button", Button.class);
        put("Drag", Drag.class);
        put("Icon", IconFactory.Icon.class);
        put("Rect", Rect.class);
        put("TextBox", TextBox.class);
        put("TextLine", TextLine.class);
        put("TextInputBox", TextInputBox.class);
        put("TextInputLine", TextInputLine.class);
        // type name and class instance which will be constructed
    }};
    @SuppressWarnings("rawtypes")
    public static void addEditorPart(Class c) {
        parts.put(c.getSimpleName(), c);
    }

    public void onGuiClosed() {
        super.onGuiClosed();
    }
    static Collection collection = null;
    public GuiEditor() {

        //if(collection == null) {
            EnhancedGuiScreen.addPart(GuiBrowser.class);
            collection = EnhancedGuiScreen.loadFromFile(this,
                    new ResourceLocation(ThaumicExtensions.MODID, "gui/gui_editor.json"));
        //}
        this.gui = collection;
    }
    public void openContextMenu(DefaultGuiObject object, Vector2f mousePosition) {
        ((ContextMenuField)object).icon.show();
        ((ContextMenuField)object).icon.setCoordinates(mousePosition);
    }
    public void closeContextMenu(DefaultGuiObject object) {
        ((ContextMenuField)object).icon.hide();
    }
}
