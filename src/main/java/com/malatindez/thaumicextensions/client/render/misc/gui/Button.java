package com.malatindez.thaumicextensions.client.render.misc.gui;


import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import scala.util.parsing.json.JSON;

public class Button extends DefaultGuiObject implements EnhancedGuiScreen.Clickable, EnhancedGuiScreen.Updatable {
    protected DefaultGuiObject icon;
    protected DefaultGuiObject hovered_icon;
    protected MethodObjectPair clicked;
    protected MethodObjectPair hovered;
    protected int id;


    @SuppressWarnings("unchecked")
    @Override
    public JSONObject generateJSONObject() {
        JSONObject returnValue = super.generateJSONObject();
        JSONObject a = (JSONObject) returnValue.get(getName());
        a.put("id", (long)id);
        a.put("icon", this.icon.getName());
        putMethod(a, "clicked", clicked);
        putMethod(a, "hovered", hovered);
        a.put(icon.getName(), icon.generateJSONObject().get(icon.getName()));
        if(hovered_icon != null) {
            a.put("hovered_icon", this.hovered_icon.getName());
            a.put(hovered_icon.getName(), hovered_icon.generateJSONObject().get(hovered_icon.getName()));
        }
        return returnValue;
    }
    @Override
    public void loadFromJSONObject(JSONObject parameters) {
        super.loadFromJSONObject(parameters);
        clicked = getMethod(parameters, "clicked", new Class[] {DefaultGuiObject.class, int.class});
        hovered = getMethod(parameters, "hovered", new Class[] {DefaultGuiObject.class, int.class});

        if(parameters.containsKey("id")) {
            id = (int)JsonToFloat(parameters.get("id"));
        } else {
            id = 0;
        }

        JSONObject icon = (JSONObject) parameters.get(parameters.get("icon"));
        this.icon = EnhancedGuiScreen.createObject((String) parameters.get("icon"), this, icon);
        this.descendants.add(this.icon);
        try {
            JSONObject hovered_icon = (JSONObject) parameters.get(parameters.get("hovered_icon"));
            this.hovered_icon = EnhancedGuiScreen.createObject((String) parameters.get("hovered_icon"), this, hovered_icon);
            this.descendants.add(this.hovered_icon);
        } catch (Exception ignored) { }

        this.setSize(this.icon.getSize());
    }

    public Button(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }

    boolean isHovered = false;
    @Override
    public boolean mouseHandler(Vector2f currentMousePosition) {
        if(hided()) {
            return false;
        }
        Vector4f temp = getBorders();
        if(
                temp.x < currentMousePosition.x &&
                        temp.z > currentMousePosition.x &&
                        temp.y < currentMousePosition.y &&
                        temp.w > currentMousePosition.y
        ) {
            isHovered = true;
            try {
                hovered.method.invoke(hovered.object,this, id);
            } catch (Exception ignored) {}
            if(this.hovered_icon != null) {
                icon.hide();
                hovered_icon.show();
            }
            return true;
        }
        isHovered = false;
        if(this.hovered_icon != null) {
            icon.show();
            hovered_icon.hide();
        }
        return false;
    }

    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {
        if(hided()) {
            return false;
        }
        Vector4f temp = getBorders();
        if(button == 0 && clicked != null &&
           temp.x < currentMousePosition.x &&
           temp.z > currentMousePosition.x &&
           temp.y < currentMousePosition.y &&
           temp.w > currentMousePosition.y) {
            try {
                clicked.method.invoke(clicked.object,this, id);
                return true;
            } catch (Exception ignored) { }
        }
        return false;
    }

    @Override
    public void Update(int flags) {
        this.updateDescendants(flags);
    }
}
