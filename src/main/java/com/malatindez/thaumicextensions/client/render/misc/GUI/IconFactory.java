package com.malatindez.thaumicextensions.client.render.misc.GUI;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.malatindez.thaumicextensions.client.lib.UtilsFX;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.lwjgl.util.vector.Vector2f;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;


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
        protected Icon(Vector2f coordinates, Vector2f texFrom,
                    Vector2f iconSize,    Vector2f textureSize,
                    Vector2f scale, int zLevel, ResolutionRescaleType type, ResourceLocation texture) {
            super("none", coordinates,scale,iconSize, zLevel, type);
            this.texFrom     = new Vector2f(texFrom);
            this.textureSize = new Vector2f(textureSize);
            this.texTo       = Vector2f.add(texFrom, iconSize, null);
            this.texture = new ResourceLocation(texture.getResourceDomain(), texture.getResourcePath());
        }

        public Icon(Icon icon, String name, Vector2f coordinates, Vector2f scale, int zLevel, ResolutionRescaleType type) {
            super(name, coordinates, scale, icon.getSize(), zLevel, type);
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
        private Vector2f mul(Vector2f left, Vector2f right, Vector2f dest) {
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
    private Vector2f Json2Vec(Object array) {
        return new Vector2f(
                ((Long)((JSONArray)array).get(0)).floatValue(),
                ((Long)((JSONArray)array).get(1)).floatValue()
        );
    }
    /**
     * Constructor of IconFactory
     * @param json_file json_file to work with
     */
    public IconFactory(ResourceLocation json_file) {
        InputStream x;
        try {
            x = Minecraft.getMinecraft().getResourceManager().getResource(json_file).getInputStream();
        } catch (Exception e) {
            System.out.println("Exception caught! Wrong ResourceFile for IconFactory.");
            System.out.println(json_file);
            e.printStackTrace();
            this.texture = new ResourceLocation(ThaumicExtensions.MODID, "asd");
            return;
        }
        InputStreamReader isReader = new InputStreamReader(x);
        //Creating a BufferedReader object
        BufferedReader reader = new BufferedReader(isReader);
        StringBuffer sb = new StringBuffer();
        String str = "";
        try {
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
        } catch (Exception ignored) { }
        str = sb.toString();
        JSONObject jsonObject = (JSONObject) JSONValue.parse(str);

        this.texture = new ResourceLocation(
                (String)jsonObject.get("resource_domain"),
                (String)jsonObject.get("resource_path"));

        Vector2f textureSize = Json2Vec(jsonObject.get("textureSize"));
        Set<Object> set = jsonObject.keySet();
        for (Object object : set) {
            if (object instanceof String) {
                if (!((String)object).equals("resource_domain") &&
                        !((String)object).equals("resource_path") &&
                        !((String)object).equals("textureSize")
                ) {
                    parts.put((String)object, new Icon(
                            new Vector2f(0,0),
                            Json2Vec(((JSONObject) jsonObject.get(object)).get("texFrom")),
                            Json2Vec(((JSONObject) jsonObject.get(object)).get("iconSize")),
                            textureSize,
                            new Vector2f(1,1),
                            0,
                            DefaultGuiObject.ResolutionRescaleType.NONE,
                            texture
                    ));
                }
            }
        }
    }

    /**
     *
     * @param IconName
     * @param objectName
     * @param coordinates
     * @param scale
     * @param zLevel
     * @param type
     * @return
     */
    public Icon getNewIcon(final String IconName, final String objectName, Vector2f coordinates, Vector2f scale,
                           int zLevel, DefaultGuiObject.ResolutionRescaleType type) {
        return new Icon(parts.get(IconName), objectName, coordinates, scale, zLevel, type);
    }

}
