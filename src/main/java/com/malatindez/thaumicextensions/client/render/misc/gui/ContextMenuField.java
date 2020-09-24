package com.malatindez.thaumicextensions.client.render.misc.gui;

import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public class ContextMenuField extends DefaultGuiObject implements EnhancedGuiScreen.Clickable,
        EnhancedGuiScreen.Updatable,
        EnhancedGuiScreen.Inputable {
    public DefaultGuiObject icon;
    protected MethodObjectPair rmbClicked;
    protected MethodObjectPair lmbClicked;

    @Override
    public void preInit(String name, Object parent, JSONObject parameters) {
        JSONObject icon = (JSONObject) parameters.get(parameters.get("icon"));
        this.icon = EnhancedGuiScreen.createObject(name, this, icon);
    }

    @Override
    public void postInit() {
        (icon).postInit();
    }

    public ContextMenuField(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
        lmbClicked = getMethod(parameters, "lmbClicked", new Class[] {Object.class});
        rmbClicked = getMethod(parameters, "rmbClicked", new Class[] {Object.class, Vector2f.class});
    }

    @Override
    public void render() {
        if(!hided()) {
            icon.render();
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public MethodObjectPair getMethodDown(String objectName, String name, Class[] parameterTypes) {
        if(objectName.equals(this.getName())) {
            getMethodFunc(objectName, name, parameterTypes);
        }
        return icon.getMethodDown(objectName, name, parameterTypes);
    }

    @Override
    public Object getObjectDown(String objectName) {
        if(objectName.equals(this.getName())) {
            return this;
        }
        return icon.getObjectDown(objectName);
    }

    @Override
    protected void VectorsWereUpdated() {
        icon.updateParentBorders(getBorders());
    }

    @Override
    public boolean mouseHandler(Vector2f currentMousePosition) {
        if(hided()) {
            return false;
        }
        if(icon instanceof EnhancedGuiScreen.Clickable) {
            return ((EnhancedGuiScreen.Clickable) icon).mouseHandler(currentMousePosition);
        }
        return false;
    }

    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {
        if(hided()) {
            return false;
        }
        if(icon instanceof EnhancedGuiScreen.Clickable) {
            if(((EnhancedGuiScreen.Clickable) icon).mouseClicked(currentMousePosition, button)) {
                return true;
            }
            Vector4f temp = ((EnhancedGuiScreen.Clickable) icon).getBorders();
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
        if(icon instanceof EnhancedGuiScreen.Updatable) {
            ((EnhancedGuiScreen.Updatable) icon).Update(flags);
        }
    }

    @Override
    public void keyTyped(char par1, int par2) {
        if(hided()) {
            return;
        }
        if(icon instanceof EnhancedGuiScreen.Inputable) {
            ((EnhancedGuiScreen.Inputable) icon).keyTyped(par1, par2);
        }
    }
}
