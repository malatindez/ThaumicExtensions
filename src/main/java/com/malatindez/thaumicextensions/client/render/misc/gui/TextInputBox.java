package com.malatindez.thaumicextensions.client.render.misc.gui;

import net.minecraft.client.Minecraft;
import org.json.simple.JSONObject;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import java.util.ArrayList;

public class TextInputBox extends TextBox implements EnhancedGuiScreen.Clickable,
        EnhancedGuiScreen.Inputable, ScrollBar.Scrollable {
    Vector4f cursorColor;

    public final Vector2f renderCursor = new Vector2f(0,0);
    public TextInputBox(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }
    boolean selected = false;
    final Vector2f cursor = new Vector2f(0,0);

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject generateJSONObject() {
        JSONObject returnValue = super.generateJSONObject();
        JSONObject a = (JSONObject) returnValue.get(getName());
        a.put("cursorColor", VecToJson(cursorColor));
        return returnValue;
    }

    @Override
    public void loadFromJSONObject(JSONObject parameters) {
        super.loadFromJSONObject(parameters);
        if(parameters.containsKey("cursorColor")) {
            cursorColor = Json4Vec(parameters.get("cursorColor"));
        } else {
            cursorColor = new Vector4f(1,1,1,1);
        }
    }

    @Override
    public void mouseHandler(Vector2f currentMousePosition) {

    }

    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {
        if(hidden() || button != 0) {
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
    final ArrayList<String> lines = new ArrayList<String>();
    private final Vector2f cursorBuf = new Vector2f(0,0);
    int longest_line = 0;
    @Override
    protected void textWasUpdated() {
        if (lines != null && (!cursorBuf.equals(renderCursor) || previousSize != getSize()  || !text.equals(previousText))) {
            String buf = previousText = text; longest_line = 0;
            cursorBuf.set(renderCursor);
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
                longest_line = Math.max(b.length(), longest_line);
                lines.add(b);
            }
            int height = 0;
            linesToRender.clear();
            for(int i = (int) renderCursor.y; i < lines.size(); i++) {
                String s = lines.get(i);
                linesToRender.add(
                        fontRendererObj.trimStringToWidth(
                                s.substring(Math.min(s.length(), (int) renderCursor.x)).
                                        replaceAll("[\n\r]", ""),
                                (int)size.x)
                );
                if(size.y  < (height + 2 * fontRendererObj.FONT_HEIGHT)) {
                    break;
                }
                height += fontRendererObj.FONT_HEIGHT;
            }
        }
    }
    protected void moveCursor(float x, float y) {
        Vector2f cursorBuf = new Vector2f(cursor);
        if (y > 0) {
            cursor.y = Math.min(cursor.y + y, lines.size() - 1);
        } else {
            cursor.y = Math.max(0, cursor.y + y);
        }
        String line = lines.get((int) (cursor.y)).replaceAll("[\n\r]", "");
        if (y != 0) {
            int a = fontRendererObj.getStringWidth(line.substring(0, Math.min(line.length(), (int) cursor.x + 1)));
            int b = fontRendererObj.getStringWidth(line.substring(0, Math.min(line.length(), (int) cursor.x)));
            int avg = (a + b) / 2;
            cursor.x = fontRendererObj.trimStringToWidth(line, avg).length();
        }
        if (x >= 0) {
            if (cursor.y == lines.size() - 1 || (cursor.x + x <= line.length())) {
                cursor.x = Math.min(cursor.x + x, line.length());
            } else {
                cursor.set(0, cursor.y + 1);
                line = lines.get((int) (cursor.y)).replaceAll("[\n\r]", "");
            }
        } else {
            if (cursor.y == 0 || (cursor.x + x >= 0)) {
                cursor.x = Math.max(0, cursor.x + x);
            } else {
                line = lines.get((int) (cursor.y - 1)).replaceAll("[\n\r]", "");
                cursor.set(line.length(), cursor.y - 1);
            }
        }
        if (cursor.y - renderCursor.y >= linesToRender.size()) {
            renderCursor.y = Math.max(0, Math.min(4 - linesToRender.size() + cursor.y, lines.size()));
        } else if (cursor.y < renderCursor.y) {
            renderCursor.y = Math.max(0, Math.min(renderCursor.y - 4, cursor.y));
        }
        if (cursor.x <= renderCursor.x) {
            renderCursor.x = Math.max(cursor.x - 4, 0);
        } else {

            String lineToRender;
            if((int)cursor.y - (int)renderCursor.y < linesToRender.size()) {
                lineToRender = linesToRender.get((int)cursor.y - (int)renderCursor.y);
            } else {
                lineToRender = lines.get((int)cursor.y);
            }
            if (cursor.x > renderCursor.x + lineToRender.length() &&
                    fontRendererObj.getStringWidth(line.substring((int) renderCursor.x)) > getSize().x
            ) {
                StringBuilder sb = new StringBuilder(line.substring((int) renderCursor.x,
                        Math.min(line.length(), (int) (cursor.x + 4))));
                int asd = (int) (cursor.x + 4) - fontRendererObj.trimStringToWidth(
                        sb.reverse().toString(),
                        (int) (getSize().x / textScale.x)).length();
                renderCursor.x = Math.max(0, Math.min(line.length(), asd));
            }
        }
        textWasUpdated();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean keyTyped(char par1, int par2) {
        if(hidden() || !selected) {
            return false;
        }
        Vector2f offset = new Vector2f(0,0);
        String line = lines.get((int)cursor.y);
        int k = 0;
        switch(par2) {
            case Keyboard.KEY_BACK:
                offset.set(cursor.x-1,0);
                if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                    for (int i = (int)cursor.x-1; (i >= renderCursor.x) && !(line.charAt(i) == ' ' ||
                            line.charAt(i) == '\n' || line.charAt(i) == '\r'); offset.set(i--,0));
                }
                for(int i = 0; i < cursor.y; k += lines.get(i++).length());
                if(k + (int) (offset.x) > 0 && k + (int) cursor.x < text.length()) {
                    text = text.substring(0, k + (int) (offset.x)) + text.substring(k + (int) cursor.x);
                    moveCursor(offset.x-cursor.x, 0);
                }
                break;
            case Keyboard.KEY_DELETE:
                offset.set(cursor.x+1,0);
                if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                    for (int i = (int) cursor.x; i < line.length() && !(line.charAt(i) == ' ' ||
                            line.charAt(i) == '\n' || line.charAt(i) == '\r'); offset.set(++i,0));
                }
                for(int i = 0; i < cursor.y; k += lines.get(i++).length());
                if(k + (int) (cursor.x) > 0 && k + (int) offset.x < text.length()) {
                    text = this.text.substring(0, k + (int) (cursor.x)) + this.text.substring(k + (int) offset.x);
                    moveCursor(1, 0);
                    moveCursor(-1, 0);
                }
                break;

            case Keyboard.KEY_ESCAPE:
                selected = false;
                break;
            case Keyboard.KEY_RIGHT:
                offset.set(cursor.x+1,0);
                if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                    for (int i = (int) cursor.x; i < line.length() && !(line.charAt(i) == ' ' ||
                            line.charAt(i) == '\n' || line.charAt(i) == '\r'); offset.set(++i,0));
                }
                moveCursor(offset.x-cursor.x, 0);
                break;
            case Keyboard.KEY_LEFT:
                offset.set(cursor.x-1,0);
                if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL)) {
                    for (int i = (int)cursor.x-1; (i >= renderCursor.x) && !(line.charAt(i) == ' ' ||
                            line.charAt(i) == '\n' || line.charAt(i) == '\r'); offset.set(i--,0));
                }
                moveCursor(offset.x-cursor.x, 0);
                break;
            case Keyboard.KEY_UP:
                moveCursor(0, -1);
                break;
            case Keyboard.KEY_DOWN:
                moveCursor(0, 1);
                break;
            case Keyboard.KEY_PRIOR: // pgup
                moveCursor(0,-linesToRender.size());
                break;
            case Keyboard.KEY_NEXT: // pgdn
                moveCursor(0,linesToRender.size());
                break;
            case Keyboard.KEY_RETURN:
                k = 0;
                for(int i = 0; i < cursor.y; k += lines.get(i++).length());
                text = this.text.substring(0, k + (int) (cursor.x)) + "\n" + this.text.substring( k + (int) cursor.x);
                textWasUpdated();
                moveCursor(+1,0);
                break;
            default:
                if (par1 > 31) {
                    k = 0;
                    for(int i = 0; i < cursor.y; k += lines.get(i++).length());
                    text = this.text.substring(0, k + (int) (cursor.x)) + par1 + this.text.substring( k + (int) cursor.x);
                    textWasUpdated();
                    moveCursor(+1,0);
                }
                break;
        }
        return true;
    }
    @Override
    public void resolutionUpdated(Vector2f newResolution) {
        super.resolutionUpdated(newResolution);
        moveCursor(0,0);
    }
    @Override
    public void render() {
        super.render();
        Vector2f position = getCurrentPosition();
        Vector2f scale = getScale();
        if(selected && (Minecraft.getSystemTime() % 1000 < 500) && ((int) (cursor.y - renderCursor.y) >= 0 && (int) (cursor.y - renderCursor.y) < linesToRender.size())) {
            String str;
            str = this.linesToRender.get((int)(cursor.y - renderCursor.y));
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
    float offsetX = 0;
    float offsetY = 0;
    @Override
    public void setOffsetX(float offset) {
        renderCursor.x = (int)(offset * longest_line);
        offsetX = (offset * longest_line) - renderCursor.x;
        textWasUpdated();
    }

    @Override
    public void setOffsetY(float offset) {
        renderCursor.y = (int)(offset * lines.size());
        offsetY = (offset * lines.size()) - renderCursor.y;
        textWasUpdated();
    }

    @Override
    public float getOffsetX() {
        return Math.max(0, Math.min(1, (renderCursor.x + offsetX) / longest_line));
    }

    @Override
    public float getOffsetY() {
        return Math.max(0, Math.min(1, (renderCursor.y + offsetY) / lines.size()));
    }

}

