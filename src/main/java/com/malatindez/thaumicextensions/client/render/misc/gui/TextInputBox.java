package com.malatindez.thaumicextensions.client.render.misc.gui;

import org.json.simple.JSONObject;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public class TextInputBox extends TextBox implements EnhancedGuiScreen.Clickable,
        EnhancedGuiScreen.Inputable {
    Vector4f cursorColor;
    public TextInputBox(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
        if(parameters.containsKey("cursorColor")) {
            cursorColor = Json4Vec(parameters.get("cursorColor"));
        } else {
            cursorColor = new Vector4f(1,1,1,1);
        }
    }
    boolean selected = false;
    Vector2f cursor = new Vector2f(0,0);

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
        if(selected) {
            //float x = (currentMousePosition.x - getCurrentPosition().x) / fontRendererObj.getStringWidth(lineToRender);
            //x = Math.min(x, 1);
            //cursor.set(renderCursor + Math.round(x * lineToRender.length()),
//
          // );
        }
        return selected;
    }

    @Override
    public boolean keyTyped(char par1, int par2) {
        if(hided() || !selected) {
            return false;
        }

        return true;
    }
}

