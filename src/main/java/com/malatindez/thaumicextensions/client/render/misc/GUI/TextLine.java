package com.malatindez.thaumicextensions.client.render.misc.GUI;

import net.minecraft.client.gui.FontRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class TextLine extends DefaultGuiObject {
    protected final FontRenderer fontRendererObj;
    public final String textLine;
    public Vector3f color;
    public boolean dropShadow;
    public TextLine(
            FontRenderer fontRendererObj, String textLine, Vector3f color, boolean dropShadow,
            Vector2f coordinates, Vector2f scale, int zLevel, ResolutionRescaleType type) {
        super(coordinates, scale,
                new Vector2f(fontRendererObj.getStringWidth(textLine) * scale.x,
                        fontRendererObj.FONT_HEIGHT * scale.y
                        ), zLevel, type
                );
        this.fontRendererObj = fontRendererObj;
        this.color = color;
        this.dropShadow = dropShadow;
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
        this.fontRendererObj.drawString(textLine, 0, 0, (int) (color.x * 255 * 255 + color.y * 255 + color.z), dropShadow);
        GL11.glPopMatrix();
    }

}
