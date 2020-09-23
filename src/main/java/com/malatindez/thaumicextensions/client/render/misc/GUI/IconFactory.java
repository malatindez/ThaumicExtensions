package com.malatindez.thaumicextensions.client.render.misc.GUI;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.malatindez.thaumicextensions.client.lib.UtilsFX;
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
public class IconFactory {
    public static class Icon extends DefaultGuiObject {
        public final ResourceLocation texture;
        protected final Vector2f texFrom, texTo, textureSize;
        /**
         * IconFactory.Part constructor
         * @param coordinates Default coordinates to render at
         * @param texFrom Coordinates of top left icons corner on the texture
         * @param iconSize The size of an icon (bottom right corner is texFrom + iconSize)
         * @param textureSize The size of the texture
         * @param scale The scale of an icon (1.0f, 1.0f is a default scale)
         */
        public Icon(@NotNull Vector2f coordinates, @NotNull Vector2f texFrom,
                    @NotNull Vector2f iconSize,    @NotNull Vector2f textureSize,
                    @NotNull Vector2f scale, int zLevel, ResolutionRescaleType type, ResourceLocation texture) {
            super(coordinates,scale,iconSize, zLevel, type);
            this.texFrom     = new Vector2f(texFrom);
            this.textureSize = new Vector2f(textureSize);
            this.texTo       = Vector2f.add(texFrom, iconSize, null);
            this.texture = new ResourceLocation(texture.getResourceDomain(), texture.getResourcePath());
        }

        public Icon(Icon icon, Vector2f coordinates, Vector2f scale, int zLevel) {
            super(coordinates, scale, icon.getSize(), zLevel, icon.getType());
            this.texFrom     = new Vector2f(icon.texFrom);
            this.textureSize = new Vector2f(icon.textureSize);
            this.texTo       = new Vector2f(icon.texTo);
            this.texture = new ResourceLocation(icon.texture.getResourceDomain(), icon.texture.getResourcePath());
        }

        /**
         * Multiply a vector to another vector and place the result in the destination vector.
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
        /**
         * Render an icon on default coordinates
         */
        @Override
        public void render() {
            UtilsFX.bindTexture(texture);
            UtilsFX.drawCustomSizeModalRect(getCurrentPosition(),
                    texFrom, texTo, getScale(), textureSize, getZLevel());
        }

        @Override
        protected void VectorsWereUpdated() {

        }

        @Override
        public int getZLevel() {
            return 0;
        }
    }
    protected final HashMap<String, Icon> parts = new HashMap<String, Icon>();
    private final ResourceLocation texture;
    /**
     * Constructor of IconFactory
     * @param json_file json_file to work with
     */
    public IconFactory(ResourceLocation json_file) {
        this.texture = new ResourceLocation(ThaumicExtensions.MODID, "asd");
    }

    /**
     * @param name Elements name
     * @return element
     */
    public Icon getNewIcon(final String name, Vector2f coordinates, Vector2f scale, int zLevel) {
        return new Icon(parts.get(name), coordinates, scale, zLevel);
    }

}
