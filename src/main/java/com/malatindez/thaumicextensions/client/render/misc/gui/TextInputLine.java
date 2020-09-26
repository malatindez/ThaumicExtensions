package com.malatindez.thaumicextensions.client.render.misc.gui;

import org.json.simple.JSONObject;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public class TextInputLine extends TextLine implements EnhancedGuiScreen.Clickable,
        EnhancedGuiScreen.Inputable {

    public Vector4f cursorColor;
    public TextInputLine(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
        if(parameters.containsKey("cursorColor")) {
            cursorColor = Json4Vec(parameters.get("cursorColor"));
        } else {
            cursorColor = new Vector4f(1,1,1,1);
        }
    }

    boolean selected = false;
    int cursor = 0;
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
            cutLine = false;
            textLineWasUpdated();
            if(lineToRender.length() > 0) {
                float x = (currentMousePosition.x - getCurrentPosition().x) / fontRendererObj.getStringWidth(lineToRender);
                x = Math.min(x, 1);
                cursor = renderCursor + (int) Math.round(x * lineToRender.length());
            }
        } else if (!cutLine) {
            cutLine = true;
            textLineWasUpdated();
        }
        return selected;
    }

    protected void moveCursor(int value) {
        if(value > 0) {
            cursor = Math.min(textLine.length(), cursor + value);
        } else {
            cursor = Math.max(0, cursor + value);
        }
        if(cursor < renderCursor) {
            renderCursor = Math.max(renderCursor - 4, 0);
        } else if (cursor > renderCursor + lineToRender.length() &&
                fontRendererObj.getStringWidth(textLine.substring(renderCursor)) > getSize().x) {
            renderCursor = Math.min(textLine.length() - 4, cursor);
        }
        textLineWasUpdated();
    }

    @Override
    public boolean keyTyped(char par1, int par2) {
        if(hided() || !selected) {
            return false;
        }
        switch(par2)  {
            case Keyboard.KEY_BACK:
                if (textLine.length() > 0 && cursor > 0) {
                    textLine = textLine.substring(0, cursor - 1) + textLine.substring(cursor);
                    moveCursor(-1);
                }
                break;
            case Keyboard.KEY_DELETE:
                if (cursor < textLine.length()) {
                    textLine = textLine.substring(0, cursor) + textLine.substring(cursor + 1);
                    textLineWasUpdated();
                }
                break;
            case Keyboard.KEY_RETURN:
                selected = false;
                cutLine = true;
                textLineWasUpdated();
                break;
            case Keyboard.KEY_RIGHT:
                moveCursor(+1);
                break;
            case Keyboard.KEY_LEFT:
                moveCursor(-1);
                break;
        }
        if (par1 > 31) {
            textLine = textLine.substring(0, cursor) + par1 + textLine.substring(cursor);
            moveCursor(+1);
        }
        return true;
    }

    @Override
    public void render() {
        if(hided()) {
            return;
        }
        super.render();
        Vector2f position = getCurrentPosition();
        Vector2f scale = getScale();
        if(selected) {
            GL11.glPushMatrix();
            float xCoord = this.fontRendererObj.getStringWidth(lineToRender.substring(lineToRender.length() - cursor + renderCursor)) * scale.x;
            /*
            // this.fontRendererObj.getStringWidth("|") = 2
            GL11.glTranslatef(position.x * scale.x + xCoord - 2.25f * scale.x, position.y, 0);
            GL11.glScalef(0.5f, scale.y, 1);
            this.fontRendererObj.drawString("│", 0, 0, Vector4fToColor(cursorColor), dropShadow);
            */
            Rect.renderSolidRect(new Vector4f(
                    position.x + xCoord,
                    position.y,
                    position.x + xCoord + 1,
                    position.y + fontRendererObj.FONT_HEIGHT * scale.y
            ), cursorColor, getZLevel());
            GL11.glPopMatrix();
        }
    }
}
