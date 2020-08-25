package com.malatindez.thaumicextensions;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import com.malatindez.thaumicextensions.client.ClientProxy;
import thaumcraft.common.lib.network.EventHandlerNetwork;

@Mod(modid = ThaumicExtensions.MODID, version = ThaumicExtensions.VERSION,
        guiFactory = "thaumicextensions.client.ThaumicExtensionsGuiFactory",
        dependencies = "required-after:Forge@[10.13.2,);after:Thaumcraft;after:AWWayofTime;after:Botania")
public class ThaumicExtensions
{
    public static final String MODID = "thaumicextensions";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide = "com.malatindez.thaumicextensions.client.ClientProxy", serverSide = "com.malatindez.thaumicextensions.common.IProxy")
    public static IProxy proxy;

    @Instance("ThaumicExtensions")
    public static ThaumicExtensions instance;

    public static CreativeTabs ThaumicExtensionsTab = new CreativeTabs("tabName") {
        public Item getTabIconItem() {
            return Items.apple;
        }
    };
    @EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        proxy.preInit(e);
    }
    @EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }
    @EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }
}
