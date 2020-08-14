package com.malatindez.thaumicextensions.client.render.tile;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

public class EnhancedInfusionPillarRenderer {
    // those arrays should be same size
    // filenames[0] = "pillar1_1"
    // filenames[1] = "pillar1_2"
    // etc.
    public IModelCustom PILLAR_MODELS[];
    public String textures[];
    public AnimationProperties[] properties;
    public static class AnimationProperties {
        float floatingOffset = 0.0f;
        // [0.0f, +inf] offset from -floatingOffset to +floatingOffset
        float timeInterval = 0.0f;
        // timing interval, how many time it will take to go
        // from -floatingOffset to +floatingOffset and backwards

        float orientationOffsetX = 0.0f;
        float orientationOffsetZ = 0.0f;
        // offsets from 0.5f, to +X, +Z (
        // both should be positive
        public AnimationProperties(float floatingOffset,
                                   float timeInterval,
                                   float orientationOffsetX,
                                   float orientationOffsetZ){
            this.floatingOffset = floatingOffset;
            this.timeInterval = timeInterval;
            this.orientationOffsetX = orientationOffsetX;
            this.orientationOffsetZ = orientationOffsetZ;
        }

    };

    public EnhancedInfusionPillarRenderer(String[] filenames, AnimationProperties[] properties) {
        PILLAR_MODELS = new IModelCustom[filenames.length];
        for (int i = 0; i < filenames.length; i++) {
            PILLAR_MODELS[i] = AdvancedModelLoader.loadModel(
                    new ResourceLocation(ThaumicExtensions.MODID,
                        "models/pillars/" + filenames[i] + ".obj"));
            textures[i] = "models/pillars/" + filenames[i] + ".png";
        }
        this.properties = properties;
    }

    public void renderModels() {

    }
}
