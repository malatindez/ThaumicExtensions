package com.malatindez.thaumicextensions.client.render.misc.gui;

import net.minecraft.client.Minecraft;
import org.json.simple.JSONObject;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import java.util.ArrayList;

public class TextInputBox extends TextBox implements EnhancedGuiScreen.Clickable,
        EnhancedGuiScreen.Inputable {
    Vector4f cursorColor;
    public Vector2f renderCursor = new Vector2f(0,0);
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
        if(hided() || button != 0) {
            return false;
        }
        Vector4f borders =  getBorders();
        selected = currentMousePosition.x > borders.x &&
                currentMousePosition.y > borders.y &&
                currentMousePosition.x < borders.z &&
                currentMousePosition.y < borders.w;
        if(selected) {
            float y = (currentMousePosition.y - getCurrentPosition().y) / fontRendererObj.FONT_HEIGHT / textScale.y;
            y = Math.min(y, linesToRender.size()-1);
            String str = linesToRender.get((int)y);
            float x = (currentMousePosition.x - getCurrentPosition().x) /
                    fontRendererObj.getStringWidth(str) / textScale.x;
            x = Math.min(x, 1);
            cursor.set(renderCursor.x + Math.round(x * str.length()),
                    (int)(renderCursor.y + y)
           );
        }
        return selected;
    }
    ArrayList<String> lines = new ArrayList<String>();
    @Override
    protected void textWasUpdated() {
        if (lines != null && (previousSize != getSize()  || !text.equals(previousText))) {
            String buf = previousText = text;
            lines.clear();
            previousSize = getSize();
            Vector2f size = getSize();
            size.set(size.x / textScale.x, size.y / textScale.y);
            while (buf.length() > 0) {
                int a = buf.indexOf("\n") + 1;
                if (a == 0) {
                    a = buf.length();
                }
                String b = buf.substring(0, a);
                buf = buf.substring(a);
                lines.add(b.replaceAll("[\n\r]", ""));
            }
            int height = 0;
            linesToRender.clear();
            for(int i = (int) renderCursor.y; i < lines.size(); i++) {
                linesToRender.add(
                        fontRendererObj.trimStringToWidth(lines.get(i).substring((int) renderCursor.x), (int)size.x)
                );
                if(size.y  < (height + 2 * fontRendererObj.FONT_HEIGHT)) {
                    break;
                }
                height += fontRendererObj.FONT_HEIGHT;
            }
        }
    }

    protected void moveCursor(Vector2f value) {
        moveCursor(value.x, value.y);
    }
    protected void moveCursor(float x, float y) {
        Vector2f cursorBuf = new Vector2f(cursor);
        if(y > 0) {
            cursor.y = Math.min(cursor.y + y, lines.size()-1);
        } else {
            cursor.y = Math.max(0, cursor.y + y);
        }
        if(y != 0) {
            String s = lines.get((int)(cursorBuf.y));
            int a = fontRendererObj.getStringWidth(s.substring(0, Math.min(s.length(), (int)cursor.x + 1) ));
            int b = fontRendererObj.getStringWidth(s.substring(0, Math.min(s.length(), (int)cursor.x) ));
            int avg = (a+b)/2;
            cursor.x = fontRendererObj.trimStringToWidth(lines.get((int)(cursor.y)), avg).length();

        }
        if(x >= 0) {
            if(cursor.y == linesToRender.size() || (cursor.x + x >= 0)) {
                cursor.x = Math.min(cursor.x + x, lines.get((int)cursor.y).length());
            } else {
                cursor.set(0, cursor.y + 1);
            }
        } else {
            if(cursor.y == 0 || (cursor.x + x >= 0)) {
                cursor.x = Math.max(0, cursor.x + x);
            } else {
                cursor.set(lines.get((int)(cursor.y - 1)).length() - 1, cursor.y - 1);
                String s = lines.get((int)(cursor.y));
                int w = fontRendererObj.getStringWidth(s.substring(0, Math.min(s.length(), (int)cursor.x+1) ));
                cursor.x = fontRendererObj.trimStringToWidth(lines.get((int)(cursor.y)), w).length();
            }
        }
        if(cursor.y - renderCursor.y > linesToRender.size()) {
            renderCursor.y = Math.max(0, Math.min(renderCursor.y + 4, lines.size() - 1));
        } else if (cursor.y < renderCursor.y) {
            renderCursor.y = Math.max(0, renderCursor.y - 4);
        }
        String line = lines.get((int)cursor.y);
        String lineToRender = linesToRender.get((int)cursor.y);
        if(cursor.x <= renderCursor.x) {
            renderCursor.x = Math.max(renderCursor.x - 4, 0);
        } else if (cursor.x > renderCursor.x + lineToRender.length() &&
                fontRendererObj.getStringWidth(line.substring((int) renderCursor.x)) > getSize().x
        ) {
            renderCursor.x = Math.max(0, Math.min(line.length(), renderCursor.x + 4));
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean keyTyped(char par1, int par2) {
        if(hided() || !selected) {
            return false;
        }
        Vector2f offset = new Vector2f(0,0);
        switch(par2) {
            case Keyboard.KEY_BACK:
                //offset.set(0,-1);
                break;

            case Keyboard.KEY_ESCAPE:
            case Keyboard.KEY_RETURN:
                selected = false;
                break;
            case Keyboard.KEY_RIGHT:
                offset.set(cursor.x+1,0);
                if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                    String line = lines.get((int)cursor.y);
                    int l = (int)(renderCursor.x + line.length());
                    for (int i = (int) cursor.x; i < l && !(line.charAt(i) == ' ' ||
                            line.charAt(i) == '\n' || line.charAt(i) == '\r'); offset.set(++i,0));
                }
                moveCursor(new Vector2f(offset.x-cursor.x, 0));
                break;
            case Keyboard.KEY_LEFT:
                offset.set(cursor.x-1,0);
                if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                    String line = lines.get((int)cursor.y);
                    for (int i = (int)cursor.x-1; (i >= renderCursor.x) && !(line.charAt(i) == ' ' ||
                            line.charAt(i) == '\n' || line.charAt(i) == '\r'); offset.set(i--,0));
                }
                moveCursor(new Vector2f(offset.x-cursor.x, 0));
                break;
            case Keyboard.KEY_UP:
                moveCursor(new Vector2f(0, -1));
                break;
            case Keyboard.KEY_DOWN:
                moveCursor(new Vector2f(0, 1));
                break;
        }
        return true;
    }

    @Override
    public void render() {
        super.render();
        Vector2f position = getCurrentPosition();
        Vector2f scale = getScale();
        if(selected && (Minecraft.getSystemTime() % 1000 < 500)) {
            String str = this.linesToRender.get((int)(cursor.y - renderCursor.y));
            GL11.glPushMatrix();
            int a = (int) Math.min(Math.max(0, cursor.x - renderCursor.x), str.length());
            float xCoord = this.fontRendererObj.getStringWidth(str.substring(0, a)) * textScale.x - 0.25f;
            xCoord = position.x + xCoord;
            float yCoord = position.y + (cursor.y-renderCursor.y) * fontRendererObj.FONT_HEIGHT * textScale.y;
            Rect.renderSolidRect(new Vector4f(
                    xCoord,
                    yCoord,
                    xCoord + 0.75f,
                    yCoord + textScale.y * fontRendererObj.FONT_HEIGHT
            ), cursorColor, getZLevel());
            GL11.glPopMatrix();
        }
    }
}

