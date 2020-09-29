package com.malatindez.thaumicextensions.client.render.misc.gui;


import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Collection extends DefaultGuiObject implements
        EnhancedGuiScreen.Clickable, EnhancedGuiScreen.Updatable, EnhancedGuiScreen.Inputable {

    protected final ArrayList<DefaultGuiObject> objects = new ArrayList<DefaultGuiObject>();
    protected Collection parent = null;

    private int getObjectZLevel(DefaultGuiObject obj) {
        obj.getZLevel();
        return 0;
    }

    @Override
    public boolean keyTyped(char par1, int par2) {
        if(hided()) {
            return false;
        }
        for(Object object : objects) {
            if(object instanceof EnhancedGuiScreen.Inputable &&
                    ((EnhancedGuiScreen.Inputable) object).keyTyped(par1, par2)) {
                return true;
            }
        }
        return false;
    }

    private class ObjectComparator implements Comparator<DefaultGuiObject> {
        @Override
        public int compare(DefaultGuiObject x, DefaultGuiObject y) {
            if(x == null) {
                return -1;
            } else if (y == null) {
                return 1;
            }
            return getObjectZLevel(x) - getObjectZLevel(y);
        }
    }
    private ObjectComparator objectComparator;
    protected void sortObjects() {
        Collections.sort(objects, objectComparator);
        Collections.reverse(objects);
    }
    public void removeObjects(ArrayList<Object> objects) {
        this.objects.remove(objects);
    }
    public void removeObject(Object object) {
        objects.remove(object);
    }
    @SuppressWarnings("UnusedReturnValue")
    public Object addObject(DefaultGuiObject object) {
        if(!(object instanceof DefaultGuiObject)) {
            return null;
        }
        object.updateParentBorders(getBorders());
        object.resolutionUpdated(this.currentResolution);
        objects.add(object); sortObjects();
        return object;
    }
    /*
    public Collection(String name, Vector2f coordinates, Vector2f size, int zLevel, ResolutionRescaleType type)  {
        super(name, coordinates,new Vector2f(1.0f,1.0f),size,zLevel, type);
    }

    public Collection(String name, Vector2f coordinates, Vector2f scale, Vector2f size, int zLevel,
                      ResolutionRescaleType type)  {
        super(name, coordinates,scale,size,zLevel, type);
    }
    // if you've created another collection with this constructor than you should use only one of them
    // Be careful! This constructor deletes objects from another collection.
    public Collection(Collection collection) {
        super(collection.getName(), collection.getCoordinates(), collection.getScale(),
                collection.getSize(), collection.zLevel, collection.getType());
        this.addObjects(collection.objects);
        collection.objects.clear();
    }*/

    @Override
    public void preInit(String name, Object parent, JSONObject parameters) {

    }

    @Override
    public void postInit() {
        for(Object object : objects) {
            ((DefaultGuiObject)object).postInit();
        }
    }

    @Override
    public JSONObject generateJSONObject() {
        JSONObject returnValue = super.generateDefaultJSONObject();
        JSONObject a = (JSONObject) returnValue.get(getName());
        for(DefaultGuiObject object : objects) {
            a.put(object.getName(), object.generateJSONObject().get(object.getName()));
        }
        return returnValue;
    }

    public Collection(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
        if(parameters.containsKey("elements")) {
            JSONObject elements = (JSONObject)parameters.get("elements");
            for(Object key : elements.keySet()) {
                this.addObject(EnhancedGuiScreen.createObject((String)key,this, (JSONObject) elements.get(key)));
            }
        }

    }
    @SuppressWarnings("rawtypes")
    @Override
    public MethodObjectPair getMethodDown(String objectName, String name, Class[] parameterTypes) {
        if(objectName.equals(this.getName())) {
            getMethodFunc(objectName, name, parameterTypes);
        }
        MethodObjectPair retValue = null;
        for(Object obj : objects) {
            if(retValue != null) {
                return retValue;
            }
            retValue = ((DefaultGuiObject)obj).getMethodDown(objectName, name, parameterTypes);
        }
        return retValue;
    }

    @Override
    public Object getObjectDown(String objectName) {
        if(objectName.equals(this.getName())) {
            return this;
        }
        Object retValue = null;
        for(Object obj : objects) {
            if(retValue != null) {
                return retValue;
            }
            retValue = ((DefaultGuiObject)obj).getObjectDown(objectName);
        }
        return retValue;
    }

    @Override
    protected void VectorsWereUpdated() {
        if(objects == null) {
            return;
        }
        for(DefaultGuiObject object : objects) {
            object.updateParentBorders(getBorders());
        }
    }


    @Override
    public boolean mouseHandler(Vector2f currentMousePosition) {
        if(hided()) {
            return false;
        }
        for(DefaultGuiObject object : objects) {
            if (object instanceof EnhancedGuiScreen.Clickable) {
                if(((EnhancedGuiScreen.Clickable) object).mouseHandler(currentMousePosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked( Vector2f currentMousePosition, int button) {
        if(hided()) {
            return false;
        }
        for(DefaultGuiObject
                object : objects) {
            if (object instanceof EnhancedGuiScreen.Clickable) {
                if(((EnhancedGuiScreen.Clickable) object).mouseClicked(currentMousePosition, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void render() {
        if(hided()) {
            return;
        }
        for(int i = objects.size() - 1; i >= 0; i--) {
            try {
                objects.get(i).render();
            } catch (Exception e) {
                System.out.println("[DEBUG] Caught an exception during rendering. Stacktrace: ");
                e.printStackTrace();
            }
        }
    }



    @Override
    public void resolutionUpdated(Vector2f newResolution) {
        super.resolutionUpdated(newResolution);
        for(DefaultGuiObject object :  objects) {
            ((EnhancedGuiScreen.Renderable) object).resolutionUpdated(newResolution);
            if (object instanceof EnhancedGuiScreen.Clickable) {
                ((EnhancedGuiScreen.Clickable) object).resolutionUpdated(newResolution);
            }
        }
    }

    @Override
    public void Update(int flags) {
        for(Object object : objects) {
            if (object instanceof EnhancedGuiScreen.Updatable) {
                ((EnhancedGuiScreen.Updatable) object).Update(flags);
            }
        }
    }
}
