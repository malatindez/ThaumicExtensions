package com.malatindez.thaumicextensions.client.render.misc.gui;

import org.json.simple.JSONObject;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public class TextInputBox extends TextLine implements EnhancedGuiScreen.Clickable,
        EnhancedGuiScreen.Inputable {
    public TextInputBox(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }
    boolean selected = false;
    @Override
    public boolean mouseHandler(Vector2f currentMousePosition) {
        return false;
    }

    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {
        if(hided()) {
            return false;
        }
        Vector4f borders =  getBorders();
        selected = currentMousePosition.x > borders.x &&
                currentMousePosition.y > borders.y &&
                currentMousePosition.x < borders.z &&
                currentMousePosition.y < borders.w;
        return selected;
    }

    @Override
    public boolean keyTyped(char par1, int par2) {
        if(hided() || !selected) {
            return false;
        }
        if (par2 == Keyboard.KEY_BACK) {
            if (this.textLine.length() > 0) {
                this.textLine = this.textLine.substring(0, this.textLine.length()-1);
            }
        }
        if (par1 > 31) {
            this.textLine += par1;
        }
        return true;
    }
}
