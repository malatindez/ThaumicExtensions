package com.malatindez.thaumicextensions.client.render.misc.gui;


import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Collection extends DefaultGuiObject implements
        EnhancedGuiScreen.Clickable, EnhancedGuiScreen.Updatable, EnhancedGuiScreen.Inputable {

    private ArrayList<DefaultGuiObject> descendants;

    public ArrayList<DefaultGuiObject> getDescendants() {
        return descendants;
    }

    private class ObjectComparator implements Comparator<DefaultGuiObject> {
        @Override
        public int compare(DefaultGuiObject x, DefaultGuiObject y) {
            if(x == null) {
                return -1;
            } else if (y == null) {
                return 1;
            }
            return x.getZLevel() - y.getZLevel();
        }
    }

    private ObjectComparator objectComparator;


    protected void sortObjects() {
        Collections.sort(descendants, objectComparator);
        Collections.reverse(descendants);
    }

    @SuppressWarnings("UnusedReturnValue")
    public Object addObject(DefaultGuiObject object) {
        if(object == null) {
            return null;
        }
        object.setParent(this);
        object.updateParentBorders(getBorders());
        object.resolutionUpdated(this.currentResolution);
        descendants.add(object); sortObjects();
        return object;
    }


    @SuppressWarnings("unchecked")
    @Override
    public JSONObject generateJSONObject() {
        JSONObject returnValue = super.generateJSONObject();
        JSONObject a = (JSONObject) returnValue.get(getName());
        JSONObject b = new JSONObject();
        for(DefaultGuiObject object : descendants) {
            b.put(object.getName(), object.generateJSONObject().get(object.getName()));
        }
        a.put("elements", b);
        return returnValue;
    }

    @Override
    public void loadFromJSONObject(JSONObject parameters) {
        this.descendants = new ArrayList<DefaultGuiObject>();
        super.loadFromJSONObject(parameters);
        if(parameters.containsKey("elements")) {
            JSONObject elements = (JSONObject)parameters.get("elements");
            for(Object key : elements.keySet()) {
                this.addObject(EnhancedGuiScreen.createObject((String)key,this, (JSONObject) elements.get(key)));
            }
        }
    }

    @Override
    protected void VectorsWereUpdated() {
        for(DefaultGuiObject object : descendants) {
            object.updateParentBorders(getBorders());
        }
    }


    public Collection(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }

    @Override
    public void postInit() {
        for (DefaultGuiObject descendant : descendants) {
            descendant.setParent(this);
        }
        for (DefaultGuiObject descendant : descendants) {
            descendant.postInit();
        }
        super.postInit();
    }



    @Override
    public void render() {
        if(hidden()) {
            return;
        }
        for(int i = descendants.size() - 1; i >= 0; i--) {
            try {
                descendants.get(i).render();
            } catch (Exception e) {
                System.out.println("[DEBUG] Caught an exception during rendering. Stacktrace: ");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void resolutionUpdated(Vector2f newResolution) {
        super.resolutionUpdated(newResolution);
        for(DefaultGuiObject object : descendants) {
            object.resolutionUpdated(newResolution);
        }
    }
    @Override
    public boolean keyTyped(char par1, int par2) {
        if(hidden()) {
            return false;
        }
        for(Object object : descendants) {
            if(object instanceof EnhancedGuiScreen.Inputable &&
                    ((EnhancedGuiScreen.Inputable) object).keyTyped(par1, par2)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void mouseHandler(Vector2f currentMousePosition) {
        if(hidden()) {
            return;
        }
        for(DefaultGuiObject object : descendants) {
            if (object instanceof EnhancedGuiScreen.Clickable) {
                ((EnhancedGuiScreen.Clickable) object).mouseHandler(currentMousePosition);
            }
        }
    }

    @Override
    public boolean mouseClicked( Vector2f currentMousePosition, int button) {
        if(hidden()) {
            return false;
        }
        for(DefaultGuiObject
                object : descendants) {
            if (object instanceof EnhancedGuiScreen.Clickable) {
                if(((EnhancedGuiScreen.Clickable) object).mouseClicked(currentMousePosition, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void Update(int flags) {
        for(Object object : descendants) {
            if (object instanceof EnhancedGuiScreen.Updatable) {
                ((EnhancedGuiScreen.Updatable) object).Update(flags);
            }
        }
    }
}
