package com.malatindez.thaumicextensions.client.render.gui;


import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.malatindez.thaumicextensions.client.render.misc.GUI.GuiTextureMapping;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.EXTRescaleNormal;
import org.lwjgl.opengl.GL11;
import com.malatindez.thaumicextensions.client.lib.UtilsFX;
import org.lwjgl.util.vector.Vector2f;
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
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketPlayerCompleteToServer;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.InventoryUtils;

@SideOnly(Side.CLIENT)
public class GuiEnhancedResearchBrowser extends GuiScreen {
    protected final Vector2f mousePos = new Vector2f();
    protected final Vector2f guiOffset = new Vector2f();
    protected static final Vector2f lastPos = new Vector2f();
    static final Vector2f guiTopLeftCorner = new Vector2f();
    static final Vector2f guiBottomRightCorner  = new Vector2f();
    private int isMouseButtonDown = 0;

    private final LinkedList<ResearchItem> research = new LinkedList<ResearchItem>();

    public static final ResourceLocation researchBack =
            new ResourceLocation(ThaumicExtensions.MODID, "textures/gui/gui_researchBack.jpg");

    public static final ResourceLocation researchBackEldritch =
            new ResourceLocation(ThaumicExtensions.MODID, "textures/gui/gui_researchBackEldritch.jpg");

    private static String selectedCategory = null;

    private final FontRenderer galFontRenderer;

    private ResearchItem currentHighlight = null;

    private final String player;

    private static final GuiResearchBrowser guiResearchBrowserInstance = new GuiResearchBrowser();

    long popupTime = 0L;

    String popupMessage = "";

    public boolean hasScribeStuff;

    /*
    private static void initMap() {
        map = new GuiTextureMapping(new ResourceLocation(ThaumicExtensions.MODID, "textures/gui/gui_research.png"));
        Vector2f iconSize       = new Vector2f(26, 26);
        Vector2f nullVector     = new Vector2f(0, 0);
        Vector2f textureSize    = new Vector2f(256, 256);
        map.addElement("defaultResearch",
                new GuiTextureMapping.Part(nullVector, new Vector2f(0, 230), new Vector2f(26, 256),
                        textureSize, iconSize)
        );
        map.addElement("specialResearch",
                new GuiTextureMapping.Part(nullVector, new Vector2f(26, 230), new Vector2f(52, 256),
                        textureSize, iconSize)
        );
        map.addElement("roundResearch",
                new GuiTextureMapping.Part(nullVector, new Vector2f(54, 230), new Vector2f(80, 256),
                        textureSize, iconSize)
        );
        map.addElement("hiddenDefaultResearch",
                new GuiTextureMapping.Part(nullVector, new Vector2f(86, 230), new Vector2f(112, 256),
                        textureSize, iconSize)
        );
        map.addElement("secondaryResearch",
                new GuiTextureMapping.Part(nullVector, new Vector2f(110, 230), new Vector2f(136, 256),
                        textureSize, iconSize)
        );
        map.addElement("selectedCategory",
                new GuiTextureMapping.Part(nullVector, new Vector2f(152, 232), new Vector2f(176, 256),
                        textureSize, new Vector2f(24, 24))
        );
        map.addElement("unselectedCategory",
                new GuiTextureMapping.Part(nullVector, new Vector2f(176, 232), new Vector2f(200, 256),
                        textureSize, new Vector2f(24, 24))
        );
        map.addElement("unselectedCategoryShadow",
                new GuiTextureMapping.Part(nullVector, new Vector2f(200, 232), new Vector2f(224, 256),
                        textureSize, new Vector2f(24, 24))
        );
        map.addElement("hiddenResearch",
                new GuiTextureMapping.Part(nullVector, new Vector2f(230, 230), new Vector2f(256, 256),
                        textureSize, iconSize)
        );
    }*/
    public GuiEnhancedResearchBrowser() {
        this.hasScribeStuff = false;
        guiOffset.set(lastPos.x * 24 - 82, lastPos.y * 24 - 70);
        updateResearch();
        this.galFontRenderer = (FMLClientHandler.instance().getClient()).standardGalacticFontRenderer;
        this.player = (Minecraft.getMinecraft()).thePlayer.getCommandSenderName();
    }
    public GuiEnhancedResearchBrowser(double x, double y) {
        guiOffset.set(lastPos.x * 24 - 82, lastPos.y * 24 - 70);
        this.hasScribeStuff = false;
        updateResearch();
        this.galFontRenderer = (FMLClientHandler.instance().getClient()).standardGalacticFontRenderer;
        this.player = (Minecraft.getMinecraft()).thePlayer.getCommandSenderName();
    }
    public void updateResearch() {
        if (this.mc == null)
            this.mc = Minecraft.getMinecraft();
        this.research.clear();
        this.hasScribeStuff = false;
        if (selectedCategory == null) {
            Collection<String> cats = ResearchCategories.researchCategories.keySet();
            selectedCategory = cats.iterator().next();
        }
        Collection<ResearchItem> col = (ResearchCategories.getResearchList(selectedCategory)).research.values();
        for (Object res : col)
            this.research.add((ResearchItem)res);
        if (ResearchManager.consumeInkFromPlayer(this.mc.thePlayer, false)
                && InventoryUtils.isPlayerCarrying(this.mc.thePlayer, new ItemStack(Items.paper)) >= 0)
            this.hasScribeStuff = true;
        guiTopLeftCorner.set(
                ResearchCategories.getResearchList(selectedCategory).minDisplayColumn * 24 - 480,
                ResearchCategories.getResearchList(selectedCategory).minDisplayRow * 24 - 240
        );
        guiBottomRightCorner.set(
                ResearchCategories.getResearchList(selectedCategory).maxDisplayColumn * 24 - 112,
                ResearchCategories.getResearchList(selectedCategory).maxDisplayRow * 24 - 61
        );
    }

    public void onGuiClosed() {
        lastPos.set(
                (int)((this.guiOffset.x + 82) / 24),
                (int)((this.guiOffset.y + 70) / 24)
        );
        super.onGuiClosed();
    }

    public void initGui() {}

    protected void actionPerformed(GuiButton par1GuiButton) {
        super.actionPerformed(par1GuiButton);
    }

    protected void keyTyped(char par1, int par2) {
        if (par2 == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            GuiResearchBrowser.highlightedItem.clear();
            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
        } else {
            if (par2 == 1)
                GuiResearchBrowser.highlightedItem.clear();
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
                    this.guiOffset.set(
                            this.guiOffset.x - mx + this.mousePos.x,
                            this.guiOffset.y - my + this.mousePos.y
                    );
                    if (guiOffset.x < guiTopLeftCorner.x) {
                        guiOffset.x = guiTopLeftCorner.x;
                    }
                    else if (guiOffset.x >= guiBottomRightCorner.x) {
                        guiOffset.x = guiBottomRightCorner.x - 1;
                    }
                    if (guiOffset.y < guiTopLeftCorner.y) {
                        guiOffset.y = guiTopLeftCorner.y;
                    }
                    else if (guiOffset.y >= guiBottomRightCorner.y) {
                        guiOffset.y = guiBottomRightCorner.y - 1;
                    }
                }
                this.mousePos.set(mx,my);
            }
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
        Collection<String> cats = ResearchCategories.researchCategories.keySet();
        int count = 0;
        boolean swop = false;
        for (Object obj : cats) {
            if (count == 11) {
                count = 0;
                swop = true;
            }
            if (obj.equals("ELDRITCH") && !ResearchManager.isResearchComplete(this.player, "ELDRITCHMINOR")) {
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
    protected void drawBackground() {
        GL11.glDepthFunc(GL11.GL_GEQUAL);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 0.0F, -200.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(EXTRescaleNormal.GL_RESCALE_NORMAL_EXT);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        ResourceLocation tempResLocation = (ResearchCategories.getResearchList(selectedCategory)).background;
        if(tempResLocation.getResourceDomain().equals("thaumcraft")) {
            if(tempResLocation.getResourcePath().equals("textures/gui/gui_researchback.png")) {
                tempResLocation = researchBack;
            } else if(tempResLocation.getResourcePath().equals("textures/gui/gui_researchbackeldritch.png")) {
                tempResLocation = researchBackEldritch;
            }
        } else {
            tempResLocation = researchBack;
        }
        (Minecraft.getMinecraft()).renderEngine.bindTexture(tempResLocation);
        Gui.func_152125_a(
                26, 0,
                0,0, 256, 256, this.width-52,this.height,
                256,256);
        GL11.glPopMatrix();
    }

    private void drawLine(Vector2f from, Vector2f to, float r, float g, float b, float te, boolean wiggle) {
        float count = (FMLClientHandler.instance().getClient()).thePlayer.ticksExisted + te;
        Tessellator var12 = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glAlphaFunc(516, 0.003921569F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        double d3 = (from.x - to.x);
        double d4 = (from.y - to.y);
        float dist = MathHelper.sqrt_double(d3 * d3 + d4 * d4);
        int inc = (int) (dist / 2.0F);
        float dx = (float) (d3 / inc);
        float dy = (float) (d4 / inc);
        boolean idk = Math.abs(d3) > Math.abs(d4);
        if (idk) {
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
                float phase = ((float) a) / inc;
                mx = MathHelper.sin((count + a) / 7.0F) * 5.0F * (1.0F - phase);
                my = MathHelper.sin((count + a) / 5.0F) * 5.0F * (1.0F - phase);
                r2 *= 1.0F - phase;
                g2 *= 1.0F - phase;
                b2 *= 1.0F - phase;
                op *= phase;
            }
            var12.setColorRGBA_F(r2, g2, b2, op);
            var12.addVertex((from.x - dx * a + mx), (from.y - dy * a + my), 0.0D);
            if (idk) {
                dx *= 1.0F - 1.0F / inc * 3.0F / 2.0F;
            } else {
                dy *= 1.0F - 1.0F / inc * 3.0F / 2.0F;
            }
        }
        var12.draw();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(2848);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(EXTRescaleNormal.GL_RESCALE_NORMAL_EXT);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glAlphaFunc(516, 0.1F);
        GL11.glPopMatrix();
    }

    protected void drawLines(float partialTicks) {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        if (GuiResearchBrowser.completedResearch.get(this.player) != null)
            for (ResearchItem researchItem : this.research) {
                Vector2f from =  new Vector2f(
                        researchItem.displayColumn * 24 - guiOffset.x + 11 + 42,
                        researchItem.displayRow * 24 - guiOffset.y + 11 + 17);
                if (researchItem.parents != null && researchItem.parents.length > 0)
                    for (int j = 0; j < researchItem.parents.length; j++) {
                        if (researchItem.parents[j] != null && (ResearchCategories.getResearch(researchItem.parents[j])).category.equals(selectedCategory)) {
                            ResearchItem parent = ResearchCategories.getResearch(researchItem.parents[j]);
                            if (!parent.isVirtual()) {
                                Vector2f to = new Vector2f(
                                        parent.displayColumn * 24 - guiOffset.x + 11 + 42,
                                        parent.displayRow * 24 - guiOffset.y + 11 + 17);
                                boolean researched = GuiResearchBrowser.completedResearch.get(this.player).contains(researchItem.key);
                                boolean var29 = GuiResearchBrowser.completedResearch.get(this.player).contains(parent.key);
                                if (researched) {
                                    drawLine(from, to, 0.1F, 0.1F, 0.1F, partialTicks, false);
                                } else if (!researchItem.isLost() && ((
                                        !researchItem.isHidden() && !researchItem.isLost()) || GuiResearchBrowser.completedResearch.get(this.player).contains("@" + researchItem.key)) && (
                                        !researchItem.isConcealed() || canUnlockResearch(researchItem))) {
                                    if (var29) {
                                        drawLine(from, to, 0.0F, 1.0F, 0.0F, partialTicks, true);
                                    } else if (((!parent.isHidden() && !researchItem.isLost()) || GuiResearchBrowser.completedResearch.get(this.player).contains("@" + parent.key)) && (!parent.isConcealed() || canUnlockResearch(parent))) {
                                        drawLine(from, to, 0.0F, 0.0F, 1.0F, partialTicks, true);
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
                                    sibling.parents == null || !Arrays.asList(sibling.parents).contains(researchItem.key))) {
                                Vector2f to = new Vector2f(
                                        sibling.displayColumn * 24 - guiOffset.x + 11 + 42,
                                        sibling.displayRow * 24 - guiOffset.y + 11 + 17);
                                boolean var28 = GuiResearchBrowser.completedResearch.get(this.player).contains(researchItem.key);
                                boolean var29 = GuiResearchBrowser.completedResearch.get(this.player).contains(sibling.key);
                                if (var28) {
                                    drawLine(from, to, 0.1F, 0.1F, 0.2F, partialTicks, false);
                                } else if (!researchItem.isLost() && (
                                        !researchItem.isHidden() || GuiResearchBrowser.completedResearch.get(this.player).contains("@" + researchItem.key)) && (
                                        !researchItem.isConcealed() || canUnlockResearch(researchItem))) {
                                    if (var29) {
                                        drawLine(from, to, 0.0F, 1.0F, 0.0F, partialTicks, true);
                                    } else if ((!sibling.isHidden() || GuiResearchBrowser.completedResearch.get(this.player).contains("@" + sibling.key)) && (!sibling.isConcealed() || canUnlockResearch(sibling))) {
                                        drawLine(from, to, 0.0F, 0.0F, 1.0F, partialTicks, true);
                                    }
                                }
                            }
                        }
                    }
            }
    }
    protected void drawResearches(int mx, int my, double t) {
        GL11.glEnable(EXTRescaleNormal.GL_RESCALE_NORMAL_EXT);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        this.currentHighlight = null;
        RenderItem itemRenderer = new RenderItem();
        if (GuiResearchBrowser.completedResearch.get(this.player) != null)
            for (ResearchItem researchItem : this.research) {
                int var26 = (int)(researchItem.displayColumn * 24 - guiOffset.x);
                int var27 = (int)(researchItem.displayRow * 24 - guiOffset.y);
                if (!researchItem.isVirtual() && var26 >= -24 && var27 >= -24 && var26 <= this.width - 26 && var27 <= this.height) {
                    int var42 = 42 + var26;
                    int var41 = 17 + var27;
                    if (GuiResearchBrowser.completedResearch.get(this.player).contains(researchItem.key)) {
                        if (ThaumcraftApi.getWarp(researchItem.key) > 0)
                            drawForbidden((var42 + 11), (var41 + 11));
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0F);
                    } else {
                        if (!GuiResearchBrowser.completedResearch.get(this.player).contains("@" + researchItem.key))
                            if (researchItem.isLost() || (
                                    researchItem.isHidden() && !GuiResearchBrowser.completedResearch.get(this.player).contains("@" + researchItem.key)) || (
                                    researchItem.isConcealed() && !canUnlockResearch(researchItem)))
                                continue;
                        if (ThaumcraftApi.getWarp(researchItem.key) > 0)
                            drawForbidden((var42 + 11), (var41 + 11));
                        if (canUnlockResearch(researchItem)) {
                            float var38 = (float) Math.sin((Minecraft.getSystemTime() % 600L) / 600.0D * Math.PI * 2.0D) * 0.25F + 0.75F;
                            GL11.glColor4f(var38, var38, var38, 1.0F);
                        } else {
                            float var38 = 0.3F;
                            GL11.glColor4f(var38, var38, var38, 1.0F);
                        }
                    }
                    UtilsFX.bindTexture("textures/gui/gui_research.png");
                    GL11.glEnable(GL11.GL_CULL_FACE);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
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
                    GL11.glDisable(GL11.GL_BLEND);
                    if (GuiResearchBrowser.highlightedItem.contains(researchItem.key)) {
                        GL11.glPushMatrix();
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                        this.mc.renderEngine.bindTexture(ParticleEngine.particleTexture);
                        int px = (int) (t % 16L) * 16;
                        GL11.glTranslatef((var42 - 5), (var41 - 5), 0.0F);
                        UtilsFX.drawTexturedQuad(0, 0, px, 80, 16, 16, 0.0D);
                        GL11.glDisable(GL11.GL_BLEND);
                        GL11.glPopMatrix();
                    }
                    if (researchItem.icon_item != null) {
                        GL11.glPushMatrix();
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        RenderHelper.enableGUIStandardItemLighting();
                        GL11.glDisable(GL11.GL_LIGHTING);
                        GL11.glEnable(EXTRescaleNormal.GL_RESCALE_NORMAL_EXT);
                        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
                        GL11.glEnable(GL11.GL_LIGHTING);
                        itemRenderer.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.renderEngine, InventoryUtils.cycleItemStack(researchItem.icon_item), var42 + 3, var41 + 3);
                        GL11.glDisable(GL11.GL_LIGHTING);
                        GL11.glDepthMask(true);
                        GL11.glEnable(GL11.GL_DEPTH_TEST);
                        GL11.glDisable(GL11.GL_BLEND);
                        GL11.glPopMatrix();
                    } else if (researchItem.icon_resource != null) {
                        GL11.glPushMatrix();
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        this.mc.renderEngine.bindTexture(researchItem.icon_resource);
                        if (!itemRenderer.renderWithColor)
                            GL11.glColor4f(0.2F, 0.2F, 0.2F, 1.0F);
                        UtilsFX.drawTexturedQuadFull(var42 + 3, var41 + 3, this.zLevel);
                        GL11.glPopMatrix();
                    }
                    if (!canUnlockResearch(researchItem))
                        itemRenderer.renderWithColor = true;
                    if (mx >= 42 && my >= 17 && mx < 42 + this.width - 26 && my < 17 + this.height && mx >= var42 && mx <= var42 + 22 && my >= var41 && my <= var41 + 22)
                        this.currentHighlight = researchItem;
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
    }
    protected void drawCategories(double t) {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Collection<String> cats = ResearchCategories.researchCategories.keySet();
        int count = 0;
        boolean swop = false;
        for (Object obj : cats) {
            ResearchCategoryList rcl = ResearchCategories.getResearchList((String)obj);
            if (obj.equals("ELDRITCH") && !ResearchManager.isResearchComplete(this.player, "ELDRITCHMINOR"))
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
                drawTexturedModalRectReversed(26 + s0 - 8, count * 24, 176 + s1, 232, 24, 24);
            } else {
                drawTexturedModalRect(26 - 24 + s0, count * 24, 152 + s1, 232, 24, 24);
            }
            if (GuiResearchBrowser.highlightedItem.contains(obj)) {
                GL11.glPushMatrix();
                this.mc.renderEngine.bindTexture(ParticleEngine.particleTexture);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                int px = (int)(16L * t % 16L);
                UtilsFX.drawTexturedQuad(26 - 27 + s2 + s0, -4 + count * 24, px, 80, 16, 16, -90.0D);
                GL11.glPopMatrix();
            }
            GL11.glPushMatrix();
            this.mc.renderEngine.bindTexture(rcl.icon);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            UtilsFX.drawTexturedQuadFull(26 - 19 + s2 + s0, 4 + count * 24, -80.0D);
            GL11.glPopMatrix();
            if (!selectedCategory.equals(obj)) {
                UtilsFX.bindTexture("textures/gui/gui_research.png");
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                if (swop) {
                    drawTexturedModalRectReversed(26 + s0 - 8, count * 24, 224, 232, 24, 24);
                } else {
                    drawTexturedModalRect(26 - 24 + s0, count * 24, 200, 232, 24, 24);
                }
            }
            GL11.glPopMatrix();
            count++;
        }
    }
    protected void drawFrame() {
        UtilsFX.bindTexture("textures/gui/gui_research.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.func_152125_a(
                26, 0,
                0,0, 1024, 640, this.width-52,this.height,
                1024,1024);
    }
    protected void drawHighlights(int mx, int my, float partialTicks) {
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        super.drawScreen(mx, my, partialTicks);
        if (GuiResearchBrowser.completedResearch.get(this.player) != null &&
                this.currentHighlight != null) {
            String var34 = this.currentHighlight.getName();
            int var26 = mx + 6;
            int var27 = my - 4;
            int var99 = 0;
            FontRenderer fr = this.fontRendererObj;
            if (!GuiResearchBrowser.completedResearch.get(this.player).contains(this.currentHighlight.key) && !canUnlockResearch(this.currentHighlight))
                fr = this.galFontRenderer;
            if (canUnlockResearch(this.currentHighlight)) {
                boolean secondary = (!GuiResearchBrowser.completedResearch.get(this.player).contains(this.currentHighlight.key) && this.currentHighlight.tags != null && this.currentHighlight.tags.size() > 0 && (Config.researchDifficulty == -1 || (Config.researchDifficulty == 0 && this.currentHighlight.isSecondary())));
                boolean primary = (!secondary && !GuiResearchBrowser.completedResearch.get(this.player).contains(this.currentHighlight.key));
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
                    if (ResearchManager.getResearchSlot(this.mc.thePlayer, this.currentHighlight.key) >= 0) {
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
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        RenderHelper.disableStandardItemLighting();
    }
    protected void genResearchBackground(int mx, int my, float partialTicks) {
        long t = System.nanoTime() / 50000000L;
        drawBackground(); // Leaves one more matrix on stack for drawFrame

        drawLines(partialTicks);

        drawResearches(mx, my, t);

        drawCategories(t);

        drawFrame();
        GL11.glPopMatrix();

        this.zLevel = 0.0F;
        drawHighlights(mx,my,partialTicks);
    }

    protected void mouseClicked(int par1, int par2, int par3) {
        this.popupTime = System.currentTimeMillis() - 1L;
        if (this.currentHighlight != null && !GuiResearchBrowser.completedResearch.get(this.player).contains(this.currentHighlight.key) && canUnlockResearch(this.currentHighlight)) {
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
                    PacketHandler.INSTANCE.sendToServer(new PacketPlayerCompleteToServer(this.currentHighlight.key, this.mc.thePlayer.getCommandSenderName(), this.mc.thePlayer.worldObj.provider.dimensionId, (byte)0));
            } else if (this.hasScribeStuff && ResearchManager.getResearchSlot(this.mc.thePlayer, this.currentHighlight.key) == -1) {
                PacketHandler.INSTANCE.sendToServer(new PacketPlayerCompleteToServer(this.currentHighlight.key, this.mc.thePlayer.getCommandSenderName(), this.mc.thePlayer.worldObj.provider.dimensionId, (byte)1));
                this.popupTime = System.currentTimeMillis() + 3000L;
                this.popupMessage = (new ChatComponentTranslation(StatCollector.translateToLocal("tc.research.popup"), "" + this.currentHighlight.getName())).getUnformattedText();
            }
        } else if (this.currentHighlight != null && GuiResearchBrowser.completedResearch.get(this.player).contains(this.currentHighlight.key)) {
            this.mc.displayGuiScreen(new GuiEnhancedResearchRecipe(this.currentHighlight, 0, this.guiOffset.x, this.guiOffset.y));
        } else {
            Collection<String> cats = ResearchCategories.researchCategories.keySet();
            int count = 0;
            boolean swop = false;
            for (Object obj : cats) {
                if (obj.equals("ELDRITCH") && !ResearchManager.isResearchComplete(this.player, "ELDRITCHMINOR"))
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
        tessellator.addVertexWithUV((par1), (par2 + par6), this.zLevel, ((par3) * f), ((par4 + par6) * f1));
        tessellator.addVertexWithUV((par1 + par5), (par2 + par6), this.zLevel, ((par3 - par5) * f), ((par4 + par6) * f1));
        tessellator.addVertexWithUV((par1 + par5), (par2), this.zLevel, ((par3 - par5) * f), ((par4) * f1));
        tessellator.addVertexWithUV((par1), (par2), this.zLevel, ((par3) * f), ((par4) * f1));
        tessellator.draw();
    }
    private void playButtonClick() {
        this.mc.renderViewEntity.worldObj.playSound(this.mc.renderViewEntity.posX, this.mc.renderViewEntity.posY, this.mc.renderViewEntity.posZ, "thaumcraft:cameraclack", 0.4F, 1.0F, false);
    }

    private boolean canUnlockResearch(ResearchItem res) {
        if (res.parents != null && res.parents.length > 0)
            for (String pt : res.parents) {
                ResearchItem parent = ResearchCategories.getResearch(pt);
                if (parent != null && !GuiResearchBrowser.completedResearch.get(this.player).contains(parent.key))
                    return false;
            }
        if (res.parentsHidden != null && res.parentsHidden.length > 0)
            for (String pt : res.parentsHidden) {
                ResearchItem parent = ResearchCategories.getResearch(pt);
                if (parent != null && !GuiResearchBrowser.completedResearch.get(this.player).contains(parent.key))
                    return false;
            }
        return true;
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    private void drawForbidden(double x, double y) {
        int count = (FMLClientHandler.instance().getClient()).thePlayer.ticksExisted;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        UtilsFX.bindTexture(TileNodeRenderer.nodetex);
        int frames = 32;
        int part = count % frames;
        GL11.glTranslated(x, y, 0.0D);
        UtilsFX.renderAnimatedQuadStrip(80.0F, 0.66F, frames, 5, frames - 1 - part, 0.0F, 4456533);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
