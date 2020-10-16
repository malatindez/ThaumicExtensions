package com.malatindez.thaumicextensions.client.render.misc.gui;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import org.json.simple.JSONObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public class Rect extends DefaultGuiObject {

    public VertexColors colors;

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject generateJSONObject() {
        JSONObject returnValue = super.generateJSONObject();
        JSONObject a = (JSONObject) returnValue.get(getName());
        JSONObject Colors = new JSONObject();
        Colors.put("topLeft", VecToJson(colors.topLeft));
        Colors.put("topRight", VecToJson(colors.topRight));
        Colors.put("bottomLeft", VecToJson(colors.bottomLeft));
        Colors.put("bottomRight", VecToJson(colors.bottomRight));
        a.put("colors", Colors);
        return returnValue;
    }

    @Override
    public void loadFromJSONObject(JSONObject parameters) {
        super.loadFromJSONObject(parameters);
        JSONObject colors = (JSONObject) parameters.get("colors");
        this.colors = new VertexColors(
                Json4Vec(colors.get("topLeft")),
                Json4Vec(colors.get("topRight")),
                Json4Vec(colors.get("bottomLeft")),
                Json4Vec(colors.get("bottomRight"))
        );
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
    public Rect(String name, Object parent, JSONObject parameters) {
        super(name,parent,parameters);
    }
    public static void renderGradientRect(Vector4f borders, VertexColors colors, int zLevel) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(colors.topRight.x, colors.topRight.y, colors.topRight.z, colors.topRight.w);
        tessellator.addVertex(borders.z, borders.y, zLevel);
        tessellator.setColorRGBA_F(colors.topLeft.x, colors.topLeft.y, colors.topLeft.z, colors.topLeft.w);
        tessellator.addVertex(borders.x, borders.y, zLevel);
        tessellator.setColorRGBA_F(colors.bottomLeft.x, colors.bottomLeft.y, colors.bottomLeft.z, colors.bottomLeft.w);
        tessellator.addVertex(borders.x, borders.w, zLevel);
        tessellator.setColorRGBA_F(colors.bottomRight.x, colors.bottomRight.y, colors.bottomRight.z, colors.bottomRight.w);
        tessellator.addVertex(borders.z, borders.w, zLevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
    public static void renderSolidRect(Vector4f borders, Vector4f color, int zLevel) {
        renderGradientRect(borders, new VertexColors(color, color, color, color), zLevel);
    }
    @Override
    public void render() {
        if(hidden()) {
            return;
        }
        Vector2f coordinates = this.getCurrentPosition();
        Vector2f size = this.getSize();
        renderGradientRect(new Vector4f(coordinates.x, coordinates.y,
                        coordinates.x + size.x, coordinates.y + size.y),
                colors, zLevel);
    }

}
