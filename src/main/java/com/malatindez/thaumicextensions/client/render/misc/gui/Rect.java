package com.malatindez.thaumicextensions.client.render.misc.gui;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.json.simple.JSONObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

@SuppressWarnings("rawtypes")
public class Rect extends DefaultGuiObject {

    @Override
    public MethodObjectPair getMethodDown(String objectName, String name, Class[] parameterTypes) {
        if(this.getName().equals(objectName)) {
            getMethodFunc(objectName, name, parameterTypes);
        }
        return null;
    }

    @Override
    public Object getObjectDown(String objectName) {
        if(this.getName().equals(objectName)) {
            return this;
        }
        return null;
    }

    @Override
    protected void VectorsWereUpdated() {

    }

    @Override
    public void preInit(String name, Object parent, JSONObject parameters) {

    }

    @Override
    public void postInit() {

    }

    public static class VertexColors {
        final Vector4f topLeft;
        final Vector4f topRight;
        final Vector4f bottomLeft;
        final Vector4f bottomRight;
        public VertexColors(Vector4f topLeft, Vector4f topRight, Vector4f bottomLeft, Vector4f bottomRight) {
            this.topLeft        = new Vector4f(topLeft);
            this.topRight       = new Vector4f(topRight);
            this.bottomLeft     = new Vector4f(bottomLeft);
            this.bottomRight    = new Vector4f(bottomRight);
        }
    }
    public final VertexColors colors;
    /*public Rect(String name, Vector2f coordinates, Vector2f scale, Vector2f size, int zLevel, VertexColors colors,
                ResolutionRescaleType type) {
        super(name, coordinates, scale, size, zLevel, type);
        this.colors = colors;
    }*/
    public Rect(String name, Object parent, JSONObject parameters) {
        super(name,parent,parameters);
        JSONObject colors = (JSONObject) parameters.get("colors");
        this.colors = new VertexColors(
                Json4Vec(colors.get("topLeft")),
                Json4Vec(colors.get("topRight")),
                Json4Vec(colors.get("bottomLeft")),
                Json4Vec(colors.get("bottomRight"))
        );
    }
    @Override
    public void render() {
        if(hided()) {
            return;
        }
        Vector2f coordinates = this.getCurrentPosition();
        Vector2f size = this.getSize();

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(colors.topRight.x, colors.topRight.y, colors.topRight.z, colors.topRight.w);
        tessellator.addVertex(coordinates.x + size.x, coordinates.y, this.zLevel);

        tessellator.setColorRGBA_F(colors.topLeft.x, colors.topLeft.y, colors.topLeft.z, colors.topLeft.w);
        tessellator.addVertex(coordinates.x, coordinates.y, this.zLevel);

        tessellator.setColorRGBA_F(colors.bottomLeft.x, colors.bottomLeft.y, colors.bottomLeft.z, colors.bottomLeft.w);
        tessellator.addVertex(coordinates.x, coordinates.y + size.y, this.zLevel);

        tessellator.setColorRGBA_F(colors.bottomRight.x, colors.bottomRight.y, colors.bottomRight.z, colors.bottomRight.w);
        tessellator.addVertex(coordinates.x + size.x, coordinates.y + size.y, this.zLevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

}
