package com.malatindez.thaumicextensions.client.lib;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
public class UtilsFX extends thaumcraft.client.lib.UtilsFX {

    static Map<String, ResourceLocation> boundTextures = new HashMap<String, ResourceLocation>();
    public static void bindTexture(String mod, String texture) {
        ResourceLocation rl;
        if (boundTextures.containsKey(texture)) {
            rl = boundTextures.get(texture);
        } else {
            rl = new ResourceLocation(mod, texture);
        }
        (Minecraft.getMinecraft()).renderEngine.bindTexture(rl);
    }
    public static void bindTexture(String texture) {
        bindTexture("thaumicextensions", texture);
    }
}
