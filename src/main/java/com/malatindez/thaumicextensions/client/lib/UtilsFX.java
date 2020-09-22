package com.malatindez.thaumicextensions.client.lib;

import com.sun.istack.internal.NotNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
public class UtilsFX extends thaumcraft.client.lib.UtilsFX {

    static Map<String, ResourceLocation> boundTextures = new HashMap<String, ResourceLocation>();
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
            float texToX, float texToY,
            float textureSizeX, float textureSizeY, float zLevel)  {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 1.0F / textureSizeX;
        float f1 = 1.0F / textureSizeY;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, (y + texToY-texFromY), zLevel,
                texFromX * f, (texFromY + texToY-texFromY) * f1);

        tessellator.addVertexWithUV(x + texToX-texFromX, y + texToY-texFromY, zLevel,
                (texFromX + texToX-texFromX) * f, (texFromY + texToY-texFromY) * f1);

        tessellator.addVertexWithUV(x + texToX-texFromX, y, zLevel,
                (texFromX + texToX-texFromX) * f, texFromY * f1);

        tessellator.addVertexWithUV(x, y, zLevel,
                texFromX * f, texFromY * f1);
        tessellator.draw();
        GL11.glPopMatrix();
    }
    public static void drawCustomSizeModalRect(
            int x, int y, float texFromX, float texFromY,
            int texToX, int texToY,
            float textureSizeX, float textureSizeY, float zLevel)
    {
        drawCustomSizeModalRect(
                (float)x, (float)y, texFromX, texFromY,
                (float)texToX,  (float)texToY,
                textureSizeX, textureSizeY, zLevel
        );
    }
    public static void drawCustomSizeModalRect(
            Vector2f coordinates, Vector2f texFrom,
            Vector2f texTo,
            Vector2f textureSize, float zLevel) {
        drawCustomSizeModalRect(
                coordinates.x, coordinates.y,
                texFrom.x, texFrom.y,
                texTo.x, texTo.y,
                textureSize.x, textureSize.y, zLevel
        );
    }

    /**
     * @param matrix Matrix to convert
     * @return FloatBuffer
     */
    @NotNull
    public static FloatBuffer matrixToBuffer(@NotNull Matrix4f matrix) {
        FloatBuffer dest = BufferUtils.createFloatBuffer(16);
        matrix.store(dest);
        dest.position(0);
        return dest;
    }
}
