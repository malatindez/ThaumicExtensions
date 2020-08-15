package com.malatindez.thaumicextensions.client.render.tile;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.malatindez.thaumicextensions.client.lib.UtilsFX;
import com.malatindez.thaumicextensions.client.render.misc.AdvancedModelRenderer;
import com.malatindez.thaumicextensions.client.render.misc.Animation;
import com.malatindez.thaumicextensions.common.tiles.TileEnhancedInfusionPillar;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class TileEnhancedInfusionPillarRenderer extends TileEntitySpecialRenderer {

    private static final AdvancedModelRenderer[] models = new AdvancedModelRenderer[] {
            new AdvancedModelRenderer(
                    new ResourceLocation(ThaumicExtensions.MODID, "models/pillars/Pillar1.obj"),
                    new AdvancedModelRenderer.BoundTexture[] {
                            new AdvancedModelRenderer.BoundTexture(
                                    ThaumicExtensions.MODID, "textures/models/pillars/pillar1_1.png", "base"),
                            new AdvancedModelRenderer.BoundTexture(
                                    ThaumicExtensions.MODID, "textures/models/pillars/pillar1_2.png", "floating_part"),
                            new AdvancedModelRenderer.BoundTexture(
                                    ThaumicExtensions.MODID, "textures/models/pillars/pillar1_3.png", "crystal"),
                            new AdvancedModelRenderer.BoundTexture(
                                    ThaumicExtensions.MODID, "textures/models/pillars/pillar1_4.png", "floating_crystal1"),
                    },
                    new Animation[] {
                            new Animation(null),
                            new Animation(
                                    new Animation.SimpleAnimation[] {
                                            Animation.Wave(0.05f, 10.0f, Animation.Axis.y)
                                    }
                             ),
                            new Animation(
                                    new Animation.SimpleAnimation[] {
                                               Animation.Wave(0.05f, 10.0f, Animation.Axis.y),
                                            Animation.RotateAroundItself(30.0f, Animation.Axis.y)
                                    }
                            ),
                            new Animation(
                                    new Animation.SimpleAnimation[] {
                                            Animation.Wave(0.1f, 10.0f, Animation.Axis.y),
                                            Animation.RotateAroundCenterAtRadius(0,30,0,0.5f),
                                            Animation.RotateAroundItself(1.0f, Animation.Axis.y)
                                    }
                            )
                    }
            ),
    };

    public void renderTileEntityAt(TileEnhancedInfusionPillar tile, double x, double y, double z, float tick) {
        double time2pi = (((double)System.currentTimeMillis())+tile.offset) / 1000 * Math.PI * 2;
        // lambda = 2pi = 1 second
        models[tile.tier].RenderAll((float)x,(float)y,(float)z, time2pi);
    }

    public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8) {
        renderTileEntityAt((TileEnhancedInfusionPillar)par1TileEntity, par2, par4, par6, par8);
    }
}
