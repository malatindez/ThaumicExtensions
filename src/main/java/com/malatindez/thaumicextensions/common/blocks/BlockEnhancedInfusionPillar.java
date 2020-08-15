package com.malatindez.thaumicextensions.common.blocks;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.malatindez.thaumicextensions.common.ConfigBlocks;
import com.malatindez.thaumicextensions.common.tiles.TileEnhancedInfusionPillar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;

import static net.minecraft.world.EnumSkyBlock.Block;

public class BlockEnhancedInfusionPillar extends BlockContainer {
    public BlockEnhancedInfusionPillar() {
        super(Material.rock);
        setHardness(3.0F);
        setResistance(25.0F);
        setStepSound(net.minecraft.block.Block.soundTypeStone);
        setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        setCreativeTab(ThaumicExtensions.ThaumicExtensionsTab);
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        par3List.add(new ItemStack(par1, 1, 0));
    }


    public int getRenderType() {
        return ConfigBlocks.BlockInfusionPillarRI;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEnhancedInfusionPillar();
    }
    public TileEntity createNewTileEntity(World var1, int md) {
        return null;
    }
}
