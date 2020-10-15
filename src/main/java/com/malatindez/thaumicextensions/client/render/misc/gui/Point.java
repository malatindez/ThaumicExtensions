package com.malatindez.thaumicextensions.client.render.misc.gui;

import org.json.simple.JSONObject;

// This class is being used to automatically scale other elements
public class Point extends DefaultGuiObject {
    public Point(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
        setSize(0,0 );
    }

    @Override
    public void render() {

    }
}
