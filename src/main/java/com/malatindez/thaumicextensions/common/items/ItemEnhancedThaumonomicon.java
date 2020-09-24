package com.malatindez.thaumicextensions.common.items;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncAspects;
import thaumcraft.common.lib.network.playerdata.PacketSyncResearch;
import thaumcraft.common.lib.research.ResearchManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemEnhancedThaumonomicon extends Item {
    @SideOnly(Side.CLIENT)
    public IIcon icon;

    @SideOnly(Side.CLIENT)
    public IIcon iconCheat;

    public ItemEnhancedThaumonomicon() {
        setHasSubtypes(true);
        setMaxStackSize(1);
        setCreativeTab(ThaumicExtensions.ThaumicExtensionsTab);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir) {
        this.icon = ir.registerIcon("thaumcraft:thaumonomicon");
        this.iconCheat = ir.registerIcon("thaumcraft:thaumonomiconcheat");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int par1) {
        return (par1 != 42) ? this.icon : this.iconCheat;
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, ArrayList<ItemStack> par3List) {
        par3List.add(new ItemStack(this, 1, 0));
        if (Config.allowCheatSheet)
            par3List.add(new ItemStack(this, 1, 42));
    }

    public ItemStack onItemRightClick(ItemStack stack, World par2World, EntityPlayer player) {
        if (!par2World.isRemote) {
            if (Config.allowCheatSheet && stack.getItemDamage() == 42) {
                Collection<ResearchCategoryList> rc = ResearchCategories.researchCategories.values();
                for (ResearchCategoryList cat : rc) {
                    Collection<ResearchItem> rl = cat.research.values();
                    for (ResearchItem ri : rl) {
                        if (!ResearchManager.isResearchComplete(player.getCommandSenderName(), ri.key))
                            Thaumcraft.proxy.getResearchManager().completeResearch(player, ri.key);
                    }
                }
                for (Aspect aspect : Aspect.aspects.values()) {
                    if (!Thaumcraft.proxy.getPlayerKnowledge().hasDiscoveredAspect(player.getCommandSenderName(), aspect))
                        Thaumcraft.proxy.researchManager.completeAspect(player, aspect, (short)50);
                }
            } else {
                Collection<ResearchCategoryList> rc = ResearchCategories.researchCategories.values();
                for (ResearchCategoryList cat : rc) {
                    Collection<ResearchItem> rl = cat.research.values();
                    for (ResearchItem ri : rl) {
                        if (ResearchManager.isResearchComplete(player.getCommandSenderName(), ri.key) &&
                                ri.siblings != null)
                            for (String sib : ri.siblings) {
                                if (!ResearchManager.isResearchComplete(player.getCommandSenderName(), sib))
                                    Thaumcraft.proxy.getResearchManager().completeResearch(player, sib);
                            }
                    }
                }
            }
            PacketHandler.INSTANCE.sendTo(new PacketSyncResearch(player), (EntityPlayerMP)player);
            PacketHandler.INSTANCE.sendTo(new PacketSyncAspects(player), (EntityPlayerMP)player);
        } else {
            par2World.playSound(player.posX, player.posY, player.posZ, "thaumcraft:page", 1.0F, 1.0F, false);
        }
        player.openGui(ThaumicExtensions.instance, 0, par2World, 0, 0, 0);
        return stack;
    }

    public EnumRarity getRarity(ItemStack itemstack) {
        return (itemstack.getItemDamage() != 42) ? EnumRarity.uncommon : EnumRarity.epic;
    }

    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, ArrayList<String> par3List, boolean par4) {
        if (par1ItemStack.getItemDamage() == 42)
            par3List.add("Cheat Sheet");
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
    }
}
