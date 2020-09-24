package com.malatindez.thaumicextensions.common;

import com.malatindez.thaumicextensions.common.blocks.BlockEnhancedInfusionPillar;
import com.malatindez.thaumicextensions.common.tiles.TileEnhancedInfusionPillar;
import cpw.mods.fml.common.registry.GameRegistry;

public class ConfigBlocks {
    public static int BlockInfusionPillarRI = -1;

    public static void init() {
        registerBlocks();
        registerTileEntities();
    }


    private static void registerBlocks() {
        GameRegistry.registerBlock( new BlockEnhancedInfusionPillar(), "blockInfusionPillar");
    }

    private static void registerTileEntities() {
        GameRegistry.registerTileEntity(TileEnhancedInfusionPillar.class, "TileEnhancedInfusionPillar");
    }
}
