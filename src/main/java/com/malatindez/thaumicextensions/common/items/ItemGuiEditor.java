package com.malatindez.thaumicextensions.common.items;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemGuiEditor extends Item {
    @SideOnly(Side.CLIENT)
    public IIcon icon;
    public ItemGuiEditor() {
        setHasSubtypes(true);
        setMaxStackSize(1);
        setCreativeTab(ThaumicExtensions.ThaumicExtensionsTab);
    }
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        this.icon = ir.registerIcon("thaumcraft:thaumonomiconcheat");
    }
    public ItemStack onItemRightClick(ItemStack stack, World par2World, EntityPlayer player) {
        player.openGui(ThaumicExtensions.instance, 1, par2World, 0, 0, 0);
        return stack;
    }
}
