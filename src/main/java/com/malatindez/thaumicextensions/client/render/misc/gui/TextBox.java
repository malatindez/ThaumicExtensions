package com.malatindez.thaumicextensions.client.render.misc.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.json.simple.JSONObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import java.util.ArrayList;

@SuppressWarnings("Convert2Diamond")
public class TextBox extends DefaultGuiObject {
    protected final FontRenderer fontRendererObj;
    public final String text;
    public final Vector2f textScale;
    public final Vector4f color;
    public final boolean isTextScalable;
    public final boolean dropShadow;
    /*
    public TextBox(String name, FontRenderer fontRendererObj, String text, Vector3f color, boolean dropShadow,
                   boolean isTextScalable, Vector2f coordinates, Vector2f textScale, Vector2f scale, Vector2f size,
                   int zLevel, ResolutionRescaleType type) {
        super(name, coordinates, scale, size, zLevel, type);
        this.text = text;
        this.color = color;
        this.dropShadow = dropShadow;
        this.isTextScalable = isTextScalable;
        this.fontRendererObj = fontRendererObj;
        this.textScale = new Vector2f(textScale);
    }*/

    @Override
    public void preInit(String name, Object parent, JSONObject parameters) {

    }

    @Override
    public void postInit() {

    }

    public TextBox(String name, Object parent, JSONObject parameters) {
        super(name,parent,parameters);
        fontRendererObj = Minecraft.getMinecraft().fontRenderer;
        text = (String)parameters.get("text");
        color = Json4Vec(parameters.get("color"));
        if(parameters.containsKey("textScale")) {
            textScale = Json2Vec(parameters.get("textScale"));
        } else {
            textScale = new Vector2f(1,1);
        }
        if(parameters.containsKey("isTextScalable")) {
            isTextScalable = (Boolean)parameters.get("isTextScalable");
        } else {
            isTextScalable = false;
        }
        if(parameters.containsKey("dropShadow")) {
            dropShadow = (Boolean)parameters.get("dropShadow");
        } else {
            dropShadow = false;
        }
    }
    private int renderTextBox(String text, boolean render) {
        Vector2f scale = this.getScale();
        ArrayList<String> words = new ArrayList<String>();
        StringBuilder current_word = new StringBuilder();
        for(int i = 0; i < text.length(); i++) {
            current_word.append(text.charAt(i));
            if (text.charAt(i) == ' ' || text.charAt(i) == '\n') {
                words.add(current_word.toString());
                current_word = new StringBuilder();
            }
        }
        int current_height = 0;
        StringBuilder current_line = new StringBuilder();
        int returnValue = 0;
        boolean flag = false;
        for (String word : words) {
            float x = textScale.x * fontRendererObj.getStringWidth(current_line + word);

            if (isTextScalable) {
                x = x * scale.x;
            }
            if (flag ||  x > this.getSize().x) {
                flag = false;
                returnValue += current_line.length();
                if(render) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(this.getCurrentPosition().x, this.getCurrentPosition().y + current_height, 0);
                    if (isTextScalable) {
                        GL11.glScalef(scale.x * textScale.x, scale.y * textScale.y, 1);
                    } else {
                        GL11.glScalef(textScale.x, textScale.y, 1);
                    }
                    this.fontRendererObj.drawString(current_line.toString(), 0, 0,
                            Vector4fToColor(color),
                            dropShadow);
                    GL11.glPopMatrix();
                }

                current_line = new StringBuilder();
                float y = textScale.y * fontRendererObj.FONT_HEIGHT;
                if (isTextScalable) {
                    y = scale.y * y;
                }
                if (current_height + y > this.getSize().y) {
                    break;
                }
                current_height += y;
            }
            if(word.endsWith("\n") || word.endsWith("\r")) {
                flag = true;
            }
            current_line.append(word.replaceAll("[\n\r]", ""));
        }
        return returnValue;
    }
    public int getFittingCharAmount(String text) {
        return renderTextBox(text, false);
    }
    @Override
    public void render() {
        if(hided()) {
            return;
        }
        renderTextBox(this.text, true);
    }


    @SuppressWarnings("rawtypes")
    @Override
    public MethodObjectPair getMethodDown(String objectName, String name, Class[] parameterTypes) {
        if(objectName.equals(this.getName())) {
            getMethodFunc(objectName, name, parameterTypes);
        }
        return null;
    }

    @Override
    public Object getObjectDown(String objectName) {
        if(objectName.equals(this.getName())) {
            return this;
        }
        return null;
    }

    @Override
    protected void VectorsWereUpdated() {

    }

    @Override
    public int getZLevel() {
        return 0;
    }
}
