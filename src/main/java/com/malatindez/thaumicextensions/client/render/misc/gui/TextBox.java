package com.malatindez.thaumicextensions.client.render.misc.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import org.json.simple.JSONObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import java.util.ArrayList;

public class TextBox extends DefaultGuiObject {
    protected FontRenderer fontRendererObj;
    protected String text;
    protected Vector2f textScale;
    public Vector4f color;
    public boolean dropShadow;


    @Override
    public void postInit() {

    }

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
        if(hided()) {
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

    @SuppressWarnings("rawtypes")
    @Override
    public MethodObjectPair getMethodDown(String objectName, String name, Class[] parameterTypes) {
        if(objectName.equals(this.getName())) {
            getMethodFunc(objectName, name, parameterTypes);
        }
        return null;
    }

    @Override
    public Object getObjectDown(String objectName) {
        if(objectName.equals(this.getName())) {
            return this;
        }
        return null;
    }

    @Override
    protected void VectorsWereUpdated() {
        textWasUpdated();
    }

    @Override
    public int getZLevel() {
        return 0;
    }
}