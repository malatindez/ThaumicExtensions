package com.malatindez.thaumicextensions.client.render.misc.gui;

import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public class ContextMenuField extends DefaultGuiObject implements EnhancedGuiScreen.Clickable,
        EnhancedGuiScreen.Updatable,
        EnhancedGuiScreen.Inputable {
    protected MethodObjectPair rmbClicked;
    protected MethodObjectPair lmbClicked;
    public DefaultGuiObject icon;

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject generateJSONObject() {
        JSONObject returnValue = super.generateJSONObject();
        JSONObject a = (JSONObject) returnValue.get(getName());
        a.put(icon.getName(), icon.generateJSONObject().get(icon.getName()));
        putMethod(a, "rmbClicked", rmbClicked);
        putMethod(a, "lmbClicked", lmbClicked);
        return returnValue;
    }

    @Override
    public void loadFromJSONObject(JSONObject parameters) {
        super.loadFromJSONObject(parameters);
        JSONObject icon = (JSONObject) parameters.get(parameters.get("icon"));
        this.icon = EnhancedGuiScreen.createObject((String) parameters.get("icon"), this, icon);
        this.addObject(this.icon);
    }
    @Override
    public void postInit() {
        JSONObject parameters = getStartupParameters();
        lmbClicked = getMethod(parameters, "lmbClicked", new Class[] {DefaultGuiObject.class});
        rmbClicked = getMethod(parameters, "rmbClicked", new Class[] {DefaultGuiObject.class, Vector2f.class});
    }

    public ContextMenuField(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }


    @Override
    public void mouseHandler(Vector2f currentMousePosition) {
        mouseHandlerDescendants(currentMousePosition);
    }

    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {
        if(hided()) {
            return false;
        }
        if(mouseClickedDescendants(currentMousePosition, button)) {
            return true;
        }
        if(icon instanceof EnhancedGuiScreen.Clickable && !icon.hided()) {
            Vector4f temp = icon.getBorders();
            if (temp.x < currentMousePosition.x &&
                    temp.z > currentMousePosition.x &&
                    temp.y < currentMousePosition.y &&
                    temp.w > currentMousePosition.y) {
                return false;
            }
        }
        Vector4f temp = getBorders();
        if(temp.x < currentMousePosition.x &&
           temp.z > currentMousePosition.x &&
           temp.y < currentMousePosition.y &&
           temp.w > currentMousePosition.y) {
            if(button == 0) {
                try {
                    lmbClicked.method.invoke(lmbClicked.object, this);
                } catch (Exception ignored) {
                    return false;
                }
            } else if(button == 1) {
                try {
                    rmbClicked.method.invoke(rmbClicked.object, this, currentMousePosition);
                } catch (Exception ignored) {
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    public void Update(int flags) {
        updateDescendants(flags);
    }

    @Override
    public boolean keyTyped(char par1, int par2) {
        return keyTypedDescendants(par1, par2);
    }
}
