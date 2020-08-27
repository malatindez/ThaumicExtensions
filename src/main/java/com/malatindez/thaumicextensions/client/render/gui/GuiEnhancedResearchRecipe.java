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

    public void drawScreen(int par1, int par2, float par3) {
        drawDefaultBackground();
        //genResearchBackground(par1, par2, par3);
        int sw = (this.width - this.paneWidth) / 2;
        int sh = (this.height - this.paneHeight) / 2;
        if (!history.isEmpty()) {
            int mx = par1 - sw + 118;
            int my = par2 - sh + 189;
            if (mx >= 0 && my >= 0 && mx < 20 && my < 12)
                this.fontRendererObj.drawStringWithShadow(StatCollector.translateToLocal("recipe.return"), par1, par2, 16777215);
        }
    }

    public GuiEnhancedResearchRecipe(ResearchItem research, int page, double x, double y) {
        this.tex1 = "textures/gui/gui_researchbook.png";
        this.tex2 = "textures/gui/gui_researchbook_overlay.png";
        this.tooltip = null;
        this.lastCycle = 0L;
        this.reference = new ArrayList<List>();
        this.cycle = -1;
        this.research = research;
        this.guiMapX = x;
        this.guiMapY = y;
        this.mc = Minecraft.getMinecraft();
        this.pages = research.getPages();
        List<ResearchPage> p1 = Arrays.asList(this.pages);
        ArrayList<ResearchPage> p2 = new ArrayList<ResearchPage>();
        for (ResearchPage pp : p1) {
            if (pp != null && pp.type == ResearchPage.PageType.TEXT_CONCEALED && !ThaumcraftApiHelper.isResearchComplete(this.mc.thePlayer.getCommandSenderName(), pp.research))
                continue;
            p2.add(pp);
        }
        this.pages = p2.<ResearchPage>toArray(new ResearchPage[0]);
        if (research.key.equals("ASPECTS")) {
            AspectList aspectsKnownSorted = Thaumcraft.proxy.getPlayerKnowledge().getAspectsDiscovered((Minecraft.getMinecraft()).thePlayer.getCommandSenderName());
            List<String> list = (List<String>)Thaumcraft.proxy.getScannedObjects().get((Minecraft.getMinecraft()).thePlayer.getCommandSenderName());
            if (list != null && list.size() > 0)
                for (String s : list) {
                    try {
                        String s2 = s.substring(1);
                        ItemStack is = getFromCache(Integer.parseInt(s2));
                        if (is == null)
                            continue;
                        AspectList tags = ThaumcraftCraftingManager.getObjectTags(is);
                        tags = ThaumcraftCraftingManager.getBonusTags(is, tags);
                        if (tags != null && tags.size() > 0)
                            for (Aspect a : tags.getAspects()) {
                                ArrayList<ItemStack> items = this.aspectItems.get(a);
                                if (items == null)
                                    items = new ArrayList<ItemStack>();
                                ItemStack is2 = is.copy();
                                is2.stackSize = tags.getAmount(a);
                                items.add(is2);
                                this.aspectItems.put(a, items);
                            }
                    } catch (NumberFormatException e) {}
                }
            ArrayList<ResearchPage> tpl = new ArrayList<ResearchPage>();
            for (ResearchPage p : research.getPages())
                tpl.add(p);
            AspectList tal = new AspectList();
            if (aspectsKnownSorted != null) {
                int count = 0;
                for (Aspect aspect : aspectsKnownSorted.getAspectsSorted()) {
                    if (count <= 4) {
                        count++;
                        tal.add(aspect, aspectsKnownSorted.getAmount(aspect));
                    }
                    if (count == 4) {
                        count = 0;
                        tpl.add(new ResearchPage(tal.copy()));
                        tal = new AspectList();
                    }
                }
                if (count > 0)
                    tpl.add(new ResearchPage(tal));
            }
            this.pages = tpl.<ResearchPage>toArray(this.pages);
        }
        this.maxPages = this.pages.length;
        this.fr = new TCFontRenderer(this.mc.gameSettings, TCFontRenderer.FONT_NORMAL, this.mc.renderEngine, true);
        if (page % 2 == 1)
            page--;
        this.page = page;
    }
}
