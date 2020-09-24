package com.malatindez.thaumicextensions.client.render.tile;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.malatindez.thaumicextensions.client.lib.Transformation;
import com.malatindez.thaumicextensions.client.render.misc.Animation;
import com.malatindez.thaumicextensions.client.render.misc.TileEntitySpecialRenderer;
import com.malatindez.thaumicextensions.common.tiles.TileEnhancedInfusionPillar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;

@SuppressWarnings("ConstantConditions")
@SideOnly(Side.CLIENT)
public class TileEnhancedInfusionPillarRenderer extends TileEntitySpecialRenderer {
    private static final Animation floatingPartAnimation = new Animation(new Animation.SimpleAnimation[] {
        Animation.Wave(0.05f, 10.0f, Animation.Axis.y)
    });

    private static final Animation[] Pillar1_animations =
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
                                    Animation.RotateAroundItself(12.0f, Animation.Axis.y),
                                    Animation.RotateAroundCenterAtRadius(0.0f,1.0f,0.0f,0.5f)
                            }
                    )
            };
    private static final Model[] Pillar1_models = new Model[] {
            new Model(
                    AdvancedModelLoader.loadModel(
                            new ResourceLocation(ThaumicExtensions.MODID, "models/Pillar_T1/base.obj")),
                    ThaumicExtensions.MODID,
                    "textures/models/Pillar_T1/base.png",
                    new Transformation(0.5f,0.759259f,0.5f)
            ),
            new Model(
                    AdvancedModelLoader.loadModel(
                            new ResourceLocation(ThaumicExtensions.MODID, "models/Pillar_T1/floating_part.obj")),
                    ThaumicExtensions.MODID,
                    "textures/models/Pillar_T1/floating_part.png",
                    new Transformation(0.5f,2.1f,0.5f)
            ),
            new AlphaModel(
                    AdvancedModelLoader.loadModel(
                            new ResourceLocation(ThaumicExtensions.MODID, "models/Pillar_T1/crystal.obj")),
                    ThaumicExtensions.MODID,
                    "textures/models/Pillar_T1/crystal_blue.png",
                    new Transformation(0.5f,2.9f,0.5f)
            ),
            new AlphaModel(
                    AdvancedModelLoader.loadModel(
                            new ResourceLocation(ThaumicExtensions.MODID, "models/Pillar_T1/crystal.obj")),
                    ThaumicExtensions.MODID,
                    "textures/models/Pillar_T1/crystal_pink.png",
                    new Transformation(0.5f,2.9f,0.5f,0,0,0,0.4f,0.4f,0.4f)
            ),

    };
    @SuppressWarnings("StatementWithEmptyBody")
    public void renderTileEntityAt(TileEnhancedInfusionPillar tile, double x, double y, double z, float tick) {
        // lambda = 2pi = 1 seconds
        this.renderModels(
                tile.noise,
                Pillar1_models,
                Pillar1_animations,
                new Transformation[] {
                    new Transformation(),
                    new Transformation(),
                    new Transformation(),
                    new Transformation()
                },
                new Transformation((float)x,(float)y,(float)z)
        );
        if (tile.matrixRef != null && tile.matrixRef.crafting) {
            // TODO crafting matrix
        }
    }

    @Override
    public void renderTileEntityAt(TileEntity par1TileEntity, double par2, double par4, double par6, float par8) {
        renderTileEntityAt((TileEnhancedInfusionPillar)par1TileEntity, par2, par4, par6, par8);
    }
}
