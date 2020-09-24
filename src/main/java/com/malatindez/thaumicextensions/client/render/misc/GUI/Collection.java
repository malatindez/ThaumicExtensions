package com.malatindez.thaumicextensions.client.render.misc.GUI;


import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Collection extends DefaultGuiObject implements
        EnhancedGuiScreen.Clickable, EnhancedGuiScreen.Updatable {

    protected final Object selected = null;
    protected final ArrayList<Object> objects = new ArrayList<Object>();
    protected Collection parent = null;

    private int getObjectZLevel(Object obj) {
        if (obj instanceof DefaultGuiObject) {
            return ((DefaultGuiObject) obj).getZLevel();
        }
        return 0;
    }
    private class ObjectComparator implements Comparator<Object> {
        @Override
        public int compare(Object x, Object y) {
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
    }
    public void removeObjects(ArrayList<Object> objects) {
        this.objects.remove(objects);
    }
    public void removeObject(Object object) {
        objects.remove(object);
    }
    @SuppressWarnings("UnusedReturnValue")
    public Object addObject(Object object) {
        if(!(object instanceof DefaultGuiObject)) {
            return null;
        }
        ((DefaultGuiObject) object).resolutionUpdated(this.currentResolution);
        ((DefaultGuiObject) object).updateParentBorders(getBorders());
        objects.add(object); sortObjects();
        return object;
    }
    public void addObjects(ArrayList<Object> objects) {
        this.objects.addAll(objects); sortObjects();
    }
    public boolean isSelected(Object object) {
        return object == selected;
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

    public Collection(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
        if(parameters.containsKey("elements")) {
            JSONObject elements = (JSONObject)parameters.get("elements");
            for(Object key : elements.keySet()) {
                this.addObject(EnhancedGuiScreen.createObject((String)key,this, (JSONObject) elements.get(key)));
            }
        }

    }
    @Override
    public MethodObjectPair getMethodDown(String objectName, String name, Class[] parameterTypes) {
        if(objectName == this.getName()) {
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
        if(objectName == this.getName()) {
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
        for(Object object : objects) {
            if(object instanceof DefaultGuiObject) {
                ((DefaultGuiObject) object).updateParentBorders(getBorders());
            }
        }
    }


    @Override
    public boolean mouseHandler(Vector2f currentMousePosition) {
        for(Object object : objects) {
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
        for(Object object : objects) {
            if (object instanceof EnhancedGuiScreen.Clickable) {
                Vector4f temp = ((EnhancedGuiScreen.Clickable) object).getBorders();
                if(
                        temp.x < currentMousePosition.x &&
                                temp.z > currentMousePosition.x &&
                                temp.y < currentMousePosition.y &&
                                temp.w > currentMousePosition.y &&
                                ((EnhancedGuiScreen.Clickable) object).mouseClicked(currentMousePosition, button)
                ) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void render() {
        for(Object object :  objects) {
            if (object instanceof EnhancedGuiScreen.Renderable) {
                ((EnhancedGuiScreen.Renderable) object).render();
            }
        }
    }



    @Override
    public void resolutionUpdated(Vector2f newResolution) {
        super.resolutionUpdated(newResolution);
        for(Object object :  objects) {
            if (object instanceof EnhancedGuiScreen.Renderable) {
                ((EnhancedGuiScreen.Renderable) object).resolutionUpdated(newResolution);
            }
            else if (object instanceof EnhancedGuiScreen.Clickable) {
                ((EnhancedGuiScreen.Clickable) object).resolutionUpdated(newResolution);
            }
        }
    }

    @Override
    public int getZLevel() {
        return 0;
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
