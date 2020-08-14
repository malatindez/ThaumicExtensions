package com.malatindez.thaumicextensions.client;

import com.malatindez.thaumicextensions.IProxy;
import com.malatindez.thaumicextensions.client.render.block.BlockEnhancedInfusionPillarRenderer;
import com.malatindez.thaumicextensions.client.render.tile.TileEnhancedInfusionPillarRenderer;
import com.malatindez.thaumicextensions.common.ConfigBlocks;
import com.malatindez.thaumicextensions.common.blocks.BlockEnhancedInfusionPillar;
import com.malatindez.thaumicextensions.common.tiles.TileEnhancedInfusionPillar;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

public class ClientProxy extends IProxy {

    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
    }

    public void init(FMLInitializationEvent e) {
        super.init(e);
        setupTileRenderers();
        setupBlockRenderers();
    }

    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }
    public void registerBlockRenderer(ISimpleBlockRenderingHandler renderer) {
        RenderingRegistry.registerBlockHandler(renderer);
    }
    public void registerTileEntitySpecialRenderer(Class tile, TileEntitySpecialRenderer renderer) {
        ClientRegistry.bindTileEntitySpecialRenderer(tile, renderer);
    }
    void setupBlockRenderers() {
        ConfigBlocks.BlockInfusionPillarRI = RenderingRegistry.getNextAvailableRenderId();
        registerBlockRenderer((ISimpleBlockRenderingHandler)new BlockEnhancedInfusionPillarRenderer());
    }
    void setupTileRenderers() {
        registerTileEntitySpecialRenderer(TileEnhancedInfusionPillar.class, (TileEntitySpecialRenderer)new TileEnhancedInfusionPillarRenderer());
    }
}
