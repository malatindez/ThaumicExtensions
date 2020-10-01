package com.malatindez.thaumicextensions.client.render.misc.gui;

import net.minecraft.client.Minecraft;
import org.json.simple.JSONObject;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public class TextInputLine extends TextLine implements EnhancedGuiScreen.Clickable,
        EnhancedGuiScreen.Inputable {

    public final Vector4f cursorColor;
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

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject generateJSONObject() {
        JSONObject returnValue = super.generateJSONObject();
        JSONObject a = (JSONObject) returnValue.get(getName());
        a.put("cursorColor", VecToJson(cursorColor));
        return returnValue;
    }

    @Override
    public boolean mouseHandler(Vector2f currentMousePosition) {
        return false;
    }

    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {
        if(hided() || button != 0) {
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
                cursor = renderCursor + Math.round(x * lineToRender.length());
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
        if(cursor <= renderCursor) {
            renderCursor = Math.max(renderCursor - 4, 0);
        } else if (cursor > renderCursor + lineToRender.length() &&
                fontRendererObj.getStringWidth(textLine.substring(renderCursor)) > getSize().x
        ) {
            renderCursor = Math.max(0, Math.min(textLine.length(), renderCursor + 4));
        }
        textLineWasUpdated();

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean keyTyped(char par1, int par2) {
        if(hided() || !selected) {
            return false;
        }
        int offset;
        switch(par2)  {
            case Keyboard.KEY_BACK:
                offset = cursor-1;
                if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                    for (int i = cursor-1; (i >= renderCursor) && !(textLine.charAt(i) == ' ' ||
                            textLine.charAt(i) == '\n' || textLine.charAt(i) == '\r'); offset=i--);
                }
                if (textLine.length() > 0 && cursor > 0) {
                    textLine = textLine.substring(0, offset) + textLine.substring(cursor);
                    moveCursor(offset - cursor);
                }
                break;
            case Keyboard.KEY_DELETE:
                offset = cursor+1;
                if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                    for (int i = cursor; (i < renderCursor + lineToRender.length()) && !(textLine.charAt(i) == ' ' ||
                            textLine.charAt(i) == '\n' || textLine.charAt(i) == '\r'); offset=++i);
                }
                if (cursor < textLine.length()) {
                    textLine = textLine.substring(0, cursor) + textLine.substring(offset);
                    textLineWasUpdated();
                }
                break;
            case Keyboard.KEY_ESCAPE:
            case Keyboard.KEY_RETURN:
                selected = false;
                cutLine = true;
                textLineWasUpdated();
                break;
            case Keyboard.KEY_RIGHT:
                offset = cursor + 1;
                if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                    for (int i = cursor; (i < renderCursor + lineToRender.length()) && !(textLine.charAt(i) == ' ' ||
                            textLine.charAt(i) == '\n' || textLine.charAt(i) == '\r'); i++, offset=i);
                }
                moveCursor(offset-cursor);
                break;
            case Keyboard.KEY_LEFT:
                offset = cursor-1;
                if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                    for (int i = cursor-1; (i >= renderCursor) && !(textLine.charAt(i) == ' ' ||
                            textLine.charAt(i) == '\n' || textLine.charAt(i) == '\r'); offset=i--);
                }
                moveCursor(offset - cursor);
                break;
            default:
                if (par1 > 31) {
                    textLine = textLine.substring(0, cursor) + par1 + textLine.substring(cursor);
                    moveCursor(+1);
                }
                break;
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
        if(selected && (Minecraft.getSystemTime() % 1000 < 500)) {
            GL11.glPushMatrix();
            int a = Math.min(Math.max(0, cursor - renderCursor), lineToRender.length());
            float xCoord = this.fontRendererObj.getStringWidth(lineToRender.substring(0, a)) * scale.x - 0.25f;
            Rect.renderSolidRect(new Vector4f(
                    position.x + xCoord,
                    position.y,
                    position.x + xCoord + 0.75f,
                    position.y + fontRendererObj.FONT_HEIGHT * scale.y
            ), cursorColor, getZLevel());
            GL11.glPopMatrix();
        }
    }
}