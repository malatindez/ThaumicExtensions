package com.malatindez.thaumicextensions.client.render.gui;

import com.malatindez.thaumicextensions.client.lib.UtilsFX;
import net.minecraft.client.gui.Gui;

import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;

import java.util.HashMap;

/*
    This class can be used to map a gui texture
    such as you can render something from texture at needed coordinates just by invoking
    TextureMapping.get("arrow").render(x,y)
    You should bind gui id before calling render functions
*/
public class GuiTextureMapping {
    public static class Part {
        public final Vector2f coordinates, texFrom, texTo, scaledTo, textureSize;
        public Part(Vector2f coordinates, Vector2f texFrom, Vector2f texTo, Vector2f textureSize, Vector2f scaledTo) {
            this.coordinates = new Vector2f(coordinates);
            this.texFrom     = new Vector2f(texFrom);
            this.texTo       = new Vector2f(texTo);
            this.scaledTo    = new Vector2f(scaledTo);
            this.textureSize = new Vector2f(textureSize);
        }
        public Part(Vector2f coordinates, Vector2f texFrom, Vector2f texTo, Vector2f textureSize) {
            this(coordinates, texFrom, texTo, textureSize, Vector2f.sub(texTo, texFrom, null));
        }
        public Part(int x,      int y,       float u,         float v,
                    int uWidth, int vHeight, int scaledWidth, int scaledHeight,
                    float textureWidth,      float textureHeight) {
            this(new Vector2f(x, y), new Vector2f(u, v),
                    new Vector2f(uWidth, vHeight), new Vector2f(scaledWidth, scaledHeight),
                    new Vector2f(textureWidth, textureHeight));
        }
        public Part(int x, int y, float u, float v,
                    int uWidth, int vHeight, float textureWidth, float textureHeight) {
            this(new Vector2f(x, y), new Vector2f(u, v),
                    new Vector2f(uWidth, vHeight), new Vector2f(textureWidth, textureHeight));
        }
        public void render() {
            this.render(null);
        }
        public void render(Vector2f coordinates) {
            if (coordinates == null) {
                coordinates = this.coordinates;
            }
            UtilsFX.drawScaledCustomSizeModalRect(coordinates,texFrom,texTo,scaledTo,textureSize);
        }
        public void render(Vector2f coordinates, Vector2f scale) {
            UtilsFX.drawScaledCustomSizeModalRect(coordinates,texFrom,texTo,scale,textureSize);
        }
    }
    protected HashMap<String, Part> parts = new HashMap<String, Part>();
    ResourceLocation texture;

    /**
     * Constructor of GuiTextureMapping
     * @param texture texture to bind
     */
    public GuiTextureMapping(ResourceLocation texture) {
        this.texture = texture;
    }

    /**
     * @return mapped texture
     */
    public ResourceLocation getTexture(){
        return texture;
    }

    /**
     * Binds mapped texture
     */
    public void bindTexture(){
        UtilsFX.bindTexture(texture);
    }

    /**
     * @param name elements name
     * @return element
     */
    public Part getGuiElement(final String name) {
        return parts.get(name);
    }

    /**
     * @param name name of an element
     * @param part element
     */
    public void addElement(final String name, Part part) {
        this.parts.put(name, part);
    }

    /**
     * Render all elements
     */
    public void renderAll() {
        for(String key : parts.keySet()) {
            parts.get(key).render();
        }
    }

    /**
     * Render only one element with this name
     * @param name elements name
     */
    public void renderElement(final String name) {
        parts.get(name).render();
    }

    /**
     * Render only one element with this name on this coordinates
     * @param name elements name
     * @param coordinates elements coordinates
     */
    public void renderElement(final String name, Vector2f coordinates) {
        parts.get(name).render(coordinates);
    }

    /**
     * Render only one element with this name on this coordinates and scale
     * @param name elements name
     * @param coordinates elements coordinates
     * @param scale elements scale
     */
    public void renderElement(final String name, Vector2f coordinates, Vector2f scale) {
        parts.get(name).render(coordinates, scale);
    }
}
