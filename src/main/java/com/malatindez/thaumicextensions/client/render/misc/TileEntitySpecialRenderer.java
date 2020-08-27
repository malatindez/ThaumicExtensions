package com.malatindez.thaumicextensions.client.render.misc;

import com.malatindez.thaumicextensions.client.lib.Transformation;
import com.malatindez.thaumicextensions.client.lib.UtilsFX;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import java.nio.FloatBuffer;
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
        public Transformation transform;
        public Model(IModelCustom model, String modID, String pathToTexture, Transformation transform) {
            this.modID = modID;
            this.pathToTexture = pathToTexture;
            this.model = model;
            this.transform = transform;
        }
    }
    // AlphaModel defines, that this part should be rendered including alpha channel
    public static class AlphaModel extends Model {
        public AlphaModel(IModelCustom model, String modID, String pathToTexture, Transformation transform) {
            super(model, modID, pathToTexture, transform);
        }
    }


    private void RenderPart(Model model,
                            Animation animation,
                            Transformation transform,
                            Transformation cameraOffset,
                            double time,  double noise) {
        Transformation t = new Transformation(transform);
        Vector3f.add(t.position, model.transform.position, t.position);
        Vector3f.add(t.position, cameraOffset.position, t.position);
        Vector3f.add(t.degree, model.transform.degree, t.degree);
        t.scale.x *= model.transform.scale.x;
        t.scale.y *= model.transform.scale.y;
        t.scale.z *= model.transform.scale.z;
        FloatBuffer buf = UtilsFX.matrixToBuffer(animation.getMatrix(t, time, noise));
        GL11.glPushMatrix();
        GL11.glMultMatrix(buf);
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
                                Transformation[] transformations,
                                Transformation cameraOffset) {
        double time = ((double)Minecraft.getSystemTime()) / 1000.0f;
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
                Transformation t = new Transformation(transformations[i]);
                Vector3f.add(t.position, models[i].transform.position, t.position);
                Vector3f.add(t.position, cameraOffset.position, t.position);
                Vector3f.add(t.degree, models[i].transform.degree, t.degree);
                t.scale.x *= models[i].transform.scale.x;
                t.scale.y *= models[i].transform.scale.y;
                t.scale.z *= models[i].transform.scale.z;
                Matrix4f m = animations[i].getMatrix(t, time, noise);
                double distance = (float)Math.sqrt(m.m30 * m.m30 + m.m31 * m.m31 + m.m32 * m.m32);
                distances.add(new TileEntitySpecialRenderer.DoubleIntPair(distance, i));
            }
            Collections.sort(distances);
        }
        for(int i : parts) {
            RenderPart(models[i],animations[i],transformations[i], cameraOffset, time, noise);
        }
        if (alphaParts.size() != 0) {
            if (distances.size() != 0) {
                for (TileEntitySpecialRenderer.DoubleIntPair pair : distances) {
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    GL11.glAlphaFunc(GL11.GL_LESS, 1.0f);
                    RenderPart(models[pair.y],animations[pair.y],transformations[pair.y], cameraOffset, time, noise);
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
