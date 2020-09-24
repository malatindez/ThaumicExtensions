package com.malatindez.thaumicextensions.client.render.misc.GUI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.json.simple.JSONObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class TextLine extends DefaultGuiObject {
    protected FontRenderer fontRendererObj;
    public String textLine;
    public Vector4f color;
    public boolean dropShadow;

    @Override
    public void preInit(String name, Object parent, JSONObject parameters) {
        fontRendererObj = Minecraft.getMinecraft().fontRenderer;
        textLine = (String)parameters.get("text");
        color = Json4Vec(parameters.get("color"));
        if(parameters.containsKey("dropShadow")) {
            dropShadow = (Boolean)parameters.get("dropShadow");
        } else {
            dropShadow = false;
        }
    }

    public TextLine(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }

    @Override
    public MethodObjectPair getMethodDown(String objectName, String name, Class[] parameterTypes) {
        if(objectName == this.getName()) {
            getMethodFunc(objectName, name, parameterTypes);
        }
        return null;
    }

    @Override
    public Object getObjectDown(String objectName) {
        if(objectName == this.getName()) {
            return this;
        }
        return null;
    }

    @Override
    protected void VectorsWereUpdated() {

    }

    @Override
    public void reScale(Vector2f scale) {
        super.reScale(scale);
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
        this.fontRendererObj.drawString(textLine, 0, 0,
                Vector4fToColor(color),
                dropShadow);
        GL11.glPopMatrix();
    }

}
