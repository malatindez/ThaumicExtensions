package com.malatindez.thaumicextensions.client.render.misc.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.json.simple.JSONObject;

@SideOnly(Side.CLIENT)
public class Selector extends Collection {
    public Selector(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }

    @Override
    public void loadFromJSONObject(JSONObject parameters) {
        super.loadFromJSONObject(parameters);
    }
}
