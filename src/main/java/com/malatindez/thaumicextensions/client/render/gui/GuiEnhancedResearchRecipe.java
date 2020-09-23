package com.malatindez.thaumicextensions.client.render.gui;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.malatindez.thaumicextensions.client.render.misc.GUI.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.client.gui.GuiResearchBrowser;

@SideOnly(Side.CLIENT)
public class GuiEnhancedResearchRecipe extends EnhancedGuiScreen {
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

    public void onGuiClosed() {
        super.onGuiClosed();
    }

    public void drawScreen(int mx, int my, float tick) {
        super.drawScreen(mx,my,tick);
    }
    protected final GuiTextureMapping map;
    final TextBox textBoxReference;
    public GuiEnhancedResearchRecipe(ResearchItem research, int page, double x, double y) {
        this.gui = new Collection(new Vector2f(0, 0), new Vector2f(DefaultGuiObject.defaultResolution
                ), 1);
        textBoxReference = (TextBox) this.gui.addObject(
                new TextBox(Minecraft.getMinecraft().fontRenderer,
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec vitae tincidunt nisl. In at enim at ipsum molestie vehicula vitae vitae metus. Curabitur nisl sem, lobortis nec erat in, consequat porttitor lacus. Curabitur vitae metus in elit egestas finibus vitae eget lacus. Curabitur sollicitudin, mauris sed molestie mollis, nisl felis malesuada justo, eu laoreet quam justo sit amet tortor. Proin finibus risus quis turpis volutpat iaculis. Morbi elementum maximus nulla, venenatis tincidunt eros semper quis. Aenean consectetur in neque mollis maximus. Suspendisse eleifend ipsum ac justo mattis, hendrerit scelerisque orci accumsan. In sollicitudin risus ac nisl porta, quis interdum libero convallis. Phasellus posuere ornare mauris nec congue. Nam accumsan porta odio, id molestie felis tempus eu. Phasellus vitae erat vel dolor ullamcorper porttitor. Curabitur tincidunt viverra purus, eget vulputate eros egestas vitae. Fusce varius blandit velit, ut lobortis elit tincidunt nec." +
                "Nulla dapibus cursus porttitor. Mauris vel bibendum massa. Suspendisse in accumsan ligula, tempor commodo elit. Mauris dapibus varius arcu ac finibus. Maecenas feugiat augue eu malesuada tristique. Maecenas sed neque rhoncus, ullamcorper risus ornare, dictum eros. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Aenean sit amet ligula laoreet, rutrum magna a, pulvinar nulla. Donec sodales ac ligula non condimentum. Aenean dictum aliquet viverra. Donec sodales nunc in mauris iaculis bibendum. Sed lacinia mi ornare convallis bibendum. Nunc faucibus neque a elit sagittis, ac pretium nisl viverra. Quisque rutrum ipsum a neque posuere, at tincidunt enim faucibus. Sed id bibendum eros.",
                0xffffff,
                true, false, false,
                new Vector2f(0,0),
                new Vector2f(1,1),
                new Vector2f(1,1),
                new Vector2f(200, 200), 0
                ));

        map = new GuiTextureMapping(new ResourceLocation(ThaumicExtensions.MODID, "textures/gui/gui_research.png"));
        Vector2f iconSize       = new Vector2f(26, 26);
        Vector2f nullVector     = new Vector2f(0, 0);
        Vector2f textureSize    = new Vector2f(256, 256);
        map.addElement("defaultResearch",
                new GuiTextureMapping.Icon(nullVector, new Vector2f(0, 230), iconSize,
                        textureSize, 10)
        );
        map.addElement("specialResearch",
                new GuiTextureMapping.Icon(nullVector, new Vector2f(26, 230), iconSize,
                        textureSize, 10)
        );
        map.addElement("roundResearch",
                new GuiTextureMapping.Icon(nullVector, new Vector2f(54, 230), iconSize,
                        textureSize, 10)
        );
        map.addElement("hiddenDefaultResearch",
                new GuiTextureMapping.Icon(nullVector, new Vector2f(86, 230), iconSize,
                        textureSize, 10)
        );
        map.addElement("secondaryResearch",
                new GuiTextureMapping.Icon(nullVector, new Vector2f(110, 230), iconSize,
                        textureSize, 10)
        );
        iconSize.set(24,24);
        map.addElement("selectedCategory",
                new GuiTextureMapping.Icon(nullVector, new Vector2f(152, 232), iconSize,
                        textureSize, 10)
        );
        map.addElement("unselectedCategory",
                new GuiTextureMapping.Icon(nullVector, new Vector2f(176, 232), iconSize,
                        textureSize, 10)
        );
        map.addElement("unselectedCategoryShadow",
                new GuiTextureMapping.Icon(nullVector, new Vector2f(200, 232), iconSize,
                        textureSize, 10)
        );
        iconSize.set(26,26);
        map.addElement("hiddenResearch",
                new GuiTextureMapping.Icon(nullVector, new Vector2f(230, 230), iconSize,
                        textureSize, 10)
        );
        try {
            Class[] a = {Object.class, int.class};
            this.gui.addObject(
                    new Button(
                            map.getGuiElement("specialResearch"),
                            new Vector2f(100, 100),
                            this, this.getClass().getDeclaredMethod("defaultResearchClicked", a),
                            0, 1, null
                    )
            );
        } catch (Exception ignored) {
            System.out.println(ignored.toString());
            ignored.printStackTrace();
        }
        this.gui.addObject(new Rect(
                new Vector2f(0,0),
                new Vector2f(1,1),
                new Vector2f(200, 200),
                -1, new Rect.VertexColors(
                        new Vector4f(0,255,255,255),
                        new Vector4f(255,0,0,128),
                        new Vector4f(0,255,0,128),
                        new Vector4f(0,0,255,0)
        )
        ));
    }
    public void defaultResearchClicked(Object obj, int id) {
        this.textBoxReference.color ^= 0xff0000;
    }
}
