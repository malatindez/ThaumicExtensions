package com.malatindez.thaumicextensions.client.render.misc.gui;

import org.json.simple.JSONObject;

public class ScrollBar extends DefaultGuiObject {
    public interface Scrollable{
        void setOffsetX(float offset);
        float getOffsetX();
        float getSizeX();
        void setOffsetY(float offset);
        float getOffsetY();
        float getSizeY();
    }
    public ScrollBar(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public MethodObjectPair getMethodDown(String objectName, String name, Class[] parameterTypes) {
        return null;
    }

    @Override
    public Object getObjectDown(String objectName) {
        return null;
    }

    @Override
    protected void VectorsWereUpdated() {

    }

    @Override
    public void preInit(String name, Object parent, JSONObject parameters) {

    }
    protected DefaultGuiObject objectToScroll;
    @Override
    public void postInit() {
        Object x = this.getObjectUp((String) getStartupParameters().get("objectsName"));
        if(x instanceof Scrollable) {
            objectToScroll = (DefaultGuiObject) x;
        } else {
            System.out.println("[DEBUG] Caught an exception in " + this.getName() + ". Object is not scrollable.");
            System.out.println("JSONObject values: ");
            System.out.println(getStartupParameters().toJSONString());
        }
    }

    @Override
    public JSONObject generateJSONObject() {
        return super.generateDefaultJSONObject();
    }

    @Override
    public void render() {

    }
}
