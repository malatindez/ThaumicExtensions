package com.malatindez.thaumicextensions.client.render.misc.GUI;

import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;

public class TextBox extends DefaultGuiObject {
    protected final FontRenderer fontRendererObj;
    public final String text;
    public final Vector2f textScale;
    public int color;
    public final boolean isTextScalable;
    public final boolean scalable;
    public final boolean dropShadow;
    public TextBox(FontRenderer fontRendererObj, String text, int color, boolean dropShadow, boolean isTextScalable,
                   boolean scalable, Vector2f coordinates, Vector2f textScale, Vector2f scale, Vector2f size,
                   int zLevel) {
        super(coordinates, scale, size, zLevel);
        this.text = text;
        this.color = color;
        this.dropShadow = dropShadow;
        this.isTextScalable = isTextScalable;
        this.scalable = scalable;
        this.fontRendererObj = fontRendererObj;
        this.textScale = new Vector2f(textScale);
    }
    private int renderTextBox(String text, boolean render) {
        Vector2f scale = this.getScale();
        Vector2f size = this.getSize();
        ArrayList<String> words = new ArrayList<String>();
        StringBuilder current_word = new StringBuilder();
        for(int i = 0; i < text.length(); i++) {
            current_word.append(text.charAt(i));
            if (text.charAt(i) == ' ') {
                words.add(current_word.toString());
                current_word = new StringBuilder();
            }
        }
        int current_height = 0;
        StringBuilder current_line = new StringBuilder();
        int returnValue = 0;
        for (String word : words) {
            float x = textScale.x * fontRendererObj.getStringWidth(current_line + word);

            if (isTextScalable) {
                x = x * scale.x;
            }
            if (x > this.getSize().x) {
                returnValue += current_line.length();
                if(render) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(this.getCurrentPosition().x, this.getCurrentPosition().y + current_height, 0);
                    if (isTextScalable) {
                        GL11.glScalef(scale.x * textScale.x, scale.y * textScale.y, 1);
                    } else {
                        GL11.glScalef(textScale.x, textScale.y, 1);
                    }
                    this.fontRendererObj.drawString(current_line.toString(), 0, 0, color, dropShadow);
                    GL11.glPopMatrix();
                }

                current_line = new StringBuilder(word);
                float y = textScale.y * fontRendererObj.FONT_HEIGHT;
                if (isTextScalable) {
                    y = scale.y * y;
                }
                if (current_height + y > this.getSize().y) {
                    break;
                }
                current_height += y;
            }
            current_line.append(word);
        }
        return returnValue;
    }
    public int getFittingCharAmount(String text) {
        return renderTextBox(text, false);
    }
    @Override
    public void render() {
        renderTextBox(this.text, true);
    }


    @Override
    public boolean scalable() {
        return scalable;
    }

    @Override
    protected void VectorsWereUpdated() {

    }

    @Override
    public int getZLevel() {
        return 0;
    }
}
