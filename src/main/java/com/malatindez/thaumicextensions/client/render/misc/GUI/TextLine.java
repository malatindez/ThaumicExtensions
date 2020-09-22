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
    public void setScale(Vector2f scale) {
        this.scale.set(scale);
        this.size.set(
                new Vector2f(fontRendererObj.getStringWidth(textLine) * scale.x,
                        fontRendererObj.FONT_HEIGHT * scale.y
                ));
    }

    @Override
    public void render() {
        GL11.glPushMatrix();
        GL11.glTranslatef(this.coordinates.x, this.coordinates.y, 0);
        GL11.glScalef(this.scale.x, this.scale.y, 1);
        this.fontRendererObj.drawString(textLine, 0, 0, color, dropShadow);
        GL11.glPopMatrix();
    }

    @Override
    public void render(Vector2f coordinates) {
        GL11.glPushMatrix();
        GL11.glTranslatef(this.coordinates.x + coordinates.x, this.coordinates.y + coordinates.y, 0);
        GL11.glScalef(this.scale.x, this.scale.y, 1);
        this.fontRendererObj.drawString(textLine, 0, 0, color, dropShadow);
        GL11.glPopMatrix();
    }

    @Override
    public boolean scalable() {
        return scalable;
    }

    @Override
    public void render(Vector2f coordinates, Vector2f scale) {
        GL11.glPushMatrix();
        GL11.glTranslatef(coordinates.x, coordinates.y, 0);
        GL11.glScalef(scale.x, scale.y, 1);
        this.fontRendererObj.drawString(textLine, 0, 0, color, dropShadow);
        GL11.glPopMatrix();
    }
}
