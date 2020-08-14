package com.malatindez.thaumicextensions.common;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.malatindez.thaumicextensions.common.misc.AnimatedAspect;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;

public class ConfigAspects {
    public static Aspect TierAspect;
    public static Aspect BloodAspect;
    public static Aspect ManaAspect;

    public static void init() {
        TierAspect = new Aspect("Tier", 0, (Aspect[])null, new ResourceLocation(ThaumicExtensions.MODID,"textures/aspects/tier.png"), 100);
        BloodAspect = new Aspect("Blood", 9109504, (Aspect[])null, new ResourceLocation(ThaumicExtensions.MODID,"textures/aspects/blood.png"), 500);
        ArrayList<ResourceLocation> s = new ArrayList<ResourceLocation>();
        for(int i = 0; i < 10; i++) {
            s.add(new ResourceLocation(ThaumicExtensions.MODID, "textures/aspects/mana/frame_0" + Integer.toString(i) + ".png"));
        }
        for(int i = 10; i < 48; i++) {
            s.add(new ResourceLocation(ThaumicExtensions.MODID, "textures/aspects/mana/frame_" + Integer.toString(i) + ".png"));
        }
        ManaAspect = new AnimatedAspect("Mana", 6088703, (Aspect[])null, s, 5000);
    }
}
