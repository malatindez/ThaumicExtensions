package com.malatindez.thaumicextensions.client.render.tile;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.malatindez.thaumicextensions.client.render.misc.AdvancedModelRenderer;
import com.malatindez.thaumicextensions.client.render.misc.Animation;
import com.malatindez.thaumicextensions.common.tiles.TileEnhancedInfusionMatrix;
import com.malatindez.thaumicextensions.common.tiles.TileEnhancedInfusionPillar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class TileEnhancedInfusionMatrixRenderer extends TileEntitySpecialRenderer {
    private static final AdvancedModelRenderer model = new AdvancedModelRenderer(
            new ResourceLocation(ThaumicExtensions.MODID, "models/InfusionMatrix.obj"),
            new AdvancedModelRenderer.Part[] {
                    new AdvancedModelRenderer.Part(
                            ThaumicExtensions.MODID, "textures/models/InfusionMatrix.png","base")
            },
            new Animation[] {
                    new Animation(null)
            }
    );

    public void renderTileEntityAt(TileEnhancedInfusionMatrix tile, double x, double y, double z, float ticks) {

    }
    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float ticks) {
        renderTileEntityAt((TileEnhancedInfusionMatrix)tile,x,y,z,ticks);
    }
}
