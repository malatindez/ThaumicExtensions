package com.malatindez.thaumicextensions.common.tiles;


import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.crafting.IInfusionStabiliser;
import thaumcraft.api.crafting.InfusionEnchantmentRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.wands.IWandable;


public class TileEnhancedInfusionMatrix extends TileThaumcraft implements IWandable, IAspectContainer {
    private ArrayList<ChunkCoordinates> pedestals = new ArrayList<ChunkCoordinates>();

    private int dangerCount = 0;

    public boolean active = false;

    public boolean crafting = false;

    public boolean checkSurroundings = true;

    public int symmetry = 0;

    public int instability = 0;
    private AspectList recipeEssentia = new AspectList();

    private ArrayList<ItemStack> recipeIngredients = null;

    private Object recipeOutput = null;

    private String recipePlayer = null;

    private String recipeOutputLabel = null;

    private ItemStack recipeInput = null;

    private int recipeInstability = 0;

    private int recipeXP = 0;

    private int recipeType = 0;

    private int recipeMana = 0;

    private int recipeBlood = 0;

    private int recipeEnergy = 0;

    public class SourceFX {
        public ChunkCoordinates loc;

        public int ticks;

        public int color;

        public int entity;

        public SourceFX(ChunkCoordinates loc, int ticks, int color) {
            this.loc = loc;
            this.ticks = ticks;
            this.color = color;
        }
    }
    public HashMap<String, SourceFX> sourceFX = new HashMap<String, SourceFX>();

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox((this.xCoord - 1), (this.yCoord - 1), (this.zCoord - 1), (this.xCoord + 1), (this.yCoord + 1), (this.zCoord + 1));
    }


    public void readCustomNBT(NBTTagCompound nbtCompound) {
        this.active = nbtCompound.getBoolean("active");
        this.crafting = nbtCompound.getBoolean("crafting");
        this.instability = nbtCompound.getShort("instability");
        this.recipeEssentia.readFromNBT(nbtCompound);
    }

    public void writeCustomNBT(NBTTagCompound nbtCompound) {
        nbtCompound.setBoolean("active", this.active);
        nbtCompound.setBoolean("crafting", this.crafting);
        nbtCompound.setShort("instability", (short)this.instability);
        this.recipeEssentia.writeToNBT(nbtCompound);
    }

    public void readFromNBT(NBTTagCompound nbtCompound) {
        super.readFromNBT(nbtCompound);
        NBTTagList nbttaglist = nbtCompound.getTagList("recipein", 10);
        this.recipeIngredients = new ArrayList<ItemStack>();
        for (int i = 0; i < nbttaglist.tagCount(); i++) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbttagcompound1.getByte("item");
            this.recipeIngredients.add(ItemStack.loadItemStackFromNBT(nbttagcompound1));
        }
        String rot = nbtCompound.getString("rotype");
        if (rot != null && rot.equals("@")) {
            this.recipeOutput = ItemStack.loadItemStackFromNBT(nbtCompound.getCompoundTag("recipeout"));
        } else if (rot != null) {
            this.recipeOutputLabel = rot;
            this.recipeOutput = nbtCompound.getTag("recipeout");
        }
        this.recipeInput = ItemStack.loadItemStackFromNBT(nbtCompound.getCompoundTag("recipeinput"));
        this.recipeInstability = nbtCompound.getInteger("recipeinst");
        this.recipeType = nbtCompound.getInteger("recipetype");
        this.recipeXP = nbtCompound.getInteger("recipexp");
        this.recipePlayer = nbtCompound.getString("recipeplayer");
        this.recipeMana = nbtCompound.getInteger("recipemana");
        this.recipeBlood = nbtCompound.getInteger("recipeblood");
        this.recipeEnergy = nbtCompound.getInteger("recipeenergy");
        if (this.recipePlayer.isEmpty())
            this.recipePlayer = null;
    }

    public void writeToNBT(NBTTagCompound nbtCompound) {
        super.writeToNBT(nbtCompound);
        if (this.recipeIngredients != null && this.recipeIngredients.size() > 0) {
            NBTTagList nbttaglist = new NBTTagList();
            int count = 0;
            for (ItemStack stack : this.recipeIngredients) {
                if (stack != null) {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setByte("item", (byte)count);
                    stack.writeToNBT(nbttagcompound1);
                    nbttaglist.appendTag(nbttagcompound1);
                    count++;
                }
            }
            nbtCompound.setTag("recipein", nbttaglist);
        }
        if (this.recipeOutput != null && this.recipeOutput instanceof ItemStack)
            nbtCompound.setString("rotype", "@");
        if (this.recipeOutput != null && this.recipeOutput instanceof NBTBase)
            nbtCompound.setString("rotype", this.recipeOutputLabel);
        if (this.recipeOutput != null && this.recipeOutput instanceof ItemStack)
            nbtCompound.setTag("recipeout", ((ItemStack)this.recipeOutput).writeToNBT(new NBTTagCompound()));
        if (this.recipeOutput != null && this.recipeOutput instanceof NBTBase)
            nbtCompound.setTag("recipeout", (NBTBase)this.recipeOutput);
        if (this.recipeInput != null)
            nbtCompound.setTag("recipeinput", this.recipeInput.writeToNBT(new NBTTagCompound()));
        nbtCompound.setInteger("recipeinst", this.recipeInstability);
        nbtCompound.setInteger("recipetype", this.recipeType);
        nbtCompound.setInteger("recipexp", this.recipeXP);
        nbtCompound.setInteger("recipemana", this.recipeMana);
        nbtCompound.setInteger("recipeblood", this.recipeBlood);
        nbtCompound.setInteger("recipeenergy", this.recipeEnergy);
        if (this.recipePlayer == null) {
            nbtCompound.setString("recipeplayer", "");
        } else {
            nbtCompound.setString("recipeplayer", this.recipePlayer);
        }
    }

    public boolean canUpdate() {
        return true;
    }

    public int count = 0;

    public int craftCount = 0;

    public float startUp;

    private int countDelay = 10;

    public int tier = 0;

    public void updateEntity() {
        super.updateEntity();
        this.count++;
        if (this.checkSurroundings) {
            this.checkSurroundings = false;
            //getSurroundings();
        }
        if (this.worldObj.isRemote) {
            //doEffects();
        } else {
            if (this.count % (this.crafting ? 20 : 100) == 0 &&
                    !validLocation()) {
                this.active = false;
                markDirty();
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                return;
            }
            if (this.active && this.crafting && this.count % this.countDelay == 0) {
               // craftCycle();
                markDirty();
            }
        }
    }

    ArrayList<ItemStack> ingredients = new ArrayList<ItemStack>();

    public boolean validLocation() {
        TileEntity te;
        te = this.worldObj.getTileEntity(this.xCoord, this.yCoord - 2, this.zCoord);
        if (!(te.getClass().getSimpleName().equals("TilePedestal")))
            return false;
        te = this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord - 2, this.zCoord + 1);
        if (!(te instanceof TileEnhancedInfusionPillar))
            return false;
        te = this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord - 2, this.zCoord - 1);
        if (!(te instanceof TileEnhancedInfusionPillar))
            return false;
        te = this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord - 2, this.zCoord - 1);
        if ((te instanceof TileEnhancedInfusionPillar))
            return false;
        te = this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord - 2, this.zCoord + 1);
        if (!(te instanceof TileEnhancedInfusionPillar))
            return false;
        return true;
    }

    public static InfusionRecipe findMatchingInfusionRecipe(ArrayList<ItemStack> items, ItemStack input, EntityPlayer player) {
        InfusionRecipe recipe = null;
        for (Object o : ThaumcraftApi.getCraftingRecipes()) {
            if (o instanceof InfusionRecipe && ((InfusionRecipe)o).matches(items, input, player.worldObj, player)) {
                recipe = (InfusionRecipe)o;
                break;
            }
        }
        return recipe;
    }
    public static InfusionEnchantmentRecipe findMatchingInfusionEnchantmentRecipe(ArrayList<ItemStack> items, ItemStack input, EntityPlayer player) {
        InfusionEnchantmentRecipe var13 = null;
        for (Object var11 : ThaumcraftApi.getCraftingRecipes()) {
            if (var11 instanceof InfusionEnchantmentRecipe && ((InfusionEnchantmentRecipe)var11).matches(items, input, player.worldObj, player)) {
                var13 = (InfusionEnchantmentRecipe)var11;
                break;
            }
        }
        return var13;
    }

    public void craftingStart(EntityPlayer player) {
        if (!validLocation()) {
            this.active = false;
            markDirty();
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            return;
        }
        //getSurroundings();
        TileEntity te;
        this.recipeInput = null;
        te = this.worldObj.getTileEntity(this.xCoord, this.yCoord - 2, this.zCoord);
        if (te != null && (te.getClass().getSimpleName().equals("TilePedestal"))) {
            ISidedInventory ped = (ISidedInventory)te;
            if (ped.getStackInSlot(0) != null)
                this.recipeInput = ped.getStackInSlot(0).copy();
        }
        if (this.recipeInput == null)
            return;
        ArrayList<ItemStack> components = new ArrayList<ItemStack>();
        for (ChunkCoordinates cc : this.pedestals) {
            te = this.worldObj.getTileEntity(cc.posX, cc.posY, cc.posZ);
            if (te != null && (te.getClass().getSimpleName().equals("TilePedestal"))) {
                ISidedInventory ped = (ISidedInventory)te;
                if (ped.getStackInSlot(0) != null)
                    components.add(ped.getStackInSlot(0).copy());
            }
        }
        if (components.size() == 0)
            return;
        InfusionRecipe recipe = findMatchingInfusionRecipe(components, this.recipeInput, player);
        if (recipe != null) {
            this.recipeType = 0;
            this.recipeIngredients = new ArrayList<ItemStack>();
            for (ItemStack ing : recipe.getComponents()) {
                this.recipeIngredients.add(ing.copy());
            }
            if (recipe.getRecipeOutput(this.recipeInput) instanceof Object[]) {
                Object[] obj = (Object[])recipe.getRecipeOutput(this.recipeInput);
                this.recipeOutputLabel = (String)obj[0];
                this.recipeOutput = obj[1];
            } else {
                this.recipeOutput = recipe.getRecipeOutput(this.recipeInput);
            }
            this.recipeInstability = recipe.getInstability(this.recipeInput);
            this.recipeEssentia = recipe.getAspects(this.recipeInput).copy();
            this.recipePlayer = player.getCommandSenderName();
            this.instability = this.symmetry + this.recipeInstability;
            this.crafting = true;
            this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "thaumcraft:craftstart", 0.5F, 1.0F);
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            markDirty();
            return;
        }
        InfusionEnchantmentRecipe recipe2 = findMatchingInfusionEnchantmentRecipe(components, this.recipeInput, player);
        if (recipe2 != null) {
            this.recipeType = 1;
            this.recipeIngredients = new ArrayList<ItemStack>();
            for (ItemStack ing : recipe2.components)
                this.recipeIngredients.add(ing.copy());
            this.recipeOutput = recipe2.getEnchantment();
            this.recipeInstability = recipe2.calcInstability(this.recipeInput);
            AspectList esscost = recipe2.aspects.copy();
            float essmod = recipe2.getEssentiaMod(this.recipeInput);
            for (Aspect as : esscost.getAspects())
                esscost.add(as, (int)(esscost.getAmount(as) * essmod));
            this.recipeEssentia = esscost;
            this.recipeXP = recipe2.calcXP(this.recipeInput);
            this.instability = this.symmetry + this.recipeInstability;
            this.crafting = true;
            this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "thaumcraft:craftstart", 0.5F, 1.0F);
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            markDirty();
        }
    }


    @Override
    public int onWandRightClick(World world, ItemStack wandstack, EntityPlayer player, int x, int y, int z, int side, int md) {
        return 0;
    }

    @Override
    public ItemStack onWandRightClick(World world, ItemStack wandstack, EntityPlayer player) {
        return null;
    }

    @Override
    public void onUsingWandTick(ItemStack wandstack, EntityPlayer player, int count) {

    }

    @Override
    public void onWandStoppedUsing(ItemStack wandstack, World world, EntityPlayer player, int count) {

    }

    @Override
    public AspectList getAspects() {
        return this.recipeEssentia;
    }

    @Override
    public void setAspects(AspectList aspects) { }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        return 0;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return false;
    }
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return false;
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }

    @Override
    public int containerContains(Aspect tag) {
        return 0;
    }
    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }
}
