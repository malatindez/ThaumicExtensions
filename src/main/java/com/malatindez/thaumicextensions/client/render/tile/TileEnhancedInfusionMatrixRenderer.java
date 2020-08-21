package com.malatindez.thaumicextensions.client.render.tile;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.malatindez.thaumicextensions.client.render.misc.Animation;
import com.malatindez.thaumicextensions.common.tiles.TileEnhancedInfusionMatrix;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class TileEnhancedInfusionMatrixRenderer extends TileEntitySpecialRenderer {

    public void renderTileEntityAt(TileEnhancedInfusionMatrix tile, double x, double y, double z, float ticks) {

    }
    @Override
    public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float ticks) {
        renderTileEntityAt((TileEnhancedInfusionMatrix)tile,x,y,z,ticks);
    }
}
