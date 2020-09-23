package com.malatindez.thaumicextensions.client.render.misc.GUI;

import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

public class TextLine extends DefaultGuiObject {
    protected final FontRenderer fontRendererObj;
    public final String textLine;
    public int color;
    public boolean scalable;
    public boolean dropShadow;
    public TextLine(
            FontRenderer fontRendererObj, String textLine, int color, boolean dropShadow, boolean scalable,
            Vector2f coordinates, Vector2f scale, int zLevel) {
        super(coordinates, scale,
                new Vector2f(fontRendererObj.getStringWidth(textLine) * scale.x,
                        fontRendererObj.FONT_HEIGHT * scale.y
                        ), zLevel
                );
        this.fontRendererObj = fontRendererObj;
        this.color = color;
        this.dropShadow = dropShadow;
        this.scalable = scalable;
        this.textLine = textLine;
    }

    @Override
    protected void VectorsWereUpdated() {

    }

    @Override
    public void reScale(Vector2f scale) {
        this.reScale(scale);
        this.setSize(
                new Vector2f(fontRendererObj.getStringWidth(textLine) * scale.x,
                        fontRendererObj.FONT_HEIGHT * scale.y
                ));
    }

    @Override
    public void render() {
        GL11.glPushMatrix();
        GL11.glTranslatef(getCurrentPosition().x, getCurrentPosition().y, 0);
        GL11.glScalef(getScale().x, getScale().y, 1);
        this.fontRendererObj.drawString(textLine, 0, 0, color, dropShadow);
        GL11.glPopMatrix();
    }

    @Override
    public boolean scalable() {
        return scalable;
    }
}
