package com.malatindez.thaumicextensions.client.render.misc.gui;

import com.malatindez.thaumicextensions.ThaumicExtensions;
import com.malatindez.thaumicextensions.client.lib.UtilsFX;

import com.malatindez.thaumicextensions.client.render.misc.Vectors.Vector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
@SideOnly(Side.CLIENT)
public class IconFactory {
    public static class IconSample {
        public final ResourceLocation texture;
        public final ResourceLocation mapping;
        public final Vector2f texFrom, texTo, textureSize;
        public final String iconName;
        protected IconSample(String iconName, Vector2f texFrom, Vector2f iconSize, Vector2f textureSize,
                             ResourceLocation texture, ResourceLocation mapping) {
            this.iconName = iconName;
            this.texFrom = new Vector2f(texFrom);
            this.textureSize = new Vector2f(textureSize);
            this.texTo       = Vector2f.add(texFrom, iconSize, null);
            this.texture = new ResourceLocation(texture.getResourceDomain(), texture.getResourcePath());
            this.mapping = new ResourceLocation(mapping.getResourceDomain(), mapping.getResourcePath());
        }
        protected IconSample(IconSample obj) {
            this.iconName    = obj.iconName;
            this.texture     = new ResourceLocation(obj.texture.getResourceDomain(), obj.texture.getResourcePath());
            this.mapping     = new ResourceLocation(obj.mapping.getResourceDomain(), obj.mapping.getResourcePath());
            this.texFrom     = new Vector2f(obj.texFrom);
            this.textureSize = new Vector2f(obj.textureSize);
            this.texTo       = new Vector2f(obj.texTo);

        }
    }
    @SideOnly(Side.CLIENT)
    public static class Icon extends DefaultGuiObject {
        protected IconSample sample;

        @SuppressWarnings("unchecked")
        @Override
        public JSONObject generateJSONObject() {
            JSONObject returnValue = super.generateJSONObject();
            JSONObject a = (JSONObject) returnValue.get(getName());
            a.put("mapping_resource_domain", sample.mapping.getResourceDomain());
            a.put("mapping_resource_path", sample.mapping.getResourcePath());
            a.put("mapping_icon_name", sample.iconName);
            return returnValue;
        }
        @Override
        public void loadFromJSONObject(JSONObject parameters) {
            super.loadFromJSONObject(parameters);
            sample = IconFactory.getFactory(
                    new ResourceLocation(
                            (String)parameters.get("mapping_resource_domain"),
                            (String)parameters.get("mapping_resource_path")
                    )
            ).getIconSample((String)parameters.get("mapping_icon_name"));
            if(!parameters.containsKey("size"))
                setSize(Vector2f.sub(sample.texTo, sample.texFrom,null));
            reScale(getScale());
        }

        public Icon(String name, Object parent, JSONObject parameters) {
            super(name,parent,parameters);
        }

        Vector2f current_scale;
        @Override
        public void VectorsWereUpdated() {
            if(sample != null) {
                current_scale = new Vector2f(0,0);
                Vector2f.sub(sample.texTo, sample.texFrom, current_scale);
                Vector.div(getSize(), current_scale, current_scale);
                Vector.mul(getScale(), current_scale, current_scale);
            }
        }

        /**
         * Render an icon on default coordinates
         */
        @Override
        public void render() {
            if(hidden()) {
                return;
            }
            UtilsFX.bindTexture(sample.texture);
            UtilsFX.drawCustomSizeModalRect(getCurrentPosition(),
                    sample.texFrom, sample.texTo,
                    current_scale,
                    sample.textureSize, getZLevel());
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

        Vector2f textureSize = DefaultGuiObject.Json2Vec(jsonObject.get("size"));
        for (Object object : jsonObject.keySet()) {
            if (object instanceof String) {
                if (!object.equals("resource_domain") &&
                        !object.equals("resource_path") &&
                        !object.equals("size")
                ) {
                    parts.put((String)object, new IconSample(
                            (String)object,
                            DefaultGuiObject.Json2Vec(((JSONObject) jsonObject.get(object)).get("texFrom")),
                            DefaultGuiObject.Json2Vec(((JSONObject) jsonObject.get(object)).get("iconSize")),
                            textureSize,
                            texture,
                            json_file
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
