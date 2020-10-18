package com.malatindez.thaumicextensions.client.render.misc.gui;

import com.malatindez.thaumicextensions.client.lib.UtilsFX;
import com.malatindez.thaumicextensions.client.render.gui.GuiEditor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

// This class is being used to automatically scale other elements
@SideOnly(Side.CLIENT)
public class Point extends DefaultGuiObject implements GuiEditor.Editable {
    public Point(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
        setSize(0,0 );
    }

    @Override
    public void render() {

    }

    @Override
    public JSONObject getFullJSON() {
        return generateJSONObject();
    }

    private static final ResourceLocation templateLocation = new ResourceLocation("thaumicextensions", "gui/templates/point_template.json");
    @Override
    public JSONObject getTemplateJSON() {
        String s = UtilsFX.loadFromFile(templateLocation);
        return (JSONObject) JSONValue.parse(s);
    }
}
