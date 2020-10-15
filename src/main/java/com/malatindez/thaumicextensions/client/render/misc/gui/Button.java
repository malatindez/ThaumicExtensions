package com.malatindez.thaumicextensions.client.render.misc.gui;


import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import scala.util.parsing.json.JSON;

public class Button extends Collection {
    protected DefaultGuiObject icon; // reference to an icon
    protected DefaultGuiObject hovered_icon; // reference to a hovered icon
    protected MethodObjectPair clicked;
    protected MethodObjectPair hovered;
    protected MethodObjectPair hoveringStopped;
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
        hoveringStopped = getMethod(parameters, "hoveringStopped", new Class[] {DefaultGuiObject.class, int.class});

        if(parameters.containsKey("id")) {
            id = (int)JsonToFloat(parameters.get("id"));
        } else {
            id = 0;
        }
        icon = (DefaultGuiObject) this.getObjectUp((String) parameters.get("icon"));
        if(parameters.containsKey("hovered_icon")) {
            hovered_icon = (DefaultGuiObject) this.getObjectUp((String) parameters.get("hovered_icon"));
            if(hovered_icon != null) {
                hovered_icon.hide();
            } else {
                System.out.println("[DEBUG] ERROR! hovered_icon wasn't found in " + getGlobalName());
            }
        }
    }

    public Button(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }

    boolean isHovered = false;
    @Override
    public void mouseHandler(Vector2f currentMousePosition) {
        if(hided()) {
            return;
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
            return;
        }
        isHovered = false;
        if(this.hovered_icon != null && (icon.hided() || !hovered_icon.hided())) {
            icon.show();
            hovered_icon.hide();
            try {
                hoveringStopped.method.invoke(hoveringStopped.object,this, id);
            } catch (Exception ignored) {}
        }
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

}
