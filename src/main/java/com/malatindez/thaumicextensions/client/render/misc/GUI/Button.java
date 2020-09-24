package com.malatindez.thaumicextensions.client.render.misc.GUI;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import scala.util.parsing.json.JSON;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Button extends DefaultGuiObject
        implements EnhancedGuiScreen.Clickable {
    protected Object icon;
    protected MethodObjectPair method;
    protected int zLevel, id;

    /*
     * Button constructor
     * @param icon buttons icon which should be rendered, icon type should be DefaultGuiObject
     * @param obj object where a method is located
     * @param method Method, which will be called when the button is clicked.
     *               Shouldn't be returning any value. Should have 2 parameters:
     *                   * Param Object which will be set with this class
     *                   * Param int id, special number which you can add in constructor to identify caller
     */
   /* public Button(String name, Object icon, Vector2f coordinates,
                  Object obj, Method method, int id, int zLevel, ResolutionRescaleType type,
                  ArrayList<EnhancedGuiScreen.Bind> binds) {
        super(name, coordinates,new Vector2f(1.0f, 1.0f),((DefaultGuiObject)icon).getSize(), zLevel, type);
        this.icon = icon;
        this.obj = obj;
        this.method = method;
        this.binds = binds;
        this.zLevel = zLevel;
        this.id = id;
        updateVectors();
    }*/

    @Override
    public void preInit(String name, Object parent, JSONObject parameters) {
        if(parameters.containsKey("id")) {
            id = (int)JsonToFloat(parameters.get("id"));
        } else {
            id = 0;
        }
        JSONObject icon = (JSONObject) parameters.get(parameters.get("icon"));
        this.icon = EnhancedGuiScreen.createObject(name, this, icon);
    }

    @Override
    public void postInit() {
        ((DefaultGuiObject)icon).postInit();
    }

    public Button(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
        if(parameters.containsKey("method") && parent instanceof DefaultGuiObject) {
            JSONObject obj = (JSONObject) parameters.get("method");
            method = this.getMethodUp(
                    (String) obj.get("object_name"),
                    (String) obj.get("method_name"),
                    new Class[] {Object.class, int.class});
        } else {
            method = null;
        }
    }

    @Override
    public void render() {
        ((DefaultGuiObject)icon).render();
    }


    @Override
    public int getZLevel() {
        return zLevel;
    }

    @Override
    public MethodObjectPair getMethodDown(String objectName, String name, Class[] parameterTypes) {
        if(objectName == this.getName()) {
            getMethodFunc(objectName, name, parameterTypes);
        }
        return ((DefaultGuiObject)icon).getMethodDown(objectName, name, parameterTypes);
    }

    @Override
    public Object getObjectDown(String objectName) {
        if(objectName == this.getName()) {
            return this;
        }
        return ((DefaultGuiObject)icon).getObjectDown(objectName);
    }

    @Override
    protected void VectorsWereUpdated() {
        ((DefaultGuiObject) icon).updateParentBorders(getBorders());
    }

    @Override
    public boolean mouseHandler(Vector2f currentMousePosition) {
        return true;
    }

    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {
        if(button == 0 && method != null) {
            try {
                method.method.invoke(method.object,this, id);
            } catch (Exception ignored) {
                return false;
            }
        }
        return true;
    }


}
