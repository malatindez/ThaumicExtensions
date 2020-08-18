package com.malatindez.thaumicextensions.client.render.tile;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.malatindez.thaumicextensions.client.render.misc.Animation;
import com.malatindez.thaumicextensions.client.render.misc.TileEntitySpecialRenderer;
import com.malatindez.thaumicextensions.common.tiles.TileEnhancedInfusionPillar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

@SideOnly(Side.CLIENT)
public class TileEnhancedInfusionPillarRenderer extends TileEntitySpecialRenderer {
    private static final Animation floatingPartAnimation = new Animation(new Animation.SimpleAnimation[] {
        Animation.Wave(0.05f, 10.0f, Animation.Axis.y)
    });
    private static final IModelCustom model =
            AdvancedModelLoader.loadModel(
                    new ResourceLocation(ThaumicExtensions.MODID,
                            "models/pillars/Pillar1.obj"));
    private static final Animation[] animations =
            new Animation[] {
                    new Animation(null),
                    floatingPartAnimation,
                    new Animation(
                            new Animation.SimpleAnimation[] {
                                    Animation.Wave(0.05f, 10.0f, Animation.Axis.y),
                                    Animation.RotateAroundItself(30.0f, Animation.Axis.y)
                            }
                    ),
                    new Animation(
                            new Animation.SimpleAnimation[] {
                                    Animation.Wave(0.1f, 10.0f, Animation.Axis.y),
                                    Animation.RotateAroundItself(1.0f, Animation.Axis.y)
                            }
                    )
            };
    private static final Part[] parts = new Part[] {
            new Part(
                    ThaumicExtensions.MODID, "textures/models/pillars/pillar1_1.png", "base"),
            new Part(
                    ThaumicExtensions.MODID, "textures/models/pillars/pillar1_2.png", "floating_part"),
            new AlphaPart(
                    ThaumicExtensions.MODID, "textures/models/pillars/pillar1_3.png", "crystal"),
            new AlphaPart(
                    ThaumicExtensions.MODID, "textures/models/pillars/pillar1_4.png", "floating_crystal1")
    };
    public void renderTileEntityAt(TileEnhancedInfusionPillar tile, double x, double y, double z, float tick) {
        // lambda = 2pi = 1 second
        this.renderAll(
                (TileEntity)tile,
                new Animation.Transformation(
                        (float)x + 0.5f, (float)y,(float)z + 0.5f,
                        0, 90 * tile.orientation, 0,
                        1, 1, 1
                ),
                tile.noise
        );
        if (tile.matrixRef != null && tile.matrixRef.crafting) {
            // TODO crafting matrix
        }
    }

    @Override
    protected IModelCustom getModel() {
        return model;
    }

    @Override
    protected Animation getAnimation(int i) {
        return animations[i];
    }

    @Override
    protected Part getPart(int i) {
        return parts[i];
    }

    @Override
    protected Part[] getParts() {
        return parts;
    }

    @Override
    public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8) {
        renderTileEntityAt((TileEnhancedInfusionPillar)par1TileEntity, par2, par4, par6, par8);
    }
}
