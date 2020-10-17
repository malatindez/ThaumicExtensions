package com.malatindez.thaumicextensions.client.render.gui.GuiEditorMisc;

import com.malatindez.thaumicextensions.client.render.misc.gui.TextInputBox;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.json.simple.JSONObject;

@SideOnly(Side.CLIENT)
public class GuiElementEditor extends TextInputBox {
    public GuiElementEditor(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }
}
