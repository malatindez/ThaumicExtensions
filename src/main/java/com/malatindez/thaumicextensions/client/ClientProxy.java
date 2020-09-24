package com.malatindez.thaumicextensions.client;

import com.malatindez.thaumicextensions.IProxy;
import com.malatindez.thaumicextensions.client.render.block.BlockEnhancedInfusionPillarRenderer;
import com.malatindez.thaumicextensions.client.render.gui.GuiEnhancedResearchRecipe;
import com.malatindez.thaumicextensions.client.render.tile.TileEnhancedInfusionPillarRenderer;
import com.malatindez.thaumicextensions.common.ConfigBlocks;
import com.malatindez.thaumicextensions.common.tiles.TileEnhancedInfusionPillar;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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
        registerBlockRenderer(new BlockEnhancedInfusionPillarRenderer());
    }
    void setupTileRenderers() {
        registerTileEntitySpecialRenderer(TileEnhancedInfusionPillar.class, new TileEnhancedInfusionPillarRenderer());
    }
    public World getClientWorld() {
        return (FMLClientHandler.instance().getClient()).theWorld;
    }

    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (world instanceof net.minecraft.client.multiplayer.WorldClient) {
            if (id == 0)
                return new GuiEnhancedResearchRecipe(null, 0,0,0);
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile == null)
                return null;
        }
        return null;
    }
}
