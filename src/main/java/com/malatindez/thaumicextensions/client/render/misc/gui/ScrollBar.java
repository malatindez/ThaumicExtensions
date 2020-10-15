package com.malatindez.thaumicextensions.client.render.misc.gui;

import com.malatindez.thaumicextensions.client.render.misc.Vectors.Vector;
import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import scala.util.hashing.Hashing;

public class ScrollBar extends Collection {
    protected Scrollable objectToScroll; // this is a reference to an object which should be scrolled
    protected Drag scroll_icon; // this is a reference to a Drag object
    protected DefaultGuiObject scrolling_collection; // this is a reference to a descendant

    public interface Scrollable{
        /**
         * @param offset x offset which should ∈ [0, 1]
         */
        void setOffsetX(float offset);
        /**
         * @param offset y offset which should ∈ [0, 1]
         */
        void setOffsetY(float offset);
        /**
         * @return float x offset which ∈ [0, 1]
         */
        float getOffsetX();
        /**
         * @return float y offset which ∈ [0, 1]
         */
        float getOffsetY();
    }

    Vector2f prevCoordinates = new Vector2f(0, 0);
    float scaleX = 1, scaleY = 1;
    float currentOffsetX = 0;
    float currentOffsetY = 0;
    @Override
    public void Update(int flags) {
        if(objectToScroll == null) {
            super.Update(flags);
            return;
        }
        Vector2f a = scroll_icon.getCoordinates();
        if(prevCoordinates != a) {
            Vector4f b = scroll_icon.getParentBorders();
            b.z -= scroll_icon.getSize().x;
            b.w -= scroll_icon.getSize().y;
            if ((a.x - prevCoordinates.x) != 0) {
                currentOffsetX = Math.max(0, Math.min(1, a.x / (b.z - b.x)));
                objectToScroll.setOffsetX(currentOffsetX);
            } else if ((a.y - prevCoordinates.y) != 0) {
                currentOffsetY = Math.max(0, Math.min(1, a.y / (b.w - b.y)));
                objectToScroll.setOffsetY(currentOffsetY);
            }
            prevCoordinates = a;
        }
        if(currentOffsetX != objectToScroll.getOffsetX() || currentOffsetY != objectToScroll.getOffsetY()) {
            currentOffsetX = objectToScroll.getOffsetX();
            currentOffsetY = objectToScroll.getOffsetY();
            Vector4f b = scroll_icon.getParentBorders();
            b.z -= scroll_icon.getSize().x;
            b.w -= scroll_icon.getSize().y;
            scroll_icon.setCoordinates(currentOffsetX * (b.z - b.x), currentOffsetY * (b.w - b.y));
            prevCoordinates.set(scroll_icon.getCoordinates());
        }
        super.Update(flags);
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

    @Override
    public JSONObject generateJSONObject() {
        return super.generateJSONObject();
    }

    public void loadFromJSONObject(JSONObject parameters) {
        super.loadFromJSONObject(parameters);
    }
}
