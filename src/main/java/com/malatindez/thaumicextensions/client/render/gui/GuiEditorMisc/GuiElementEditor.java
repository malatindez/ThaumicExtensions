package com.malatindez.thaumicextensions.client.render.gui.GuiEditorMisc;

import com.malatindez.thaumicextensions.client.render.misc.gui.DefaultGuiObject;
import com.malatindez.thaumicextensions.client.render.misc.gui.TextInputBox;
import org.json.simple.JSONObject;

public class GuiElementEditor extends TextInputBox {
    public GuiElementEditor(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }
}
