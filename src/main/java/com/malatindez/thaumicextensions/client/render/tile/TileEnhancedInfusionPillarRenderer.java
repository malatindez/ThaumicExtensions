package com.malatindez.thaumicextensions.client.render.tile;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.malatindez.thaumicextensions.client.lib.UtilsFX;
import com.malatindez.thaumicextensions.common.tiles.TileEnhancedInfusionPillar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;
import java.util.concurrent.ThreadLocalRandom;

import static net.minecraft.util.MathHelper.cos;
import static net.minecraft.util.MathHelper.sin;

public class TileEnhancedInfusionPillarRenderer extends TileEntitySpecialRenderer {

    private static final ResourceLocation PILLAR1_P1 = new ResourceLocation(ThaumicExtensions.MODID, "models/pillars/pillar1_1.obj");
    private static final ResourceLocation PILLAR1_P2 = new ResourceLocation(ThaumicExtensions.MODID, "models/pillars/pillar1_2.obj");
    private IModelCustom PILLAR1_PART1 = AdvancedModelLoader.loadModel(PILLAR1_P1);
    private IModelCustom PILLAR1_PART2 = AdvancedModelLoader.loadModel(PILLAR1_P2);

    private static final ResourceLocation BALL = new ResourceLocation(ThaumicExtensions.MODID, "models/pillars/ball.obj");
    private IModelCustom ball2 = AdvancedModelLoader.loadModel(BALL);

    public void renderModel(IModelCustom model, int orientation, String texture, float x, float y, float z) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, z);
        GL11.glRotatef(90.0F, -1.0F, 0.0F, 0.0F);
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
        this.ball2.renderAll();
        GL11.glPopMatrix();
    }
    public void renderFirstTier(TileEnhancedInfusionPillar tile, float x, float y, float z, float o) {
        this.renderModel(this.PILLAR1_PART1, tile.orientation,
                "models/pillars/pillar1_1.png",
                x + 0.5f,
                y,
                 z + 0.5f);

        this.renderModel(this.PILLAR1_PART2, tile.orientation,
                "models/pillars/pillar1_2.png",
                x + 0.5f,
                y + 2.2f + 0.1f * cos((o+0.1f)/15.0f), // 15s rendering delay
                z + 0.5f);

        float xOffset = 0.625f,  zOffset = 0.625f;
        if (tile.orientation == 3) {
            zOffset = 0.375f;
        } else if (tile.orientation == 4) {
            xOffset = 0.375f;
        } else if (tile.orientation == 5) {
            xOffset = 0.375f;
            zOffset = 0.375f;
        }
        renderBall(x + xOffset,
                   y + 2.7f + 0.15f * sin((o+0.2f)/30.0f), // 30s rendering delay
                   z + zOffset);
    }
    public void renderTileEntityAt(TileEnhancedInfusionPillar tile, double x, double y, double z, float tick) {
        float o = (tick + tile.offset) * (float)Math.PI/10.0f;
        // tick mod 20 -> [0, 2pi]
        switch(tile.tier) {
            case 0:
                renderFirstTier(tile,(float)x,(float)y,(float)z,o);
        }



        /*float x = (float)((System.currentTimeMillis()+ tile.offset) % 30000 ) / 30000 * (float)Math.PI;
        x = 3*cos(sin(cos(x))) - 5.0f/2.0f; // [-0.5, 0.5]
        x = x * 0.1f; // [-0.5, 0.5] transforming to [-0.1, 0.1]

        this.model2.renderAll();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        float y = (float)((System.currentTimeMillis()+ tile.offset) % 45000 ) / 45000 * (float)Math.PI;
        y = 3*cos(sin(cos(y))) - 5.0f/2.0f; // [-0.5, 0.5]
        y = y * 0.15f; // [-0.5, 0.5] transforming to [-0.2, 0.2]



        GL11.glTranslatef((float)par2 + xOffset, (float)par4 + 2.7F + y, (float)par6 + zOffset);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        UtilsFX.bindTexture("thaumicextensions", "models/pillars/ball.png");
        this.ball2.renderAll();
        GL11.glPopMatrix();*/
    }

    public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8) {
        renderTileEntityAt((TileEnhancedInfusionPillar)par1TileEntity, par2, par4, par6, par8);
    }
}
