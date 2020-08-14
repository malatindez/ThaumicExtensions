package com.malatindez.thaumicextensions.common.blocks;
import com.malatindez.thaumicextensions.ModBlock;
import cpw.mods.fml.common.Optional;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import thaumcraft.api.crafting.IInfusionStabiliser;

@Optional.Interface(modid = "Thaumcraft", iface = "thaumcraft.api.crafting.IInfusionStabiliser", striprefs = true)
public class BlockInfusionStabiliser extends ModBlock implements IInfusionStabiliser {

    public BlockInfusionStabiliser(Material material, String blockName, Item toDrop) {
        super(material, blockName, toDrop);
    }

    @Override
    public boolean canStabaliseInfusion(World world, int x, int y, int z) {
        return true;
    }
}
