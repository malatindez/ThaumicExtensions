package com.malatindez.thaumicextensions.client.lib;

import com.sun.istack.internal.NotNull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.BufferUtils;
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

    public static void drawScaledCustomSizeModalRect(
            float x, float y, float texFromX, float textFromY,
            float texToX, float texToY, float scaledToX, float scaledToY,
            float textureSizeX, float textureSizeY)  {
        float f4 = 1.0F / textureSizeX;
        float f5 = 1.0F / textureSizeY;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + scaledToY,
                0.0D, texFromX * f4, (textFromY + texToY) * f5);
        tessellator.addVertexWithUV(x + scaledToX, y + scaledToY,
                0.0D, (texFromX + texToX) * f4,(textFromY + texToY) * f5);
        tessellator.addVertexWithUV(x + scaledToX, y,
                0.0D, ((texFromX + texToX) * f4), textFromY * f5);
        tessellator.addVertexWithUV(x, y, 0.0D,
                texFromX * f4, textFromY * f5);
        tessellator.draw();
    }
    public static void drawScaledCustomSizeModalRect(
            int x, int y, float texFromX, float texFromY,
            int texToX, int texToY, int scaledToX, int scaledToY,
            float textureSizeX, float textureSizeY)
    {
        drawScaledCustomSizeModalRect(
                (float)x, (float)y, texFromX, texFromY,
                (float)texToX,  (float)texToY, (float)scaledToX, (float)scaledToY,
                textureSizeX, textureSizeY
        );
    }
    public static void drawScaledCustomSizeModalRect(
            Vector2f coordinates, Vector2f texFrom,
            Vector2f texTo, Vector2f scaledTo,
            Vector2f textureSize) {
        drawScaledCustomSizeModalRect(
                coordinates.x, coordinates.y,
                texFrom.x, texFrom.y,
                texTo.x, texTo.y,
                scaledTo.x, scaledTo.y,
                textureSize.x, textureSize.y
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
