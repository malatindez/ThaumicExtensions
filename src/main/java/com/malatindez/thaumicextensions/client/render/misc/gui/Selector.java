package com.malatindez.thaumicextensions.client.render.misc.gui;

import org.json.simple.JSONObject;

public class Selector extends Collection {
    public Selector(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }

    @Override
    public void loadFromJSONObject(JSONObject parameters) {
        super.loadFromJSONObject(parameters);
    }
}
