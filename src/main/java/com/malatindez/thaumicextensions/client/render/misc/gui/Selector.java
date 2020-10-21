package com.malatindez.thaumicextensions.client.render.misc.gui;

import com.malatindez.thaumicextensions.client.lib.UtilsFX;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class Selector extends Collection {
    public Selector(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }
    ArrayList<String> lines;
    TextLine target_line;
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject generateJSONObject() {
        JSONObject returnValue = super.generateJSONObject();
        JSONObject a = (JSONObject) returnValue.get(getName());
        JSONArray b = new JSONArray();
        b.addAll(lines);
        a.put("lines", b);
        if(target_line != null)
            a.put("target_line", target_line.getDomainName(this));
        return returnValue;
    }
    @Override
    public void loadFromJSONObject(JSONObject parameters) {
        super.loadFromJSONObject(parameters);
        JSONArray a = ((JSONArray)parameters.get("lines"));
        for(Object object : a)
            if(object instanceof String)
                lines.add((String)object);
        target_line = (TextLine) this.getObjectDown((String) getStartupParameters().get("target_line"));
    }

    @Override
    public JSONObject getFullJSON() {
        return generateJSONObject();
    }

    private static final ResourceLocation templateLocation = new ResourceLocation("thaumicextensions", "gui/templates/selector_template.json");
    @Override
    public JSONObject getTemplateJSON() {
        String s = UtilsFX.loadFromFile(templateLocation);
        return (JSONObject) JSONValue.parse(s);
    }

}
