package com.malatindez.thaumicextensions.client.lib;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
public class UtilsFX extends thaumcraft.client.lib.UtilsFX {

    static final Map<String, ResourceLocation> boundTextures = new HashMap<String, ResourceLocation>();
    public static void bindTexture(String mod, String texture) {
        ResourceLocation rl;
        if (boundTextures.containsKey(texture)) {
            rl = boundTextures.get(texture);
        } else {
            rl = new ResourceLocation(mod, texture);
        }
        (Minecraft.getMinecraft()).renderEngine.bindTexture(rl);

    }
    public static void bindTexture(String texture) {
        bindTexture("thaumicextensions", texture);
    }

    public static void drawCustomSizeModalRect(
            float x, float y, float texFromX, float texFromY,
            float texToX, float texToY, float scaleX, float scaleY,
            float textureSizeX, float textureSizeY, float zLevel)  {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef(x, (y + (texToY-texFromY)*scaleY), zLevel);
        GL11.glScalef(scaleX, scaleY, 1);
        float f = 1.0F / textureSizeX;
        float f1 = 1.0F / textureSizeY;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0,0,0,
                texFromX * f, (texFromY + texToY-texFromY) * f1);

        tessellator.addVertexWithUV(texToX-texFromX, 0, 0,
                (texFromX + texToX-texFromX) * f, (texFromY + texToY-texFromY) * f1);

        tessellator.addVertexWithUV(texToX-texFromX, texFromY-texToY, 0,
                (texFromX + texToX-texFromX) * f, texFromY * f1);

        tessellator.addVertexWithUV(0, texFromY-texToY, 0,
                texFromX * f, texFromY * f1);
        tessellator.draw();
        GL11.glPopMatrix();
    }
    public static void drawCustomSizeModalRect(
            Vector2f coordinates, Vector2f texFrom,
            Vector2f texTo, Vector2f scale,
            Vector2f textureSize, float zLevel) {
        drawCustomSizeModalRect(
                coordinates.x, coordinates.y,
                texFrom.x, texFrom.y,
                texTo.x, texTo.y, scale.x, scale.y,
                textureSize.x, textureSize.y, zLevel
        );
    }

    /**
     * @param matrix Matrix to convert
     * @return FloatBuffer
     */
    public static FloatBuffer matrixToBuffer(Matrix4f matrix) {
        FloatBuffer dest = BufferUtils.createFloatBuffer(16);
        matrix.store(dest);
        dest.position(0);
        return dest;
    }

    public static String loadFromFile(ResourceLocation file) {
        InputStream x;
        try {
            x = Minecraft.getMinecraft().getResourceManager().getResource(file).getInputStream();
        } catch (Exception e) {
            System.out.println("Exception caught! Wrong ResourceFile in loadFromFile.");
            System.out.println(file);
            e.printStackTrace();
            return "";
        }
        InputStreamReader isReader = new InputStreamReader(x);
        BufferedReader reader = new BufferedReader(isReader);
        StringBuilder sb = new StringBuilder();
        String str;
        try {
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
        } catch (Exception ignored) { }
        str = sb.toString();
        return str;
    }
}
