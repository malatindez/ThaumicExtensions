package com.malatindez.thaumicextensions.common.tiles;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import thaumcraft.api.TileThaumcraft;

import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("Since15")
public class TileEnhancedInfusionPillar extends TileThaumcraft {
    public byte orientation;

    public final double noise;

    public byte tier = 0;

    public final TileEnhancedInfusionMatrix matrixRef = null;

    public TileEnhancedInfusionPillar() {
        noise = ThreadLocalRandom.current().nextDouble() * ThreadLocalRandom.current().nextInt(1000000);
        this.orientation = (byte)ThreadLocalRandom.current().nextInt(0,4);
    }

    public boolean canUpdate()  {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox((this.xCoord - 1), (this.yCoord - 1), (this.zCoord - 1), (this.xCoord + 1), (this.yCoord + 3), (this.zCoord + 1));
    }

    public void readCustomNBT(NBTTagCompound nbttagcompound) {
        this.orientation = nbttagcompound.getByte("orientation");
        this.tier = nbttagcompound.getByte("tier");
    }

    public void writeCustomNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("orientation", this.orientation);
        nbttagcompound.setByte("tier", this.tier);
    }
}
