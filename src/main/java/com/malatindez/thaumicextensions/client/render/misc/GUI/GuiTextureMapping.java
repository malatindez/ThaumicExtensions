package com.malatindez.thaumicextensions.client.render.misc.GUI;

import com.malatindez.thaumicextensions.client.lib.UtilsFX;
import com.malatindez.thaumicextensions.client.render.misc.GUI.EhnancedGuiScreen;
import com.sun.istack.internal.NotNull;

import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector2f;

import java.util.HashMap;


/**
 * This class can be used to map a GUI texture
 * such as you can render something from texture at needed coordinates just by invoking
 * TextureMapping.get("arrow").render(x,y)
 * You should bind GUI texture before calling render functions
 */
public class GuiTextureMapping {
    public static class Icon implements EhnancedGuiScreen.Renderable {
        protected final Vector2f coordinates, texFrom, texTo, iconSize, scale, textureSize;
        /**
         * GuiTextureMapping.Part constructor
         * @param coordinates Default coordinates to render at
         * @param texFrom Coordinates of top left icons corner on the texture
         * @param iconSize The size of an icon (bottom right corner is texFrom + iconSize)
         * @param textureSize The size of the texture
         * @param scale The scale of an icon (1.0f, 1.0f is a default scale)
         */
        public Icon(@NotNull Vector2f coordinates, @NotNull Vector2f texFrom,
                    @NotNull Vector2f iconSize,    @NotNull Vector2f textureSize,
                    @NotNull Vector2f scale) {
            this.coordinates = new Vector2f(coordinates);
            this.texFrom     = new Vector2f(texFrom);
            this.iconSize    = new Vector2f(iconSize);
            this.scale       = new Vector2f(scale);
            this.textureSize = new Vector2f(textureSize);
            this.texTo       = Vector2f.add(texFrom, iconSize, null);
        }

        /**
         * GuiTextureMapping.Part constructor
         * @param coordinates Default coordinates to render at
         * @param texFrom Coordinates of top left icons corner on the texture
         * @param iconSize The size of an icon (bottom right corner is texFrom + iconSize)
         * @param textureSize The size of the texture
         */
        public Icon(@NotNull Vector2f coordinates, @NotNull Vector2f texFrom,
                    @NotNull Vector2f iconSize,    @NotNull Vector2f textureSize) {
            this(coordinates, texFrom, iconSize, textureSize, new Vector2f(1.0f, 1.0f));
        }

        /**
         * GuiTextureMapping.Part constructor
         * @param x The default x coordinate to render on
         * @param y The default y coordinate to render on
         * @param u The x coordinate of top left icon corner on the texture
         * @param v The y coordinate of top left icon corner on the texture
         * @param iconSizeU The x size of an icon
         * @param iconSizeV The y size of an icon
         * @param scaleX The x scale of an icon(1.0f is default)
         * @param scaleY The y scale of an icon(1.0f is default)
         * @param textureWidth The x texture width
         * @param textureHeight The y texture width
         */
        public Icon(int x,      int y,       float u,         float v,
                    int iconSizeU, int iconSizeV, float scaleX, float scaleY,
                    float textureWidth,      float textureHeight) {
            this(new Vector2f(x, y), new Vector2f(u, v),
                    new Vector2f(iconSizeU, iconSizeV), new Vector2f(scaleX, scaleY),
                    new Vector2f(textureWidth, textureHeight));
        }

        /**
         * GuiTextureMapping.Part constructor
         * @param x The default x coordinate to render on
         * @param y The default y coordinate to render on
         * @param u The x coordinate of top left icon corner on the texture
         * @param v The y coordinate of top left icon corner on the texture
         * @param iconSizeU The x size of an icon
         * @param iconSizeV The y size of an icon
         * @param textureWidth The x texture width
         * @param textureHeight The y texture width
         */
        public Icon(int x, int y, float u, float v,
                    int iconSizeU, int iconSizeV, float textureWidth, float textureHeight) {
            this(new Vector2f(x, y), new Vector2f(u, v),
                    new Vector2f(iconSizeU, iconSizeV), new Vector2f(textureWidth, textureHeight));
        }

        /**
         * Multiply a vector to another vector and place the result in a destination vector.
         * Multiplication defines as x = left.x * right.x; y = left.y * right.y;
         * @param left The LHS vector
         * @param right The RHS vector
         * @param dest The destination vector, or null if a new vector is to be created
         * @return the mul of left and right in dest
         */
        private Vector2f mul(@NotNull Vector2f left, @NotNull Vector2f right, Vector2f dest) {
            if(dest == null) {
                return new Vector2f(left.x * right.x, left.y * right.y);
            } else {
                dest.set(left.x * right.x, left.y * right.y);
                return dest;
            }
        }
        public Vector2f getTexFrom() {
            return new Vector2f(texFrom);
        }
        public Vector2f getTexTo() {
            return new Vector2f(texTo);
        }
        public Vector2f getIconSize() {
            return new Vector2f(iconSize);
        }
        /**
         * Render an icon on default coordinates
         */
        @Override
        public void render() {
            this.render(this.coordinates);
        }

        /**
         * Render an icon on coordinates
         * @param coordinates The coordinates to render at
         */
        @Override
        public void render(@NotNull Vector2f coordinates) {
            UtilsFX.drawScaledCustomSizeModalRect(coordinates, texFrom, texTo, this.mul(scale, iconSize, null), textureSize);
        }

        /**
         * @return If icon is scalable
         */
        @Override
        public boolean scalable() {
            return true;
        }

        /**
         * Render an icon on coordinates with a scale
         * @param coordinates The coordinates to render at
         * @param scale The scale of an Icon (default scale is 1.0f)
         */
        @Override
        public void render(@NotNull Vector2f coordinates, @NotNull Vector2f scale) {
            UtilsFX.drawScaledCustomSizeModalRect(coordinates, texFrom, texTo, this.mul(scale, iconSize, null), textureSize);
        }

        @Override
        public void resolutionUpdated(Vector2f previousResolution, Vector2f currentResolution) {

        }
    }
    protected HashMap<String, Icon> parts = new HashMap<String, Icon>();
    ResourceLocation texture;

    /**
     * Constructor of GuiTextureMapping
     * @param texture Texture to bind
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
     * @param name Elements name
     * @return element
     */
    public Icon getGuiElement(final String name) {
        return parts.get(name);
    }

    /**
     * @param name Elements name
     * @param icon Element to add
     */
    public void addElement(final String name, Icon icon) {
        this.parts.put(name, icon);
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
     * @param name Elements name
     */
    public void renderElement(final String name) {
        parts.get(name).render();
    }

    /**
     * Render only one element with this name on this coordinates
     * @param name Elements name
     * @param coordinates Elements coordinates
     */
    public void renderElement(final String name, Vector2f coordinates) {
        parts.get(name).render(coordinates);
    }

    /**
     * Render only one element with this name on this coordinates and scale
     * @param name Elements name
     * @param coordinates Coordinates to render at
     * @param scale Elements scale
     */
    public void renderElement(final String name, Vector2f coordinates, Vector2f scale) {
        parts.get(name).render(coordinates, scale);
    }
}
