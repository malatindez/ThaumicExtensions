package com.malatindez.thaumicextensions.common;

import com.malatindez.thaumicextensions.common.blocks.BlockEnhancedInfusionPillar;
import com.malatindez.thaumicextensions.common.blocks.BlockInfusionStabiliser;
import com.malatindez.thaumicextensions.common.tiles.TileEnhancedInfusionPillar;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;

public class ConfigBlocks {
    public static int BlockInfusionPillarRI = -1;

    public static void init() {
        registerBlocks();
        registerTileEntities();
    }


    private static void registerBlocks() {
        GameRegistry.registerBlock( new BlockInfusionStabiliser(Material.iron, "infusionStabiliser",null), "infusionStabiliser");
        GameRegistry.registerBlock( new BlockEnhancedInfusionPillar(), "blockInfusionPillar");
    }

    private static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEnhancedInfusionPillar.class, "TileEnhancedInfusionPillar");
    }
}
