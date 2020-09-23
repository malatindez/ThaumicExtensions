package com.malatindez.thaumicextensions.client.render.misc.GUI;

import com.sun.istack.internal.NotNull;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Collection extends DefaultGuiObject implements
        EnhancedGuiScreen.Clickable, EnhancedGuiScreen.Bindable, EnhancedGuiScreen.Updatable, EnhancedGuiScreen.needParent {

    protected Object selected = null;
    protected ArrayList<Object> objects = new ArrayList<Object>();
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
    public Object addObject(@NotNull Object object) {
        if(object == null) {
            return null;
        }
        if (object instanceof EnhancedGuiScreen.Renderable) {
            ((EnhancedGuiScreen.Renderable) object).resolutionUpdated(this.currentResolution);
        } else if (object instanceof EnhancedGuiScreen.Clickable) {
            ((EnhancedGuiScreen.Clickable) object).resolutionUpdated(this.currentResolution);
        }
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
    public Collection(Vector2f coordinates, Vector2f size, int zLevel)  {
        super(coordinates,new Vector2f(1.0f,1.0f),size,zLevel);
    }

    public Collection(Vector2f coordinates, Vector2f scale, Vector2f size, int zLevel)  {
        super(coordinates,scale,size,zLevel);
    }
    // if you've created another collection with this constructor than you should use only one of them
    // Be careful! This constructor deletes objects from another collection.
    public Collection(Collection collection) {
        super(collection.getCoordinates(), collection.getScale(), collection.getSize(), collection.zLevel);
        this.addObjects(collection.objects);
        collection.objects.clear();
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
    public boolean mouseHandler(@NotNull Vector2f currentMousePosition) {
        for(Object object : objects) {
            if (object instanceof EnhancedGuiScreen.Clickable) {
                Vector4f temp = ((EnhancedGuiScreen.Clickable) object).getBorders();
                if(((EnhancedGuiScreen.Clickable) object).mouseHandler(currentMousePosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(@NotNull  Vector2f currentMousePosition, int button) {
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
    public ArrayList<EnhancedGuiScreen.Bind> getBinds() {
        ArrayList<EnhancedGuiScreen.Bind> binds = new ArrayList<EnhancedGuiScreen.Bind>();
        for(Object object : objects) {
            if (object instanceof EnhancedGuiScreen.Bindable) {
                binds.addAll(((EnhancedGuiScreen.Bindable) object).getBinds());
            }
        }
        return binds;
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
    public boolean scalable() {
        return true;
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
    public void setParent(Collection parent) {
        this.parent = parent;
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
