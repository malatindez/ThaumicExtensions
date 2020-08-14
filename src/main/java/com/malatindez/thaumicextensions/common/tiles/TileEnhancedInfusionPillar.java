package com.malatindez.thaumicextensions.common.tiles;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import thaumcraft.api.TileThaumcraft;

import java.util.concurrent.ThreadLocalRandom;

public class TileEnhancedInfusionPillar extends TileThaumcraft {
    public byte orientation = 6;

    public float offset = 6;

    public byte tier = 0;

    public TileEnhancedInfusionPillar() {
        offset = ThreadLocalRandom.current().nextFloat() * 20.0f; // from 1 to 20
        this.orientation = (byte)ThreadLocalRandom.current().nextInt(2,6);
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
        this.orientation = nbttagcompound.getByte("tier");
    }

    public void writeCustomNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("orientation", this.orientation);
        nbttagcompound.setByte("tier", this.tier);
    }
}
