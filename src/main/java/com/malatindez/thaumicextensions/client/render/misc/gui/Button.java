package com.malatindez.thaumicextensions.client.render.misc.gui;


import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public class Button extends DefaultGuiObject implements EnhancedGuiScreen.Clickable, EnhancedGuiScreen.Updatable {
    protected Object icon;
    protected final MethodObjectPair clicked;
    protected final MethodObjectPair hovered;
    protected int id;

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
        this.setSize(((DefaultGuiObject)icon).getSize());
        clicked = getMethod(parameters, "clicked", new Class[] {Object.class, int.class});
        hovered = getMethod(parameters, "hovered", new Class[] {Object.class, int.class});
    }

    @Override
    public void render() {
        if(!hided()) {
            ((DefaultGuiObject) icon).render();
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public MethodObjectPair getMethodDown(String objectName, String name, Class[] parameterTypes) {
        if(objectName.equals(this.getName())) {
            getMethodFunc(objectName, name, parameterTypes);
        }
        return ((DefaultGuiObject)icon).getMethodDown(objectName, name, parameterTypes);
    }

    @Override
    public Object getObjectDown(String objectName) {
        if(objectName.equals(this.getName())) {
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
            try {
                hovered.method.invoke(hovered.object,this, id);
                return true;
            } catch (Exception ignored) {}
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
            } catch (Exception ignored) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void Update(int flags) {
        if(icon instanceof EnhancedGuiScreen.Updatable) {
            ((EnhancedGuiScreen.Updatable) icon).Update(flags);
        }
    }
}
