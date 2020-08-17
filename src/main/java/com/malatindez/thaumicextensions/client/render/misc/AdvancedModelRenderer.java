package com.malatindez.thaumicextensions.client.render.misc;

import com.malatindez.thaumicextensions.client.lib.UtilsFX;
import com.malatindez.thaumicextensions.client.render.misc.Animation.Animation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@SideOnly(Side.CLIENT)
public class AdvancedModelRenderer {
    // Here's some rules of formatting texture:
    // texture XXX.png will be bound to YYY in .obj if it exists.
    // objects in model w/o bound texture will not be rendered
    // If there's no YYY in .obj file - there will be unpredictable behaviour in Rendering
    // Example of usage:
    // BoundTexture("thaumicextensions", "models/pillars/pillar1_1.png", "pillar_base")
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
    public static class AlphaPart extends Part {
        public AlphaPart(String modID, String pathToTexture, String objRef) {
            super(modID, pathToTexture, objRef);
        }
    }
    protected IModelCustom   model;
    protected Animation[]    animations;
    protected Part[] parts;
    public AdvancedModelRenderer(ResourceLocation model, Part[] parts, Animation[] animations) throws IllegalArgumentException {
        if(animations.length != parts.length) {
            throw new IllegalArgumentException("Lengths aren't equal!");
        }
        this.model = AdvancedModelLoader.loadModel(model);
        this.parts = parts;
        this.animations = animations;
    }
    public Part[] getParts() {
        return this.parts;
    }
    public Part getPartByName(String name) {
        for(Part part : parts) {
            if (part.objRef.equals(name)) {
                return part;
            }
        }
        return null;
    }
    public Animation[] getAnimations() {
        return this.animations;
    }
    public Animation getAnimationByName(String name) {
        for(int i = 0; i < parts.length; i++) {
            if (parts[i].objRef.equals(name)) {
                return animations[i];
            }
        }
        return null;
    }
    private void RenderPart(float x, float y, float z, float degreeX, float degreeY, float degreeZ, double noise, int i) {
        animations[i].PushMatrix(x, y, z, degreeX, degreeY, degreeZ, noise);
        UtilsFX.bindTexture(parts[i].modID, parts[i].pathToTexture);
        model.renderPart(parts[i].objRef);
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
    private void RenderParts(float x, float y, float z,
                             float degreeX, float degreeY, float degreeZ,
                             double noise, ArrayList<Integer> parts, ArrayList<Integer> alphaParts) {
        ChunkCoordinates coordinates = Minecraft.getMinecraft().thePlayer.getPlayerCoordinates();
        coordinates.posY += 1.6;
        ArrayList<DoubleIntPair> distances = new ArrayList<DoubleIntPair>();
        for(Integer i : alphaParts) {
            Animation.Coordinates partCoordinates = animations[i].getModifiedCoordinates(x,y,z,noise);
            distances.add(
                    new DoubleIntPair(
                            Math.sqrt(
                                    (partCoordinates.x) * (partCoordinates.x) +
                                            (partCoordinates.y) * (partCoordinates.y) +
                                            (partCoordinates.z) * (partCoordinates.z)
                            ),
                            i
                    ));
        }
        Collections.sort(distances);
        for(int i : parts) {
            RenderPart(x,y,z,degreeX,degreeY,degreeZ, noise, i);
        }
        if (distances.size() != 0) {
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glAlphaFunc(GL11.GL_LESS, 1.0f);
            for(DoubleIntPair pair : distances) {
                RenderPart(x,y,z,degreeX,degreeY,degreeZ, noise, pair.y);
            }
            GL11.glDisable(GL11.GL_ALPHA_TEST);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }
    public void RenderAll(float x, float y, float z,
                          float degreeX, float degreeY, float degreeZ,
                          double noise) {
        ArrayList<Integer> parts = new ArrayList<Integer>();
        ArrayList<Integer> alphaParts = new ArrayList<Integer>();
        for (int i = 0; i < this.parts.length; i++) {
            if (this.parts[i] instanceof AlphaPart) {
                alphaParts.add(i);
            } else {
                parts.add(i);
            }
        }
        RenderParts(x,y,z,degreeX,degreeY,degreeZ,noise,parts,alphaParts);
    }
    private void RenderOnly(float x, float y, float z,
                            float degreeX, float degreeY, float degreeZ,
                            double noise, ArrayList<String> names) {
        ArrayList<Integer> parts = new ArrayList<Integer>();
        ArrayList<Integer> alphaParts = new ArrayList<Integer>();
        for (int i = 0; i < this.parts.length; i++) {
            for (String name : names) {
                if(this.parts[i].objRef.equals(name)) {
                    if (this.parts[i] instanceof AlphaPart) {
                        alphaParts.add(i);
                    } else {
                        parts.add(i);
                    }
                    names.remove(name);
                }
            }
        }
        RenderParts(x,y,z,degreeX,degreeY,degreeZ,noise,parts,alphaParts);
    }
    public void RenderOnly(float x, float y, float z, float degreeX, float degreeY, float degreeZ, double noise, String... names) {
        RenderOnly(x, y, z, degreeX, degreeY, degreeZ, noise, new ArrayList<String>(Arrays.asList(names)));
    }

}
