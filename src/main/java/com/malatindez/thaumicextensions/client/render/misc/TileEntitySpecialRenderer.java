package com.malatindez.thaumicextensions.client.render.misc;

import com.malatindez.thaumicextensions.client.lib.UtilsFX;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collections;

public abstract class TileEntitySpecialRenderer extends net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer {
    // Here's some rules of texture formatting:
    // texture XXX.png will be bound to YYY in .obj if it exists.
    // objects in model w/o bound texture will not be rendered
    // If there's no YYY in .obj file - there will be unpredictable behaviour in Rendering
    // Example of usage:
    // Part("thaumicextensions", "models/pillars/pillar1_1.png", "pillar_base")
    public static class Part {
        public String modID;
        public String pathToTexture;
        public String objRef;
        public Part(String modID, String pathToTexture, String objRef) {
            this.modID = modID;
            this.pathToTexture = pathToTexture;
            this.objRef = objRef;
        }
    }
    // AlphaPart defines, that this part should be rendered with alpha channel

    public static class AlphaPart extends Part {
        public AlphaPart(String modID, String pathToTexture, String objRef) {
            super(modID, pathToTexture, objRef);
        }
    }
/*  Those variables you can store as you want in your class but there's some recommendations:
IModelCustom better to store as static final, because we don't need to load it more than once
    protected IModelCustom model;
Tile entity can be updated so do animations, so its your choice
    protected Animation[] animations;
This part should be static final too, because we still don't need to load it more than once.
    protected Part[] parts;
*/
    protected abstract IModelCustom getModel();
    protected abstract Animation getAnimation(int i);
    protected abstract Part getPart(int i);
    protected abstract Part[] getParts();


    private void RenderPart(Animation.Transformation transform, double noise, int i) {
        this.getAnimation(i).PushMatrix(transform, noise);
        UtilsFX.bindTexture(this.getPart(i).modID, this.getPart(i).pathToTexture);
        this.getModel().renderPart(this.getPart(i).objRef);
        GL11.glPopMatrix();
    }
    private class DoubleIntPair implements Comparable<DoubleIntPair> {
        public Double x;
        public Integer y;
        public DoubleIntPair(Double x, Integer y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(DoubleIntPair o) {
            return o.x.compareTo(x);
        }
    }
    protected void renderParts(TileEntity tile, Animation.Transformation transform,
                             double noise, ArrayList<Integer> parts, ArrayList<Integer> alphaParts) {

        ArrayList<TileEntitySpecialRenderer.DoubleIntPair> distances =
                new ArrayList<TileEntitySpecialRenderer.DoubleIntPair>();
        if (alphaParts.size() != 0) {
            ChunkCoordinates coordinates = Minecraft.getMinecraft().thePlayer.getPlayerCoordinates();
            coordinates.posX = tile.xCoord - coordinates.posX;
            coordinates.posY = tile.yCoord - coordinates.posY;
            coordinates.posZ = tile.zCoord - coordinates.posZ;
            for (Integer i : alphaParts) {
                Animation.Transformation partCoordinates = this.getAnimation(i).getModifiedCoordinates(transform, noise);
                partCoordinates.x += coordinates.posX;
                partCoordinates.y += -1.6f + coordinates.posY;
                partCoordinates.z += coordinates.posZ;
                distances.add(
                        new TileEntitySpecialRenderer.DoubleIntPair(
                                Math.sqrt(
                                        (partCoordinates.x) * (partCoordinates.x) +
                                        (partCoordinates.y) * (partCoordinates.y) +
                                        (partCoordinates.z) * (partCoordinates.z)
                                ),
                                i
                        )

                );
            }
            Collections.sort(distances);
        }
        for(int i : parts) {
            RenderPart(transform,noise, i);
        }
        if (alphaParts.size() != 0) {
            if (distances.size() != 0) {
                for (TileEntitySpecialRenderer.DoubleIntPair pair : distances) {
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GL11.glAlphaFunc(GL11.GL_LESS, 1.0f);
                    RenderPart(transform, noise, pair.y);
                    GL11.glDisable(GL11.GL_ALPHA_TEST);
                    GL11.glDisable(GL11.GL_BLEND);
                }
            }
        }
    }
    public void renderAll(TileEntity tile, Animation.Transformation transform, double noise) {
        ArrayList<Integer> parts = new ArrayList<Integer>();
        ArrayList<Integer> alphaParts = new ArrayList<Integer>();
        for (int i = 0; i < this.getParts().length; i++) {
            if (this.getPart(i) instanceof AlphaPart) {
                alphaParts.add(i);
            } else {
                parts.add(i);
            }
        }
        renderParts(tile, transform, noise,parts,alphaParts);
    }
    public void renderOnly(TileEntity tile, Animation.Transformation transform, double noise, ArrayList<Integer> input) {
        ArrayList<Integer> parts = new ArrayList<Integer>();
        ArrayList<Integer> alphaParts = new ArrayList<Integer>();
        for (Integer i : input) {
            if (this.getPart(i) instanceof AlphaPart) {
                alphaParts.add(i);
            } else {
                parts.add(i);
            }
        }
        renderParts(tile,transform,noise,parts,alphaParts);
    }
    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float tick) {

    }
}
