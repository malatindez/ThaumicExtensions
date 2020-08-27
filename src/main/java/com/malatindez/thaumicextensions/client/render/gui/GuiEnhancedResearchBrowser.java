package com.malatindez.thaumicextensions.client.render.gui;


import com.google.common.base.Charsets;
import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.SaveHandler;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import com.malatindez.thaumicextensions.client.lib.UtilsFX;
import org.lwjgl.util.vector.Vector2f;
import thaumcraft.api.IScribeTools;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.gui.GuiResearchBrowser;
import thaumcraft.client.renderers.tile.TileNodeRenderer;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.items.relics.ItemThaumonomicon;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketPlayerCompleteToServer;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.InventoryUtils;

@SideOnly(Side.CLIENT)
public class GuiEnhancedResearchBrowser extends GuiScreen {
    private static int guiMapTop;

    private static int guiMapLeft;

    private static int guiMapBottom;

    private static int guiMapRight;

    protected double field_74117_m;

    protected double field_74115_n;

    protected int mouseX = 0;

    protected int mouseY = 0;

    protected double guiMapX;

    protected double guiMapY;

    protected double guiMapTopBuf;

    protected double guiMapLeftBuf;

    private int isMouseButtonDown = 0;

    public static int lastX = -5;

    public static int lastY = -6;

    private GuiButton button;

    private LinkedList<ResearchItem> research = new LinkedList<ResearchItem>();

    public static final ResourceLocation researchBack =
            new ResourceLocation(ThaumicExtensions.MODID, "textures/gui/gui_researchBack.jpg");

    public static final ResourceLocation researchBackEldritch =
            new ResourceLocation(ThaumicExtensions.MODID, "textures/gui/gui_researchBackEldritch.jpg");

    public static HashMap<String, ArrayList<String>> completedResearch = new HashMap<String, ArrayList<String>>();

    public static ArrayList<String> highlightedItem = new ArrayList<String>();

    private static String selectedCategory = null;

    private FontRenderer galFontRenderer;

    private ResearchItem currentHighlight = null;

    private String player = "";

    private static final GuiResearchBrowser guiResearchBrowserInstance = new GuiResearchBrowser();

    private Method drawline = null;

    long popupTime = 0L;

    String popupMessage = "";

    public boolean hasScribeStuff;
    private void initDrawLine() {
        try {
            Class[] cArg = new Class[9];
            cArg[0] = cArg[1] = cArg[2] = cArg[3] = int.class;
            cArg[4] = cArg[5] = cArg[6] = cArg[7] = float.class;
            cArg[8] = boolean.class;
            this.drawline = guiResearchBrowserInstance.getClass().getDeclaredMethod("drawLine",cArg);
            drawline.setAccessible(true);
        } catch (Exception a) { }
    }
    public GuiEnhancedResearchBrowser() {
        if(drawline == null) {
            initDrawLine();
        }
        this.hasScribeStuff = false;
        short var2 = 141;
        short var3 = 141;
        this.field_74117_m = this.guiMapX = this.guiMapTopBuf = (lastX * 24 - var2 / 2 - 12);
        this.field_74115_n = this.guiMapY = this.guiMapLeftBuf = (lastY * 24 - var3 / 2);
        updateResearch();
        this.galFontRenderer = (FMLClientHandler.instance().getClient()).standardGalacticFontRenderer;
        this.player = (Minecraft.getMinecraft()).thePlayer.getCommandSenderName();
    }
    public GuiEnhancedResearchBrowser(double x, double y) {
        if(drawline == null) {
            initDrawLine();
        }
        this.hasScribeStuff = false;
        this.field_74117_m = this.guiMapX = this.guiMapTopBuf = x;
        this.field_74115_n = this.guiMapY = this.guiMapLeftBuf = y;
        this.guiMapX = x;
        this.guiMapY = y;
        updateResearch();
        this.galFontRenderer = (FMLClientHandler.instance().getClient()).standardGalacticFontRenderer;
        this.player = (Minecraft.getMinecraft()).thePlayer.getCommandSenderName();
    }
    public void updateResearch() {
        this.completedResearch = GuiResearchBrowser.completedResearch;
        if (this.mc == null)
            this.mc = Minecraft.getMinecraft();
        this.research.clear();
        this.hasScribeStuff = false;
        if (selectedCategory == null) {
            Collection<String> cats = ResearchCategories.researchCategories.keySet();
            selectedCategory = cats.iterator().next();
        }
        Collection col = (ResearchCategories.getResearchList(selectedCategory)).research.values();
        for (Object res : col)
            this.research.add((ResearchItem)res);
        if (ResearchManager.consumeInkFromPlayer((EntityPlayer)this.mc.thePlayer, false)
                && InventoryUtils.isPlayerCarrying((EntityPlayer)this.mc.thePlayer, new ItemStack(Items.paper)) >= 0)
            this.hasScribeStuff = true;
        guiMapTop = (ResearchCategories.getResearchList(selectedCategory)).minDisplayColumn * 24 - 480;
        guiMapLeft = (ResearchCategories.getResearchList(selectedCategory)).minDisplayRow * 24 - 240;
        guiMapBottom = (ResearchCategories.getResearchList(selectedCategory)).maxDisplayColumn * 24 - 112;
        guiMapRight = (ResearchCategories.getResearchList(selectedCategory)).maxDisplayRow * 24 - 61;
    }

    public void onGuiClosed() {
        short var2 = 141;
        short var3 = 141;
        lastX = (int)((this.guiMapX + (var2 / 2) + 12.0D) / 24.0D);
        lastY = (int)((this.guiMapY + (var3 / 2)) / 24.0D);
        super.onGuiClosed();
    }

    public void initGui() {}

    protected void actionPerformed(GuiButton par1GuiButton) {
        super.actionPerformed(par1GuiButton);
    }

    protected void keyTyped(char par1, int par2) {
        if (par2 == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            highlightedItem.clear();
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
        } else {
            if (par2 == 1)
                highlightedItem.clear();
            super.keyTyped(par1, par2);
        }
    }
    public void drawScreen(int mx, int my, float partialTicks) {
        if (Mouse.isButtonDown(0)) {
            if ((this.isMouseButtonDown == 0 || this.isMouseButtonDown == 1) &&
                    mx >= 34 && mx < 34 + this.width - 26 && my >= 17 && my < 17 + this.height) {
                if (this.isMouseButtonDown == 0) {
                    this.isMouseButtonDown = 1;
                } else {
                    this.guiMapX -= (mx - this.mouseX);
                    this.guiMapY -= (my - this.mouseY);
                    this.guiMapTopBuf = this.field_74117_m = this.guiMapX;
                    this.guiMapLeftBuf = this.field_74115_n = this.guiMapY;
                }
                this.mouseX = mx;
                this.mouseY = my;
            }
            if (this.guiMapTopBuf < guiMapTop)
                this.guiMapTopBuf = guiMapTop;
            if (this.guiMapLeftBuf < guiMapLeft)
                this.guiMapLeftBuf = guiMapLeft;
            if (this.guiMapTopBuf >= guiMapBottom)
                this.guiMapTopBuf = (guiMapBottom - 1);
            if (this.guiMapLeftBuf >= guiMapRight)
                this.guiMapLeftBuf = (guiMapRight - 1);
        } else {
            this.isMouseButtonDown = 0;
        }
        drawDefaultBackground();
        genResearchBackground(mx, my, partialTicks);
        if (this.popupTime > System.currentTimeMillis()) {
            int xq = 26 + 128;
            int yq = 128;
            int var41 = this.fontRendererObj.splitStringWidth(this.popupMessage, 150) / 2;
            drawGradientRect(xq - 78, yq - var41 - 3, xq + 78, yq + var41 + 3, -1073741824, -1073741824);
            this.fontRendererObj.drawSplitString(this.popupMessage, xq - 75, yq - var41, 150, -7302913);
        }
        Collection cats = ResearchCategories.researchCategories.keySet();
        int count = 0;
        boolean swop = false;
        for (Object obj : cats) {
            if (count == 11) {
                count = 0;
                swop = true;
            }
            ResearchCategoryList rcl = ResearchCategories.getResearchList((String)obj);
            if (((String)obj).equals("ELDRITCH") && !ResearchManager.isResearchComplete(this.player, "ELDRITCHMINOR")) {
                continue;
            }
            int mposx = mx - (swop ? this.width-26 : 0);
            int mposy = my - count * 24;
            if (mposx >= 0 && mposx < 26 && mposy >= 0 && mposy < 24) {

                this.fontRendererObj.drawStringWithShadow(
                        ResearchCategories.getCategoryName((String) obj),
                        mx - (swop ? (int)(1.25*this.fontRendererObj.getStringWidth((String) obj)) : -4),
                        my - 8,
                        16777215);
            }
            count++;
        }
    }
    public void updateScreen() {
        this.field_74117_m = this.guiMapX;
        this.field_74115_n = this.guiMapY;
        double var1 = this.guiMapTopBuf - this.guiMapX;
        double var3 = this.guiMapLeftBuf - this.guiMapY;
        if (var1 * var1 + var3 * var3 < 4.0D) {
            this.guiMapX += var1;
            this.guiMapY += var3;
        } else {
            this.guiMapX += var1 * 0.85D;
            this.guiMapY += var3 * 0.85D;
        }
    }

    protected void genResearchBackground(int mx, int my, float partialTicks) {
        long t = System.nanoTime() / 50000000L;
        int var4 = MathHelper.floor_double(this.field_74117_m + (this.guiMapX - this.field_74117_m) * partialTicks);
        int var5 = MathHelper.floor_double(this.field_74115_n + (this.guiMapY - this.field_74115_n) * partialTicks);
        if (var4 < guiMapTop)
            var4 = guiMapTop;
        if (var5 < guiMapLeft)
            var5 = guiMapLeft;
        if (var4 >= guiMapBottom)
            var4 = guiMapBottom - 1;
        if (var5 >= guiMapRight)
            var5 = guiMapRight - 1;
        int var8 = 26;
        int var9 = 0;
        int var10 = var8 + 16;
        int var11 = var9 + 17;
        this.zLevel = 0.0F;
        GL11.glDepthFunc(518);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, -200.0F);
        GL11.glEnable(3553);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glDisable(2896);
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        ResourceLocation tempResLocation = (ResearchCategories.getResearchList(selectedCategory)).background;
        if(tempResLocation.getResourceDomain() == "thaumcraft") {
            if(tempResLocation.getResourcePath() == "textures/gui/gui_researchback.png") {
                tempResLocation = researchBack;
            } else if(tempResLocation.getResourcePath() == "textures/gui/gui_researchbackeldritch.png") {
                tempResLocation = researchBackEldritch;
            }
        } else {
            tempResLocation = researchBack;
        }
        (Minecraft.getMinecraft()).renderEngine.bindTexture(tempResLocation);
        net.minecraft.client.gui.Gui.func_152125_a(
                var8, 0,
                0,0, 256, 256, this.width-52,this.height,
                256,256);
        GL11.glScalef(0.5F, 0.5F, 1.0F);
        GL11.glPopMatrix();
        GL11.glEnable(2929);
        GL11.glDepthFunc(515);
        if (completedResearch.get(this.player) != null)
            for (int i = 0; i < this.research.size(); i++) {
                ResearchItem researchItem = this.research.get(i);
                if (researchItem.parents != null && researchItem.parents.length > 0)
                    for (int j = 0; j < researchItem.parents.length; j++) {
                        if (researchItem.parents[j] != null && (ResearchCategories.getResearch(researchItem.parents[j])).category.equals(selectedCategory)) {
                            ResearchItem parent = ResearchCategories.getResearch(researchItem.parents[j]);
                            if (!parent.isVirtual()) {
                                int fromX = researchItem.displayColumn * 24 - var4 + 11 + var10;
                                int fromY = researchItem.displayRow * 24 - var5 + 11 + var11;
                                int toX = parent.displayColumn * 24 - var4 + 11 + var10;
                                int toY = parent.displayRow * 24 - var5 + 11 + var11;
                                boolean researched = ((ArrayList)completedResearch.get(this.player)).contains(researchItem.key);
                                boolean var29 = ((ArrayList)completedResearch.get(this.player)).contains(parent.key);
                                if (researched) {
                                    drawLine(fromX, fromY, toX, toY, 0.1F, 0.1F, 0.1F, partialTicks, false);
                                } else if (!researchItem.isLost() && ((
                                        !researchItem.isHidden() && !researchItem.isLost()) || ((ArrayList)completedResearch.get(this.player)).contains("@" + researchItem.key)) && (
                                        !researchItem.isConcealed() || canUnlockResearch(researchItem))) {
                                    if (var29) {
                                        drawLine(fromX, fromY, toX, toY, 0.0F, 1.0F, 0.0F, partialTicks, true);
                                    } else if (((!parent.isHidden() && !researchItem.isLost()) || ((ArrayList)completedResearch.get(this.player)).contains("@" + parent.key)) && (!parent.isConcealed() || canUnlockResearch(parent))) {
                                        drawLine(fromX, fromY, toX, toY, 0.0F, 0.0F, 1.0F, partialTicks, true);
                                    }
                                }
                            }
                        }
                    }
                if (researchItem.siblings != null && researchItem.siblings.length > 0)
                    for (int a = 0; a < researchItem.siblings.length; a++) {
                        if (researchItem.siblings[a] != null && (ResearchCategories.getResearch(researchItem.siblings[a])).category.equals(selectedCategory)) {
                            ResearchItem sibling = ResearchCategories.getResearch(researchItem.siblings[a]);
                            if (!sibling.isVirtual() && (
                                    sibling.parents == null || (sibling.parents != null && !Arrays.<String>asList(sibling.parents).contains(researchItem.key)))) {
                                int fromX = researchItem.displayColumn * 24 - var4 + 11 + var10;
                                int fromY = researchItem.displayRow * 24 - var5 + 11 + var11;
                                int toX = sibling.displayColumn * 24 - var4 + 11 + var10;
                                int toY = sibling.displayRow * 24 - var5 + 11 + var11;
                                boolean var28 = ((ArrayList)completedResearch.get(this.player)).contains(researchItem.key);
                                boolean var29 = ((ArrayList)completedResearch.get(this.player)).contains(sibling.key);
                                if (var28) {
                                    drawLine(fromX, fromY, toX, toY, 0.1F, 0.1F, 0.2F, partialTicks, false);
                                } else if (!researchItem.isLost() && (
                                        !researchItem.isHidden() || ((ArrayList)completedResearch.get(this.player)).contains("@" + researchItem.key)) && (
                                        !researchItem.isConcealed() || canUnlockResearch(researchItem))) {
                                    if (var29) {
                                        drawLine(fromX, fromY, toX, toY, 0.0F, 1.0F, 0.0F, partialTicks, true);
                                    } else if ((!sibling.isHidden() || ((ArrayList)completedResearch.get(this.player)).contains("@" + sibling.key)) && (!sibling.isConcealed() || canUnlockResearch(sibling))) {
                                        drawLine(fromX, fromY, toX, toY, 0.0F, 0.0F, 1.0F, partialTicks, true);
                                    }
                                }
                            }
                        }
                    }
            }
        this.currentHighlight = null;
        RenderItem itemRenderer = new RenderItem();
        GL11.glEnable(32826);
        GL11.glEnable(2903);
        if (completedResearch.get(this.player) != null)
            for (int i = 0; i < this.research.size(); i++) {
                ResearchItem researchItem = this.research.get(i);
                int var26 = researchItem.displayColumn * 24 - var4;
                int var27 = researchItem.displayRow * 24 - var5;
                if (!researchItem.isVirtual() && var26 >= -24 && var27 >= -24 && var26 <= this.width - 26 && var27 <= this.height) {
                    int var42 = var10 + var26;
                    int var41 = var11 + var27;
                    if (((ArrayList)completedResearch.get(this.player)).contains(researchItem.key)) {
                        if (ThaumcraftApi.getWarp(researchItem.key) > 0)
                            drawForbidden((var42 + 11), (var41 + 11));
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
                    } else {
                        if (!((ArrayList)completedResearch.get(this.player)).contains("@" + researchItem.key))
                            if (researchItem.isLost() || (
                                    researchItem.isHidden() && !((ArrayList)completedResearch.get(this.player)).contains("@" + researchItem.key)) || (
                                    researchItem.isConcealed() && !canUnlockResearch(researchItem)))
                                continue;
                        if (ThaumcraftApi.getWarp(researchItem.key) > 0)
                            drawForbidden((var42 + 11), (var41 + 11));
                        if (canUnlockResearch(researchItem)) {
                            float var38 = (float)Math.sin((Minecraft.getSystemTime() % 600L) / 600.0D * Math.PI * 2.0D) * 0.25F + 0.75F;
                            GL11.glColor4f(var38, var38, var38, 1.0F);
                        } else {
                            float var38 = 0.3F;
                            GL11.glColor4f(var38, var38, var38, 1.0F);
                        }
                    }
                    UtilsFX.bindTexture("textures/gui/gui_research.png");
                    GL11.glEnable(2884);
                    GL11.glEnable(3042);
                    GL11.glBlendFunc(770, 771);
                    if (researchItem.isRound()) {
                        drawTexturedModalRect(var42 - 2, var41 - 2, 54, 230, 26, 26);
                    } else if (researchItem.isHidden()) {
                        if (Config.researchDifficulty == -1 || (Config.researchDifficulty == 0 && researchItem.isSecondary())) {
                            drawTexturedModalRect(var42 - 2, var41 - 2, 230, 230, 26, 26);
                        } else {
                            drawTexturedModalRect(var42 - 2, var41 - 2, 86, 230, 26, 26);
                        }
                    } else if (Config.researchDifficulty == -1 || (Config.researchDifficulty == 0 && researchItem.isSecondary())) {
                        drawTexturedModalRect(var42 - 2, var41 - 2, 110, 230, 26, 26);
                    } else {
                        drawTexturedModalRect(var42 - 2, var41 - 2, 0, 230, 26, 26);
                    }
                    if (researchItem.isSpecial())
                        drawTexturedModalRect(var42 - 2, var41 - 2, 26, 230, 26, 26);
                    if (!canUnlockResearch(researchItem)) {
                        float var40 = 0.1F;
                        GL11.glColor4f(var40, var40, var40, 1.0F);
                        itemRenderer.renderWithColor = false;
                    }
                    GL11.glDisable(3042);
                    if (highlightedItem.contains(researchItem.key)) {
                        GL11.glPushMatrix();
                        GL11.glEnable(3042);
                        GL11.glBlendFunc(770, 771);
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        this.mc.renderEngine.bindTexture(ParticleEngine.particleTexture);
                        int px = (int)(t % 16L) * 16;
                        GL11.glTranslatef((var42 - 5), (var41 - 5), 0.0F);
                        UtilsFX.drawTexturedQuad(0, 0, px, 80, 16, 16, 0.0D);
                        GL11.glDisable(3042);
                        GL11.glPopMatrix();
                    }
                    if (researchItem.icon_item != null) {
                        GL11.glPushMatrix();
                        GL11.glEnable(3042);
                        GL11.glBlendFunc(770, 771);
                        RenderHelper.enableGUIStandardItemLighting();
                        GL11.glDisable(2896);
                        GL11.glEnable(32826);
                        GL11.glEnable(2903);
                        GL11.glEnable(2896);
                        itemRenderer.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, InventoryUtils.cycleItemStack(researchItem.icon_item), var42 + 3, var41 + 3);
                        GL11.glDisable(2896);
                        GL11.glDepthMask(true);
                        GL11.glEnable(2929);
                        GL11.glDisable(3042);
                        GL11.glPopMatrix();
                    } else if (researchItem.icon_resource != null) {
                        GL11.glPushMatrix();
                        GL11.glEnable(3042);
                        GL11.glBlendFunc(770, 771);
                        this.mc.renderEngine.bindTexture(researchItem.icon_resource);
                        if (!itemRenderer.renderWithColor)
                            GL11.glColor4f(0.2F, 0.2F, 0.2F, 1.0F);
                        UtilsFX.drawTexturedQuadFull(var42 + 3, var41 + 3, this.zLevel);
                        GL11.glPopMatrix();
                    }
                    if (!canUnlockResearch(researchItem))
                        itemRenderer.renderWithColor = true;
                    if (mx >= var10 && my >= var11 && mx < var10 + this.width - 26 && my < var11 + this.height && mx >= var42 && mx <= var42 + 22 && my >= var41 && my <= var41 + 22)
                        this.currentHighlight = researchItem;
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                }
                continue;
            }
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Collection cats = ResearchCategories.researchCategories.keySet();
        int count = 0;
        boolean swop = false;
        for (Object obj : cats) {
            ResearchCategoryList rcl = ResearchCategories.getResearchList((String)obj);
            if (((String)obj).equals("ELDRITCH") && !ResearchManager.isResearchComplete(this.player, "ELDRITCHMINOR"))
                continue;
            GL11.glPushMatrix();
            if (count == 11) {
                count = 0;
                swop = true;
            }
            int s0 = !swop ? 0 : (this.width-44);
            int s1 = 0;
            int s2 = swop ? 14 : 0;
            if (!selectedCategory.equals(obj)) {
                s1 = 26;
                s2 = swop ? 6 : 8;
            }
            UtilsFX.bindTexture("textures/gui/gui_research.png");
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if (swop) {
                drawTexturedModalRectReversed(var8 + s0 - 8, var9 + count * 24, 176 + s1, 232, 24, 24);
            } else {
                drawTexturedModalRect(var8 - 24 + s0, var9 + count * 24, 152 + s1, 232, 24, 24);
            }
            if (highlightedItem.contains(obj)) {
                GL11.glPushMatrix();
                this.mc.renderEngine.bindTexture(ParticleEngine.particleTexture);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                int px = (int)(16L * t % 16L);
                UtilsFX.drawTexturedQuad(var8 - 27 + s2 + s0, var9 - 4 + count * 24, px, 80, 16, 16, -90.0D);
                GL11.glPopMatrix();
            }
            GL11.glPushMatrix();
            this.mc.renderEngine.bindTexture(rcl.icon);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            UtilsFX.drawTexturedQuadFull(var8 - 19 + s2 + s0, var9 + 4 + count * 24, -80.0D);
            GL11.glPopMatrix();
            if (!selectedCategory.equals(obj)) {
                UtilsFX.bindTexture("textures/gui/gui_research.png");
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                if (swop) {
                    drawTexturedModalRectReversed(var8 + s0 - 8, var9 + count * 24, 224, 232, 24, 24);
                } else {
                    drawTexturedModalRect(var8 - 24 + s0, var9 + count * 24, 200, 232, 24, 24);
                }
            }
            GL11.glPopMatrix();
            count++;
        }
        UtilsFX.bindTexture("textures/gui/gui_research.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        net.minecraft.client.gui.Gui.func_152125_a(
                var8, var9,
                0,0, 1024, 640, this.width-52,this.height,
                1024,1024);
        GL11.glPopMatrix();
        this.zLevel = 0.0F;
        GL11.glDepthFunc(515);
        GL11.glDisable(2929);
        GL11.glEnable(3553);
        super.drawScreen(mx, my, partialTicks);
        if (completedResearch.get(this.player) != null &&
                this.currentHighlight != null) {
            String var34 = this.currentHighlight.getName();
            int var26 = mx + 6;
            int var27 = my - 4;
            int var99 = 0;
            FontRenderer fr = this.fontRendererObj;
            if (!((ArrayList)completedResearch.get(this.player)).contains(this.currentHighlight.key) && !canUnlockResearch(this.currentHighlight))
                fr = this.galFontRenderer;
            if (canUnlockResearch(this.currentHighlight)) {
                boolean secondary = (!((ArrayList)completedResearch.get(this.player)).contains(this.currentHighlight.key) && this.currentHighlight.tags != null && this.currentHighlight.tags.size() > 0 && (Config.researchDifficulty == -1 || (Config.researchDifficulty == 0 && this.currentHighlight.isSecondary())));
                boolean primary = (!secondary && !((ArrayList)completedResearch.get(this.player)).contains(this.currentHighlight.key));
                int var42 = (int)Math.max(fr.getStringWidth(var34), fr.getStringWidth(this.currentHighlight.getText()) / 1.9F * 1.5F);
                int var41 = fr.splitStringWidth(var34, var42) + 5;
                if (primary) {
                    var99 += 9;
                    var42 = (int)Math.max(var42, fr.getStringWidth(StatCollector.translateToLocal("tc.research.shortprim")) / 1.9F * 1.5F);
                }
                if (secondary) {
                    var99 += 29;
                    var42 = (int)Math.max(var42, fr.getStringWidth(StatCollector.translateToLocal("tc.research.short")) / 1.9F * 1.5F);
                }
                int warp = ThaumcraftApi.getWarp(this.currentHighlight.key);
                if (warp > 5)
                    warp = 5;
                String ws = StatCollector.translateToLocal("tc.forbidden");
                String wr = StatCollector.translateToLocal("tc.forbidden.level." + warp);
                String wte = ws.replaceAll("%n", wr);
                if (ThaumcraftApi.getWarp(this.currentHighlight.key) > 0) {
                    var99 += 9;
                    var42 = (int)Math.max(var42, fr.getStringWidth(wte) / 1.9F * 1.5F);
                }
                drawGradientRect(var26 - 3, var27 - 3, var26 + var42 + 3, var27 + var41 + 6 + var99, -1073741824, -1073741824);
                GL11.glPushMatrix();
                GL11.glTranslatef(var26, (var27 + var41 - 1), 0.0F);
                GL11.glScalef(0.75F, 0.75F, 0.75F);
                this.fontRendererObj.drawStringWithShadow(this.currentHighlight.getText(), 0, 0, -7302913);
                GL11.glPopMatrix();
                if (warp > 0) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(var26, (var27 + var41 + 8), 0.0F);
                    GL11.glScalef(0.75F, 0.75F, 0.75F);
                    this.fontRendererObj.drawStringWithShadow(wte, 0, 0, 16777215);
                    GL11.glPopMatrix();
                    var41 += 9;
                }
                GL11.glPushMatrix();
                if (primary) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(var26, (var27 + var41 + 8), 0.0F);
                    GL11.glScalef(0.75F, 0.75F, 0.75F);
                    if (ResearchManager.getResearchSlot((EntityPlayer)this.mc.thePlayer, this.currentHighlight.key) >= 0) {
                        this.fontRendererObj.drawStringWithShadow(StatCollector.translateToLocal("tc.research.hasnote"), 0, 0, 16753920);
                    } else if (this.hasScribeStuff) {
                        this.fontRendererObj.drawStringWithShadow(StatCollector.translateToLocal("tc.research.getprim"), 0, 0, 8900331);
                    } else {
                        this.fontRendererObj.drawStringWithShadow(StatCollector.translateToLocal("tc.research.shortprim"), 0, 0, 14423100);
                    }
                    GL11.glPopMatrix();
                } else if (secondary) {
                    boolean enough = true;
                    int cc = 0;
                    for (Aspect a : this.currentHighlight.tags.getAspectsSortedAmount()) {
                        if (Thaumcraft.proxy.playerKnowledge.hasDiscoveredAspect(this.player, a)) {
                            float alpha = 1.0F;
                            if (Thaumcraft.proxy.playerKnowledge.getAspectPoolFor(this.player, a) < this.currentHighlight.tags.getAmount(a)) {
                                alpha = (float)Math.sin((Minecraft.getSystemTime() % 600L) / 600.0D * Math.PI * 2.0D) * 0.25F + 0.75F;
                                enough = false;
                            }
                            GL11.glPushMatrix();
                            GL11.glPushAttrib(1048575);
                            UtilsFX.drawTag(var26 + cc * 16, var27 + var41 + 8, a, this.currentHighlight.tags.getAmount(a), 0, 0.0D, 771, alpha, false);
                            GL11.glPopAttrib();
                            GL11.glPopMatrix();
                        } else {
                            enough = false;
                            GL11.glPushMatrix();
                            UtilsFX.bindTexture("thaumcraft","textures/aspects/_unknown.png");
                            GL11.glColor4f(0.5F, 0.5F, 0.5F, 0.5F);
                            GL11.glTranslated((var26 + cc * 16), (var27 + var41 + 8), 0.0D);
                            UtilsFX.drawTexturedQuadFull(0, 0, 0.0D);
                            GL11.glPopMatrix();
                        }
                        cc++;
                    }
                    GL11.glPushMatrix();
                    GL11.glTranslatef(var26, (var27 + var41 + 27), 0.0F);
                    GL11.glScalef(0.75F, 0.75F, 0.75F);
                    if (enough) {
                        this.fontRendererObj.drawStringWithShadow(StatCollector.translateToLocal("tc.research.purchase"), 0, 0, 8900331);
                    } else {
                        this.fontRendererObj.drawStringWithShadow(StatCollector.translateToLocal("tc.research.short"), 0, 0, 14423100);
                    }
                    GL11.glPopMatrix();
                }
                GL11.glPopMatrix();
            } else {
                GL11.glPushMatrix();
                int var42 = (int)Math.max(fr.getStringWidth(var34), fr.getStringWidth(StatCollector.translateToLocal("tc.researchmissing")) / 1.5F);
                String var39 = StatCollector.translateToLocal("tc.researchmissing");
                int var30 = fr.splitStringWidth(var39, var42 * 2);
                drawGradientRect(var26 - 3, var27 - 3, var26 + var42 + 3, var27 + var30 + 10, -1073741824, -1073741824);
                GL11.glTranslatef(var26, (var27 + 12), 0.0F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                this.fontRendererObj.drawSplitString(var39, 0, 0, var42 * 2, -9416624);
                GL11.glPopMatrix();
            }
            fr.drawStringWithShadow(var34, var26, var27, canUnlockResearch(this.currentHighlight) ? (this.currentHighlight.isSpecial() ? -128 : -1) : (this.currentHighlight.isSpecial() ? -8355776 : -8355712));
        }
        GL11.glEnable(2929);
        GL11.glEnable(2896);
        RenderHelper.disableStandardItemLighting();
    }

    protected void mouseClicked(int par1, int par2, int par3) {
        this.popupTime = System.currentTimeMillis() - 1L;
        if (this.currentHighlight != null && !((ArrayList)completedResearch.get(this.player)).contains(this.currentHighlight.key) && canUnlockResearch(this.currentHighlight)) {
            updateResearch();
            boolean secondary = (this.currentHighlight.tags != null && this.currentHighlight.tags.size() > 0 && (Config.researchDifficulty == -1 || (Config.researchDifficulty == 0 && this.currentHighlight.isSecondary())));
            if (secondary) {
                boolean enough = true;
                for (Aspect a : this.currentHighlight.tags.getAspects()) {
                    if (Thaumcraft.proxy.playerKnowledge.getAspectPoolFor(this.player, a) < this.currentHighlight.tags.getAmount(a)) {
                        enough = false;
                        break;
                    }
                }
                if (enough)
                    PacketHandler.INSTANCE.sendToServer((IMessage)new PacketPlayerCompleteToServer(this.currentHighlight.key, this.mc.thePlayer.getCommandSenderName(), this.mc.thePlayer.worldObj.provider.dimensionId, (byte)0));
            } else if (this.hasScribeStuff && ResearchManager.getResearchSlot((EntityPlayer)this.mc.thePlayer, this.currentHighlight.key) == -1) {
                PacketHandler.INSTANCE.sendToServer((IMessage)new PacketPlayerCompleteToServer(this.currentHighlight.key, this.mc.thePlayer.getCommandSenderName(), this.mc.thePlayer.worldObj.provider.dimensionId, (byte)1));
                this.popupTime = System.currentTimeMillis() + 3000L;
                this.popupMessage = (new ChatComponentTranslation(StatCollector.translateToLocal("tc.research.popup"), new Object[] { "" + this.currentHighlight.getName() })).getUnformattedText();
            }
        } else if (this.currentHighlight != null && ((ArrayList)completedResearch.get(this.player)).contains(this.currentHighlight.key)) {
            this.mc.displayGuiScreen(new GuiEnhancedResearchRecipe(this.currentHighlight, 0, this.guiMapX, this.guiMapY));
        } else {
            Collection cats = ResearchCategories.researchCategories.keySet();
            int count = 0;
            boolean swop = false;
            for (Object obj : cats) {
                ResearchCategoryList rcl = ResearchCategories.getResearchList((String)obj);
                if (((String)obj).equals("ELDRITCH") && !ResearchManager.isResearchComplete(this.player, "ELDRITCHMINOR"))
                    continue;
                if (count == 11) {
                    count = 0;
                    swop = true;
                }
                int mposx = par1 - (swop ? this.width-26 : 0);
                int mposy = par2 - count * 24;
                if (mposx >= 0 && mposx < 26 && mposy >= 0 && mposy < 24) {
                    selectedCategory = (String)obj;
                    updateResearch();
                    playButtonClick();
                    break;
                }
                count++;
            }
        }
        super.mouseClicked(par1, par2, par3);
    }
    public void drawTexturedModalRectReversed(int par1, int par2, int par3, int par4, int par5, int par6) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((par1 + 0), (par2 + par6), this.zLevel, ((par3 + 0) * f), ((par4 + par6) * f1));
        tessellator.addVertexWithUV((par1 + par5), (par2 + par6), this.zLevel, ((par3 - par5) * f), ((par4 + par6) * f1));
        tessellator.addVertexWithUV((par1 + par5), (par2 + 0), this.zLevel, ((par3 - par5) * f), ((par4 + 0) * f1));
        tessellator.addVertexWithUV((par1 + 0), (par2 + 0), this.zLevel, ((par3 + 0) * f), ((par4 + 0) * f1));
        tessellator.draw();
    }
    private void playButtonClick() {
        this.mc.renderViewEntity.worldObj.playSound(this.mc.renderViewEntity.posX, this.mc.renderViewEntity.posY, this.mc.renderViewEntity.posZ, "thaumcraft:cameraclack", 0.4F, 1.0F, false);
    }

    private boolean canUnlockResearch(ResearchItem res) {
        if (res.parents != null && res.parents.length > 0)
            for (String pt : res.parents) {
                ResearchItem parent = ResearchCategories.getResearch(pt);
                if (parent != null && !((ArrayList)completedResearch.get(this.player)).contains(parent.key))
                    return false;
            }
        if (res.parentsHidden != null && res.parentsHidden.length > 0)
            for (String pt : res.parentsHidden) {
                ResearchItem parent = ResearchCategories.getResearch(pt);
                if (parent != null && !((ArrayList)completedResearch.get(this.player)).contains(parent.key))
                    return false;
            }
        return true;
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    private void drawLine(int x, int y, int x2, int y2, float r, float g, float b, float te, boolean wiggle) {
        if(this.drawline != null) {
            try {
                this.drawline.invoke(this.guiResearchBrowserInstance, x,y,x2,y2,r,g,b,te,wiggle);
            } catch (Exception e) {}
        } else {
            float count = (FMLClientHandler.instance().getClient()).thePlayer.ticksExisted + te;
            Tessellator var12 = Tessellator.instance;
            GL11.glPushMatrix();
            GL11.glAlphaFunc(516, 0.003921569F);
            GL11.glDisable(3553);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            double d3 = (x - x2);
            double d4 = (y - y2);
            float dist = MathHelper.sqrt_double(d3 * d3 + d4 * d4);
            int inc = (int) (dist / 2.0F);
            float dx = (float) (d3 / inc);
            float dy = (float) (d4 / inc);
            if (Math.abs(d3) > Math.abs(d4)) {
                dx *= 2.0F;
            } else {
                dy *= 2.0F;
            }
            GL11.glLineWidth(3.0F);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            var12.startDrawing(3);
            for (int a = 0; a <= inc; a++) {
                float r2 = r;
                float g2 = g;
                float b2 = b;
                float mx = 0.0F;
                float my = 0.0F;
                float op = 0.6F;
                if (wiggle) {
                    float phase = a / inc;
                    mx = MathHelper.sin((count + a) / 7.0F) * 5.0F * (1.0F - phase);
                    my = MathHelper.sin((count + a) / 5.0F) * 5.0F * (1.0F - phase);
                    r2 *= 1.0F - phase;
                    g2 *= 1.0F - phase;
                    b2 *= 1.0F - phase;
                    op *= phase;
                }
                var12.setColorRGBA_F(r2, g2, b2, op);
                var12.addVertex((x - dx * a + mx), (y - dy * a + my), 0.0D);
                if (Math.abs(d3) > Math.abs(d4)) {
                    dx *= 1.0F - 1.0F / inc * 3.0F / 2.0F;
                } else {
                    dy *= 1.0F - 1.0F / inc * 3.0F / 2.0F;
                }
            }
            var12.draw();
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(2848);
            GL11.glDisable(3042);
            GL11.glDisable(32826);
            GL11.glEnable(3553);
            GL11.glAlphaFunc(516, 0.1F);
            GL11.glPopMatrix();
        }
    }

    private void drawForbidden(double x, double y) {
        int count = (FMLClientHandler.instance().getClient()).thePlayer.ticksExisted;
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        UtilsFX.bindTexture(TileNodeRenderer.nodetex);
        int frames = 32;
        int part = count % frames;
        GL11.glTranslated(x, y, 0.0D);
        UtilsFX.renderAnimatedQuadStrip(80.0F, 0.66F, frames, 5, frames - 1 - part, 0.0F, 4456533);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
}
