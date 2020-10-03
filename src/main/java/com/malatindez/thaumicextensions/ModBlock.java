package com.malatindez.thaumicextensions;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

import java.util.Random;

public class ModBlock extends Block {
    private final Item toDrop;
    public ModBlock(Material material, String blockName, Item toDrop) {
        super(material);
        this.setBlockName(blockName);
        this.setBlockTextureName(ThaumicExtensions.MODID + ":" + blockName);
        this.setCreativeTab(ThaumicExtensions.ThaumicExtensionsTab);
        this.setHardness(4.0F);
        this.setHarvestLevel("pickaxe", 1);
        this.toDrop = toDrop;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune)
    {
        if(toDrop != null) {
            return toDrop;
        }
        return Item.getItemFromBlock(this);
    }
}