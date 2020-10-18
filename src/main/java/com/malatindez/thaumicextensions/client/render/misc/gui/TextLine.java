package com.malatindez.thaumicextensions.client.render.misc.gui;

import com.malatindez.thaumicextensions.client.lib.UtilsFX;
import com.malatindez.thaumicextensions.client.render.gui.GuiEditor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.ResourceLocation;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

@SideOnly(Side.CLIENT)
public class TextLine extends DefaultGuiObject implements GuiEditor.Editable {
    protected FontRenderer fontRendererObj;
    protected String textLine;
    protected int renderCursor = 0;
    public Vector4f color;
    protected Vector2f textScale;
    public boolean dropShadow;
    protected boolean cutLine = true;
    Minecraft mc;

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject generateJSONObject() {
        JSONObject returnValue = super.generateJSONObject();
        JSONObject a = (JSONObject) returnValue.get(getName());
        a.put("text", this.textLine);
        a.put("color",VecToJson(color));
        a.put("textScale",VecToJson(textScale));
        a.put("dropShadow",dropShadow);
        return returnValue;
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getFullJSON() {
        JSONObject returnValue = super.getFullDefaultJSON();
        JSONObject a = (JSONObject) returnValue.get(getName());
        a.put("text", this.textLine);
        a.put("color",VecToJson(color));
        a.put("textScale",VecToJson(textScale));
        a.put("dropShadow",dropShadow);
        return returnValue;
    }

    private static final ResourceLocation templateLocation = new ResourceLocation("thaumicextensions", "gui/templates/text_box_template.json");
    @Override
    public JSONObject getTemplateJSON() {
        String s = UtilsFX.loadFromFile(templateLocation);
        return (JSONObject) JSONValue.parse(s);
    }


    @Override
    public void loadFromJSONObject(JSONObject parameters) {
        super.loadFromJSONObject(parameters);
        fontRendererObj = Minecraft.getMinecraft().fontRenderer;
        textLine = (String)parameters.get("text");
        if(parameters.containsKey("color")) {
            color = Json4Vec(parameters.get("color"));
        } else {
            color = new Vector4f(1,1,1,1);
        }
        if(parameters.containsKey("textScale")) {
            textScale = Json2Vec(parameters.get("textScale"));
        } else {
            textScale = new Vector2f(1,1);
        }
        if(parameters.containsKey("dropShadow")) {
            dropShadow = (Boolean)parameters.get("dropShadow");
        } else {
            dropShadow = false;
        }
        mc = Minecraft.getMinecraft();
    }

    public TextLine(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }


    public void setTextLine(String line) {
        this.textLine = line;
        textLineWasUpdated();
    }
    public String getTextLine() {
        return textLine;
    }
    public void setRenderCursor(int renderCursor) {
        this.renderCursor = Math.min(textLine.length(), renderCursor);
        textLineWasUpdated();
    }
    protected String lineToRender;
    void textLineWasUpdated() {
        if(textLine != null) {
            int maxSize = (int) (getSize().x / textScale.x);
            lineToRender = fontRendererObj.trimStringToWidth(textLine.substring(renderCursor), maxSize);
            if (lineToRender.length() != 0 && cutLine && !textLine.endsWith(lineToRender)) {
                String substring = lineToRender.substring(0, lineToRender.length() - 1);
                int threeDotsSize = fontRendererObj.getStringWidth("...");
                while (substring.length() != 0) {
                    if (fontRendererObj.getStringWidth(substring) + threeDotsSize < maxSize) {
                        lineToRender = substring + "...";
                        break;
                    }
                    substring = substring.substring(0, substring.length() - 1);
                }
            }
        }
    }
    @Override
    protected void VectorsWereUpdated() {
        textLineWasUpdated();
    }
    @Override
    public void render() {
        if(hidden()) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glTranslatef(getCurrentPosition().x, getCurrentPosition().y, 0);
        GL11.glScalef(textScale.x, textScale.y, 1);
        this.fontRendererObj.drawString(lineToRender, 0, 0,
                (int) Vector4fToColor(color),
                dropShadow);
        GL11.glPopMatrix();
    }

}
