package com.malatindez.thaumicextensions.common.blocks;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

public class BlockEnhancedInfusionMatrix extends BlockContainer {
    public BlockEnhancedInfusionMatrix() {
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

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return null;
    }
}
