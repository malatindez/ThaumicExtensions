package com.malatindez.thaumicextensions.client.render.misc.gui;

import org.json.simple.JSONObject;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

public class TextInputLine extends TextLine implements EnhancedGuiScreen.Clickable,
        EnhancedGuiScreen.Inputable {

    public TextInputLine(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }

    @Override
    public boolean mouseHandler(Vector2f currentMousePosition) {
        return false;
    }

    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {
        return false;
    }

    @Override
    public void keyTyped(char par1, int par2) {
        if (par2 == Keyboard.KEY_BACK) {
            if (this.textLine.length() > 0) {
                this.textLine = this.textLine.substring(0, this.textLine.length()-1);
            }
        }
        if (par1 > 31) {
            this.textLine += par1;
        }
    }
}
