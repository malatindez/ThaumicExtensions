package com.malatindez.thaumicextensions;

import com.malatindez.thaumicextensions.common.Config;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class IProxy {
    public void preInit(FMLPreInitializationEvent e) {

    }

    public void init(FMLInitializationEvent e) {
        Config.init();
    }

    public void postInit(FMLPostInitializationEvent e) {

    }
}
