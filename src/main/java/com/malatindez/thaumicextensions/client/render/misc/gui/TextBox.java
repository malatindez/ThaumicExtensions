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

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class TextBox extends DefaultGuiObject implements GuiEditor.Editable {
    protected FontRenderer fontRendererObj;
    protected String text;
    protected Vector2f textScale;
    public Vector4f color;
    public boolean dropShadow;


    @SuppressWarnings("unchecked")
    @Override
    public JSONObject generateJSONObject() {
        JSONObject returnValue = super.generateJSONObject();
        JSONObject a = (JSONObject) returnValue.get(getName());
        a.put("text", this.text);
        a.put("color",VecToJson(color));
        a.put("textScale",VecToJson(textScale));
        a.put("dropShadow",dropShadow);
        return returnValue;
    }

    @Override
    public void loadFromJSONObject(JSONObject parameters) {
        super.loadFromJSONObject(parameters);
        fontRendererObj = Minecraft.getMinecraft().fontRenderer;
        text = (String)parameters.get("text");
        color = Json4Vec(parameters.get("color"));
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
        textWasUpdated();
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getFullJSON() {
        JSONObject returnValue = super.getFullDefaultJSON();
        JSONObject a = (JSONObject) returnValue.get(getName());
        a.put("text", this.text);
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

    public void setText(String text) {
        this.text = text;
        textWasUpdated();
    }
    public String getText() {
        return text;
    }
    public void setTextScale(Vector2f scale) {
        this.textScale.set(scale);
        textWasUpdated();
    }
    public Vector2f getTextScale() {
        return new Vector2f(textScale);
    }
    public TextBox(String name, Object parent, JSONObject parameters) {
        super(name,parent,parameters);
    }

    protected final ArrayList<String> linesToRender = new ArrayList<String>();
    protected Vector2f previousSize = new Vector2f(0,0);
    protected String previousText;
    protected void textWasUpdated() {
        if (linesToRender != null && (previousSize != getSize()  || !text.equals(previousText))) {
            String buf = previousText = text;
            linesToRender.clear();
            previousSize = getSize();
            Vector2f size = getSize();
            size.set(size.x / textScale.x, size.y / textScale.y);
            int height = 0;
            while (buf.length() > 0) {
                int a = buf.indexOf("\n") + 1;
                if (a == 0) {
                    a = buf.length();
                }
                String b = fontRendererObj.trimStringToWidth(buf, (int)(size.x));
                if(b.length() < a) {
                    if(b.contains(" ")) {
                        a = b.lastIndexOf(" ") + 1;
                    } else {
                        a = b.length();
                    }
                }
                b = buf.substring(0, a);
                buf = buf.substring(a);
                linesToRender.add(b.replaceAll("[\n\r]", ""));
                if(size.y  < (height + 2 * fontRendererObj.FONT_HEIGHT)) {
                    break;
                }
                height += fontRendererObj.FONT_HEIGHT;
            }
        }
    }
    public int fitsInBox(String text) {
        String buf = text;
        Vector2f size = getSize();
        size.set(size.x / textScale.x, size.y / textScale.y);
        int height = 0;
        while (buf.length() > 0) {
            int a = buf.indexOf("\n");
            if (a == -1) {
                a = buf.length();
            }
            String str = fontRendererObj.trimStringToWidth(buf.substring(0, a), (int)(size.x));
            if(str.length() < a) {
                if(str.contains(" ")) {
                    a = str.lastIndexOf(" ");
                } else {
                    a = str.length();
                }
            }
            buf = buf.substring(a);
            if(size.y  < (height + 2 * fontRendererObj.FONT_HEIGHT)) {
                break;
            }
            height += fontRendererObj.FONT_HEIGHT;
        }
        return text.length() - buf.length();
    }
    @Override
    public void render() {
        if(hidden()) {
            return;
        }
        int current_height = 0;
        for(String line : this.linesToRender) {
            GL11.glPushMatrix();
            GL11.glTranslatef(this.getCurrentPosition().x, this.getCurrentPosition().y + current_height, 0);
            GL11.glScalef(textScale.x, textScale.y, 1);
            this.fontRendererObj.drawString(line, 0, 0,
                    (int)Vector4fToColor(color),
                    dropShadow);
            GL11.glPopMatrix();
            current_height += fontRendererObj.FONT_HEIGHT * textScale.y;
        }
    }

    @Override
    protected void VectorsWereUpdated() {
        textWasUpdated();
    }
}