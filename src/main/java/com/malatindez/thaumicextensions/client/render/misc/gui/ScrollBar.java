package com.malatindez.thaumicextensions.client.render.misc.gui;

import com.malatindez.thaumicextensions.client.render.misc.Vectors.Vector;
import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import scala.util.hashing.Hashing;

public class ScrollBar extends DefaultGuiObject implements
        EnhancedGuiScreen.Updatable, EnhancedGuiScreen.Clickable {
    protected Scrollable objectToScroll; // that's a reference
    protected DefaultGuiObject scroll_icon; // that's a reference
    protected DefaultGuiObject scrolling_collection; // this is a descendant

    public interface Scrollable{
        // offset ∈ [0, 1]
        void setOffsetX(float offset);
        // offset ∈ [0, 1]
        void setOffsetY(float offset);
        // scale of bar which belongs to range (0, 1]
        // and means size of bar
        float getScaleX();
        float getScaleY();
    }
    Vector2f prevCoordinates = new Vector2f(0, 0);
    float scaleX = 1, scaleY = 1;
    @Override
    public void Update(int flags) {
        if(scrolling_collection instanceof EnhancedGuiScreen.Updatable) {
            ((EnhancedGuiScreen.Updatable) scrolling_collection).Update(flags);
        }
        if(objectToScroll == null) {
            return;
        }
        Vector2f a = scroll_icon.getCoordinates();
        if(prevCoordinates != a) {
            Vector4f b = scroll_icon.getParentBorders();
            b.z -= scroll_icon.getSize().x;
            b.w -= scroll_icon.getSize().y;
            if ((a.x - prevCoordinates.x) != 0) {
                objectToScroll.setOffsetX(a.x / (b.z - b.x));
            } else if ((a.y - prevCoordinates.y) != 0) {
                objectToScroll.setOffsetY(a.y / (b.w - b.y));
            }
            prevCoordinates = a;
        }
        if(objectToScroll.getScaleX() != scaleX) {
            scroll_icon.reScale(objectToScroll.getScaleX() / scaleX, 1);
            scaleX = objectToScroll.getScaleX();
        }
        if(objectToScroll.getScaleY() != scaleY) {
            scroll_icon.reScale(1, objectToScroll.getScaleY() / scaleY);
            scaleY = objectToScroll.getScaleY();
        }
    }

    @Override
    public boolean mouseHandler(Vector2f currentMousePosition) {
        if(scrolling_collection instanceof EnhancedGuiScreen.Clickable)
            return ((EnhancedGuiScreen.Clickable) scrolling_collection).mouseHandler(currentMousePosition);
        return false;
    }

    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {
        if(scrolling_collection instanceof EnhancedGuiScreen.Clickable)
            return ((EnhancedGuiScreen.Clickable) scrolling_collection).mouseClicked(currentMousePosition, button);
        return false;
    }

    public ScrollBar(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public MethodObjectPair getMethodDown(String objectName, String name, Class[] parameterTypes) {
        if(objectName.equals(this.getName())) {
            getMethodFunc(objectName, name, parameterTypes);
        }
        if(scrolling_collection == null) {
            return null;
        }
        return scrolling_collection.getMethodDown(objectName, name, parameterTypes);
    }

    @Override
    public Object getObjectDown(String objectName) {
        if(objectName.equals(this.getName())) {
            return this;
        }
        if(scrolling_collection != null) {
            return scrolling_collection.getObjectDown(objectName);
        }
        return null;
    }

    @Override
    protected void VectorsWereUpdated() {
        if(scrolling_collection != null) {
            scrolling_collection.updateParentBorders(getBorders());
        }
    }


    @Override
    public void resolutionUpdated(Vector2f newResolution) {
        super.resolutionUpdated(newResolution);
        if(scrolling_collection != null) {
            this.scrolling_collection.resolutionUpdated(newResolution);
        }
    }
    @Override
    public void postInit() {
        Object x = this.getObjectUp((String) getStartupParameters().get("objectToScroll"));
        if(x instanceof Scrollable) {
            objectToScroll = (Scrollable) x;
        } else {
            System.out.println("[DEBUG] Caught an exception in " + this.getName() + ". Object is not scrollable.");
            System.out.println("JSONObject values: ");
            System.out.println(getStartupParameters().toJSONString().replace(",","\n,"));
        }

        this.scrolling_collection.postInit();

        String a = (String) getStartupParameters().get("scroll_icon");
        scroll_icon = (DefaultGuiObject) this.getObjectDown(a);
    }

    void scrollUp(DefaultGuiObject object, int id) {

    }

    void scrollDown(DefaultGuiObject object, int id) {

    }

    @Override
    public JSONObject generateJSONObject() {
        return super.generateJSONObject();
    }

    public void loadFromJSONObject(JSONObject parameters) {
        super.loadFromJSONObject(parameters);
        String name = (String) parameters.get("scrolling_collection");
        this.scrolling_collection = EnhancedGuiScreen.createObject(name, this, (JSONObject) parameters.get(name));
    }

    @Override
    public void render() {
        if(scrolling_collection != null) {
            scrolling_collection.render();
        }
    }
}
