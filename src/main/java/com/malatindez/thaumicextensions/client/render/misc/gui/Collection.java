package com.malatindez.thaumicextensions.client.render.misc.gui;


import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Collection extends DefaultGuiObject implements
        EnhancedGuiScreen.Clickable, EnhancedGuiScreen.Updatable, EnhancedGuiScreen.Inputable {


    @Override
    public boolean keyTyped(char par1, int par2) {
        return keyTypedDescendants(par1, par2);
    }


    @SuppressWarnings("unchecked")
    @Override
    public JSONObject generateJSONObject() {
        JSONObject returnValue = super.generateJSONObject();
        JSONObject a = (JSONObject) returnValue.get(getName());
        JSONObject b = new JSONObject();
        for(DefaultGuiObject object : getDescendants()) {
            b.put(object.getName(), object.generateJSONObject().get(object.getName()));
        }
        a.put("elements", b);
        return returnValue;
    }

    @Override
    public void loadFromJSONObject(JSONObject parameters) {
        super.loadFromJSONObject(parameters);
        if(parameters.containsKey("elements")) {
            JSONObject elements = (JSONObject)parameters.get("elements");
            for(Object key : elements.keySet()) {
                this.addObject(EnhancedGuiScreen.createObject((String)key,this, (JSONObject) elements.get(key)));
            }
        }
    }

    public Collection(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }
    @SuppressWarnings("rawtypes")

    @Override
    public void mouseHandler(Vector2f currentMousePosition) {
        mouseHandlerDescendants(currentMousePosition);
    }

    @Override
    public boolean mouseClicked( Vector2f currentMousePosition, int button) {
        return mouseClickedDescendants(currentMousePosition, button);
    }

    @Override
    public void Update(int flags) {
        super.updateDescendants(flags);
    }
}
