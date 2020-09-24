package com.malatindez.thaumicextensions.client.render.misc.gui;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.malatindez.thaumicextensions.client.lib.UtilsFX;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.json.simple.JSONValue;
import org.lwjgl.util.vector.Vector2f;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;


/**
 * This class can be used to map a gui texture
 * such as you can render something from texture at needed coordinates just by invoking
 * TextureMapping.get("arrow").render(x,y)
 * You should bind gui texture before calling render functions
 */
@SuppressWarnings("Convert2Diamond")
public class IconFactory {
    public static class IconSample {
        public final ResourceLocation texture;
        public final Vector2f texFrom, texTo, textureSize;
        protected IconSample(Vector2f texFrom, Vector2f iconSize, Vector2f textureSize, ResourceLocation texture) {
            this.texFrom = new Vector2f(texFrom);
            this.textureSize = new Vector2f(textureSize);
            this.texTo       = Vector2f.add(texFrom, iconSize, null);
            this.texture = new ResourceLocation(texture.getResourceDomain(), texture.getResourcePath());
        }
        protected IconSample(IconSample obj) {
            this.texture     = new ResourceLocation(obj.texture.getResourceDomain(), obj.texture.getResourcePath());
            this.texFrom     = new Vector2f(obj.texFrom);
            this.textureSize = new Vector2f(obj.textureSize);
            this.texTo       = new Vector2f(obj.texTo);

        }
    }
    public static class Icon extends DefaultGuiObject {
        /*
         * IconFactory.Part constructor
         * @param coordinates Default coordinates to render at
         * @param texFrom Coordinates of top left icons corner on the texture
         * @param iconSize The size of an icon (bottom right corner is texFrom + iconSize)
         * @param textureSize The size of the texture
         * @param scale The scale of an icon (1.0f, 1.0f is a default scale)
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
        }*/
        protected IconSample sample;

        @Override
        public void preInit(String name, Object parent, JSONObject parameters) {
                sample = IconFactory.getFactory(
                    new ResourceLocation(
                            (String)parameters.get("mapping_resource_domain"),
                            (String)parameters.get("mapping_resource_path")
                    )
            ).getIconSample((String)parameters.get("mapping_icon_name"));
        }

        @Override
        public void postInit() {

        }

        public Icon(String name, Object parent, JSONObject parameters) {
            super(name,parent,parameters);
            setSize(Vector2f.sub(sample.texTo, sample.texFrom,null));
            reScale(getScale());
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
        /**
         * Render an icon on default coordinates
         */
        @Override
        public void render() {
            if(hided()) {
                return;
            }
            UtilsFX.bindTexture(sample.texture);
            UtilsFX.drawCustomSizeModalRect(getCurrentPosition(),
                    sample.texFrom, sample.texTo, getScale(), sample.textureSize, getZLevel());
        }

        @Override
        protected void VectorsWereUpdated() {

        }
    }
    protected final HashMap<String, IconSample> parts = new HashMap<String, IconSample>();
    @SuppressWarnings("FieldCanBeLocal")
    private final ResourceLocation texture;
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
        StringBuilder sb = new StringBuilder();
        String str;
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

        Vector2f textureSize = DefaultGuiObject.Json2Vec(jsonObject.get("textureSize"));
        for (Object object : jsonObject.keySet()) {
            if (object instanceof String) {
                if (!object.equals("resource_domain") &&
                        !object.equals("resource_path") &&
                        !object.equals("textureSize")
                ) {
                    parts.put((String)object, new IconSample(
                            DefaultGuiObject.Json2Vec(((JSONObject) jsonObject.get(object)).get("texFrom")),
                            DefaultGuiObject.Json2Vec(((JSONObject) jsonObject.get(object)).get("iconSize")),
                            textureSize,
                            texture
                    ));
                }
            }
        }
    }

    public IconSample getIconSample(final String IconName) {
        return new IconSample(parts.get(IconName));
    }
    private static final HashMap<ResourceLocation, IconFactory> factories = new HashMap<ResourceLocation, IconFactory>();
    public static IconFactory getFactory(ResourceLocation json_file) {
        if(!factories.containsKey(json_file)) {
            factories.put(
                    json_file,
                    new IconFactory(json_file)
            );
        }
        return factories.get(json_file);
    }
}
