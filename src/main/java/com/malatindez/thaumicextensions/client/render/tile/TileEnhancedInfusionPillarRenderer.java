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
                    new ResourceLocation(ThaumicExtensions.MODID, "Pillar1.obj"),
                    new AdvancedModelRenderer.BoundTexture[] {
                            new AdvancedModelRenderer.BoundTexture(
                                    ThaumicExtensions.MODID, "textures/model/pillar1_1.png", "base"),
                            new AdvancedModelRenderer.BoundTexture(
                                    ThaumicExtensions.MODID, "textures/model/pillar1_2.png", "floating_part"),
                            new AdvancedModelRenderer.BoundTexture(
                                    ThaumicExtensions.MODID, "textures/model/pillar1_3.png", "crystal"),
                            new AdvancedModelRenderer.BoundTexture(
                                    ThaumicExtensions.MODID, "textures/model/pillar1_4.png", "floating_crystals1"),
                    },
                    new Animation[] {
                            new Animation(null),
                            new Animation(
                                    new Animation.SimpleAnimation[] {
                                            Animation.Wave(0.1f, 10.0f, Animation.Axis.y)
                                    }
                            ),
                            new Animation(
                                    new Animation.SimpleAnimation[] {
                                            Animation.Wave(0.1f, 10.0f, Animation.Axis.y),
                                            Animation.RotateAroundItself(6.0f, Animation.Axis.y)
                                    }
                            ),
                            new Animation(
                                    new Animation.SimpleAnimation[] {
                                            Animation.Wave(0.25f, 30.0f, Animation.Axis.y),
                                            Animation.RotateAroundCenterAtRadius(0,6,0,0.5f)
                                    }
                            )
                    }
            ),
    };



    private static final ResourceLocation BALL = new ResourceLocation(ThaumicExtensions.MODID, "models/pillars/ball.obj");
    private IModelCustom ball = AdvancedModelLoader.loadModel(BALL);

    public void renderModel(IModelCustom model, int orientation, String texture, float x, float y, float z) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (orientation == 3) {
            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
        } else if (orientation == 4) {
            GL11.glRotatef(270.0F, 0.0F, 0.0F, 1.0F);
        } else if (orientation == 5) {
            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        }
        UtilsFX.bindTexture(ThaumicExtensions.MODID, texture);
        model.renderAll();
        GL11.glPopMatrix();
    }

    public void renderBall(float x, float y, float z) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        UtilsFX.bindTexture(ThaumicExtensions.MODID, "models/pillars/ball.png");
        this.ball.renderAll();
        GL11.glPopMatrix();
    }
    public void renderFirstTier(TileEnhancedInfusionPillar tile, float x, float y, float z, double o) {
        this.renderModel(this.PILLAR1_PART1, tile.orientation,
                "models/pillars/pillar1_1.png",
                x + 0.5f,
                y,
                 z + 0.5f);

        float xOffset = 0.75f,  zOffset = 0.75f;
        if (tile.orientation == 3) {
            zOffset = 0.25f;
        } else if (tile.orientation == 4) {
            xOffset = 0.25f;
        } else if (tile.orientation == 5) {
            xOffset = 0.25f;
            zOffset = 0.25f;
        }
        this.renderModel(this.PILLAR1_PART2, tile.orientation,
                "models/pillars/pillar1_2.png",
                x + xOffset,
                y + 2.0f + 0.05f * (float)java.lang.Math.cos((o+0.1)/15.0), // 15s lambda
                z + zOffset);
        renderBall(x + xOffset,
                   y + 2.65f + 0.1f * (float)java.lang.Math.sin((o+0.2)/30.0), // 30s lambda
                   z + zOffset);
    }
    private long lastTime = 0;
    public void renderTileEntityAt(TileEnhancedInfusionPillar tile, double x, double y, double z, float tick) {
        double time2pi = (double)(System.currentTimeMillis()+tile.offset) / 1000 * Math.PI * 2;
        // lambda = 2pi = 1 second
        models[tile.tier].RenderAll((float)x,(float)y,(float)z, time2pi);
    }

    public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8) {
        renderTileEntityAt((TileEnhancedInfusionPillar)par1TileEntity, par2, par4, par6, par8);
    }
}
