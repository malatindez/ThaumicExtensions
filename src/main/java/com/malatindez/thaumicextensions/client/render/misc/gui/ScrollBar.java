package com.malatindez.thaumicextensions.client.render.misc.gui;

import com.malatindez.thaumicextensions.client.render.misc.Vectors.Vector;
import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import scala.util.hashing.Hashing;

public class ScrollBar extends DefaultGuiObject implements
        EnhancedGuiScreen.Updatable, EnhancedGuiScreen.Clickable {
    protected Scrollable objectToScroll; // that's a reference
    protected Drag scroll_icon; // that's a reference to a Drag object
    protected DefaultGuiObject scrolling_collection; // this is a reference to a descendant

    public interface Scrollable{
        // offset ∈ [0, 1]
        void setOffsetX(float offset);
        // offset ∈ [0, 1]
        void setOffsetY(float offset);
    }

    Vector2f prevCoordinates = new Vector2f(0, 0);
    float scaleX = 1, scaleY = 1;
    @Override
    public void Update(int flags) {
        if(objectToScroll == null) {
            super.updateDescendants(flags);
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
        super.updateDescendants(flags);
    }

    @Override
    public void mouseHandler(Vector2f currentMousePosition) {
        super.mouseHandlerDescendants(currentMousePosition);
    }

    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {
        return super.mouseClickedDescendants(currentMousePosition, button);
    }

    public ScrollBar(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
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

        String a = (String) getStartupParameters().get("scroll_icon");
        scroll_icon = (Drag) this.getObjectDown(a);

        super.postInit();
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
        this.addObject(scrolling_collection);
    }
}
