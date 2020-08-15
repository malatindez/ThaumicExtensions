package com.malatindez.thaumicextensions.client.render.misc;

import com.malatindez.thaumicextensions.client.lib.UtilsFX;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Arrays;

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
        public float x, y, z,
                degreeX, degreeY, degreeZ;
        public Part(String modID, String pathToTexture, String objRef) {
            this.modID = modID;
            this.pathToTexture = pathToTexture;
            this.objRef = objRef;
            x = y = z = degreeX = degreeY = degreeZ = 0;
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
    public void RenderAll(float x, float y, float z) {
        for(int i = 0; i < animations.length; i++) {
            animations[i].PushMatrix(
                    x + parts[i].x, y + parts[i].y, z + parts[i].z,
                    parts[i].degreeX, parts[i].degreeY, parts[i].degreeZ
            );
            UtilsFX.bindTexture(parts[i].modID, parts[i].pathToTexture);
            model.renderPart(parts[i].objRef);
            GL11.glPopMatrix();
        }
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
    public void RenderPart(float x, float y, float z, String name) {
        for (int i = 0; i < parts.length; i++) {
            if(parts[i].objRef.equals(name)) {
                animations[i].PushMatrix(
                        x + parts[i].x, y + parts[i].y, z + parts[i].z,
                        parts[i].degreeX, parts[i].degreeY, parts[i].degreeZ
                );
                UtilsFX.bindTexture(parts[i].modID, parts[i].pathToTexture);
                model.renderPart(name);
                GL11.glPopMatrix();
            }
        }
    }
    public void RenderOnly(float x, float y, float z, ArrayList<String> names) {
        for (int i = 0; i < parts.length; i++) {
            for (String name : names) {
                if(parts[i].objRef.equals(name)) {
                    animations[i].PushMatrix(
                            x + parts[i].x, y + parts[i].y, z + parts[i].z,
                            parts[i].degreeX, parts[i].degreeY, parts[i].degreeZ
                    );
                    UtilsFX.bindTexture(parts[i].modID, parts[i].pathToTexture);
                    model.renderPart(name);
                    GL11.glPopMatrix();
                    names.remove(name);
                }
            }
        }
    }
    public void RenderOnly(float x, float y, float z, String... names) {
        RenderOnly(x, y, z, new ArrayList<String>(Arrays.asList(names)));
    }

}
