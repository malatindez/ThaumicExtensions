package com.malatindez.thaumicextensions.client.render.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import thaumcraft.client.gui.GuiResearchBrowser;
import thaumcraft.client.gui.GuiResearchRecipe;
import thaumcraft.client.lib.TCFontRenderer;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SideOnly(Side.CLIENT)
public class GuiEnhancedResearchRecipe extends GuiScreen {
    protected static RenderItem itemRenderer = new RenderItem();

    public static LinkedList<Object[]> history = new LinkedList();

    protected int paneWidth = 256;

    protected int paneHeight = 181;

    protected double guiMapX;

    protected double guiMapY;

    protected int mouseX = 0;

    protected int mouseY = 0;

    private GuiButton button;

    private ResearchItem research;

    private ResearchPage[] pages = null;

    private int page = 0;

    private int maxPages = 0;

    TCFontRenderer fr = null;

    HashMap<Aspect, ArrayList<ItemStack>> aspectItems = new HashMap<Aspect, ArrayList<ItemStack>>();

    public static ConcurrentHashMap<Integer, ItemStack> cache = new ConcurrentHashMap<Integer, ItemStack>();

    String tex1;

    String tex2;

    private Object[] tooltip;

    private long lastCycle;

    ArrayList<List> reference;

    private int cycle;


    public static synchronized void putToCache(int key, ItemStack stack) {
        cache.put(Integer.valueOf(key), stack);
    }

    public static synchronized ItemStack getFromCache(int key) {
        return cache.get(Integer.valueOf(key));
    }

    public void initGui() {}

    protected void actionPerformed(GuiButton par1GuiButton) {
        super.actionPerformed(par1GuiButton);
    }

    protected void keyTyped(char par1, int par2) {
        if (par2 == this.mc.gameSettings.keyBindInventory.getKeyCode() || par2 == 1) {
            history.clear();
            this.mc.displayGuiScreen(new GuiEnhancedResearchBrowser(this.guiMapX, this.guiMapY));
        } else {
            super.keyTyped(par1, par2);
        }
    }

    public void onGuiClosed() {
        super.onGuiClosed();
    }

    public void drawScreen(int mx, int my, float tick) {
        drawDefaultBackground();
        super.drawScreen(mx, my, tick);
        //genResearchBackground(par1, par2, par3);
        int sw = (this.width - this.paneWidth) / 2;
        int sh = (this.height - this.paneHeight) / 2;
        if (!history.isEmpty()) {
            int mx1 = mx - sw + 118;
            int my1 = my - sh + 189;
            if (mx1 >= 0 && my1 >= 0 && mx1 < 20 && my1 < 12)
                this.fontRendererObj.drawStringWithShadow(StatCollector.translateToLocal("recipe.return"), mx, my, 16777215);
        }
    }

    public GuiEnhancedResearchRecipe(ResearchItem research, int page, double x, double y) {

    }
}
