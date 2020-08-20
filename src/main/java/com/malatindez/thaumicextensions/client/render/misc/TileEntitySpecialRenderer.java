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
    public static class Model {
        public IModelCustom model;
        public String modID;
        public String pathToTexture;
        public Animation.Transformation transform;
        public Model(IModelCustom model, String modID, String pathToTexture, Animation.Transformation transform) {
            this.modID = modID;
            this.pathToTexture = pathToTexture;
            this.model = model;
            this.transform = transform;
        }
    }
    // AlphaModel defines, that this part should be rendered including alpha channel
    public static class AlphaModel extends Model {
        public AlphaModel(IModelCustom model, String modID, String pathToTexture,Animation.Transformation transform) {
            super(model, modID, pathToTexture, transform);
        }
    }


    private void RenderPart(Model model,
                            Animation animation,
                            Animation.Transformation transform,
                            double noise,
                            Animation.Transformation cameraOffset) {
        Animation.Transformation t = new Animation.Transformation(transform);
        t.x += model.transform.x; t.degreeX += model.transform.degreeX; t.scaleX *= model.transform.scaleX;
        t.y += model.transform.y; t.degreeY += model.transform.degreeY; t.scaleY *= model.transform.scaleY;
        t.z += model.transform.z; t.degreeZ += model.transform.degreeZ; t.scaleZ *= model.transform.scaleZ;
        t.x += cameraOffset.x;
        t.y += cameraOffset.y;
        t.z += cameraOffset.z;
        animation.PushMatrix(t, noise);
        UtilsFX.bindTexture(model.modID, model.pathToTexture);
        model.model.renderAll();
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

    protected void renderModels(double noise,
                                Model[] models,
                                Animation[] animations,
                                Animation.Transformation[] transformations,
                                Animation.Transformation cameraOffset) {

        ArrayList<Integer> parts = new ArrayList<Integer>();
        ArrayList<Integer> alphaParts = new ArrayList<Integer>();
        for (int i = 0; i < models.length; i++) {
            if (models[i] instanceof AlphaModel) {
                alphaParts.add(i);
            } else {
                parts.add(i);
            }
        }
        ArrayList<TileEntitySpecialRenderer.DoubleIntPair> distances =
                new ArrayList<TileEntitySpecialRenderer.DoubleIntPair>();
        if (alphaParts.size() != 0) {
            for (Integer i : alphaParts) {
                Animation.Transformation t = new Animation.Transformation(transformations[i]);
                t.x += models[i].transform.x;
                t.y += models[i].transform.y;
                t.z += models[i].transform.z;
                t.x += cameraOffset.x;
                t.y += cameraOffset.y;
                t.z += cameraOffset.z;
                t = animations[i].getModifiedCoordinates(t,noise);
                double distance = Math.sqrt(t.x * t.x + t.y * t.y + t.z * t.z);
                distances.add(new TileEntitySpecialRenderer.DoubleIntPair(distance, i));
            }
            Collections.sort(distances);
        }
        for(int i : parts) {
            RenderPart(models[i],animations[i],transformations[i],noise, cameraOffset);
        }
        if (alphaParts.size() != 0) {
            if (distances.size() != 0) {
                for (TileEntitySpecialRenderer.DoubleIntPair pair : distances) {
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GL11.glAlphaFunc(GL11.GL_LESS, 1.0f);
                    RenderPart(models[pair.y],animations[pair.y],transformations[pair.y],noise, cameraOffset);
                    GL11.glDisable(GL11.GL_ALPHA_TEST);
                    GL11.glDisable(GL11.GL_BLEND);
                }
            }
        }
    }
    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float tick) {

    }
}
