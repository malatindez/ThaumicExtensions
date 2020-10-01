package com.malatindez.thaumicextensions.client.render.misc.gui;


import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import java.lang.reflect.Method;

public class Button extends DefaultGuiObject implements EnhancedGuiScreen.Clickable, EnhancedGuiScreen.Updatable {
    protected DefaultGuiObject icon;
    protected DefaultGuiObject hovered_icon;
    protected final MethodObjectPair clicked;
    protected final MethodObjectPair hovered;
    protected int id;

    @Override
    public void preInit(String name, Object parent, JSONObject parameters) {
        if(parameters.containsKey("id")) {
            id = (int)JsonToFloat(parameters.get("id"));
        } else {
            id = 0;
        }
        JSONObject icon = (JSONObject) parameters.get(parameters.get("icon"));
        this.icon = EnhancedGuiScreen.createObject((String) parameters.get("icon"), this, icon);
        try {
            JSONObject hovered_icon = (JSONObject) parameters.get(parameters.get("hovered_icon"));
            this.hovered_icon = EnhancedGuiScreen.createObject((String) parameters.get("hovered_icon"), this, hovered_icon);
        } catch (Exception ignored) {

        }
    }

    @Override
    public void postInit() {
        ((DefaultGuiObject)icon).postInit();
    }

    @Override
    public JSONObject generateJSONObject() {
        JSONObject returnValue = super.generateDefaultJSONObject();
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

    public Button(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
        this.setSize(((DefaultGuiObject)icon).getSize());
        clicked = getMethod(parameters, "clicked", new Class[] {Object.class, int.class});
        hovered = getMethod(parameters, "hovered", new Class[] {Object.class, int.class});
    }

    @Override
    public void render() {
        if(!hided()) {
            if (hovered_icon != null && isHovered) {
                ((DefaultGuiObject) hovered_icon).render();
            } else {
                ((DefaultGuiObject) icon).render();
            }
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public MethodObjectPair getMethodDown(String objectName, String name, Class[] parameterTypes) {
        if(objectName.equals(this.getName())) {
            getMethodFunc(objectName, name, parameterTypes);
        }
        MethodObjectPair retValue = ((DefaultGuiObject)icon).getMethodDown(objectName, name, parameterTypes);
        if(retValue != null || hovered_icon == null) {
            return retValue;
        }
        return ((DefaultGuiObject)hovered_icon).getMethodDown(objectName, name, parameterTypes);
    }

    @Override
    public Object getObjectDown(String objectName) {
        if(objectName.equals(this.getName())) {
            return this;
        }
        Object retValue =  ((DefaultGuiObject)icon).getObjectDown(objectName);
        if(retValue != null || hovered_icon == null) {
            return retValue;
        }
        return ((DefaultGuiObject)hovered_icon).getObjectDown(objectName);
    }

    @Override
    protected void VectorsWereUpdated() {
        ((DefaultGuiObject) icon).updateParentBorders(getBorders());
        if (hovered_icon != null) {
            ((DefaultGuiObject) hovered_icon).updateParentBorders(getBorders());
        }
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
            return true;
        }
        isHovered = false;
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
            } catch (Exception ignored) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public void Update(int flags) {
        if(icon instanceof EnhancedGuiScreen.Updatable) {
            ((EnhancedGuiScreen.Updatable) icon).Update(flags);
        }
    }
}
