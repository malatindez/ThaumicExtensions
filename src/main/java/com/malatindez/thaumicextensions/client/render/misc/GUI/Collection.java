package com.malatindez.thaumicextensions.client.render.misc.GUI;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import java.util.ArrayList;

public class Collection extends defaultGuiObject implements
        EnhancedGuiScreen.Clickable, EnhancedGuiScreen.Bindable, EnhancedGuiScreen.Updatable, EnhancedGuiScreen.needParent {

    protected Object selected = null;
    protected ArrayList<Object> objects = new ArrayList<Object>();
    protected Collection parent = null;

    private int getObjectZLevel(int i) {
        if (objects.get(i) instanceof EnhancedGuiScreen.Renderable) {
            return ((EnhancedGuiScreen.Renderable) objects.get(i)).getZLevel();
        } else if (objects.get(i) instanceof EnhancedGuiScreen.Clickable) {
            return ((EnhancedGuiScreen.Clickable) objects.get(i)).getZLevel();
        }
        return 0;
    }
    private int sortPartition(int beginning, int end) {
        int left, right, loc, flag;
        Object temp;
        loc = left = beginning; right = end;
        flag = 0;
        while(flag != 1) {
            while(getObjectZLevel(loc) <= getObjectZLevel(right) && loc != right) {
                right--;
            }
            if (loc == right) {
                flag = 1;
            } else if (getObjectZLevel(loc) > getObjectZLevel(right)) {
                temp = objects.get(loc);
                objects.set(loc, objects.get(right));
                objects.set(right, temp);
                loc = right;
            }
            if(flag!=1)
            {
                while(getObjectZLevel(loc) >= getObjectZLevel(left) && (loc!=left)) {
                    left++;
                }
                if(loc == left) {
                    flag = 1;
                }
                else if(getObjectZLevel(loc) < getObjectZLevel(left)) {
                    temp = objects.get(loc);
                    objects.set(loc, objects.get(left));
                    objects.set(left, temp);
                    loc = left;
                }
            }
        }
        return loc;
    }
    private void quickSort(int beginning, int end) {
        int loc;
        if (beginning < end) {
            loc = sortPartition(beginning, end);
            quickSort(beginning, loc - 1);
            quickSort(loc + 1, end);
        }
    }
    protected void sortObjects() {
        quickSort(0,objects.size());
    }


    public void removeObjects(ArrayList<Object> objects) {
        objects.remove(objects);
    }
    public void removeObject(Object object) {
        objects.remove(object);
    }
    public void addObject(Object object) {
        objects.add(object); sortObjects();
    }
    public void addObjects(ArrayList<Object> objects) {
        this.objects.addAll(objects); sortObjects();
    }
    public boolean isSelected(Object object) {
        return object == selected;
    }
    public Collection(Vector2f coordinates, Vector2f size)  {
        super(coordinates,new Vector2f(1.0f,1.0f),size);
    }

    @Override
    public Vector4f getBorders() {
        return borders;
    }

    @Override
    public boolean mouseHandler(Vector2f currentMousePosition) {
        for(Object object : objects) {
            if (object instanceof EnhancedGuiScreen.Clickable) {
                Vector4f temp = ((EnhancedGuiScreen.Clickable) object).getBorders();
                if(
                        temp.x < currentMousePosition.x &&
                                temp.z > currentMousePosition.x &&
                        temp.y < currentMousePosition.y &&
                                temp.w > currentMousePosition.y &&
                        ((EnhancedGuiScreen.Clickable) object).mouseHandler(currentMousePosition)
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
                ((EnhancedGuiScreen.Renderable) object).render(new Vector2f(this.coordinates));
            }
        }
    }

    @Override
    public void render(Vector2f coordinates) {
        for(Object object :  objects) {
            if (object instanceof EnhancedGuiScreen.Renderable) {
                ((EnhancedGuiScreen.Renderable) object).render(
                        new Vector2f(
                                this.coordinates.x + coordinates.x,
                                this.coordinates.y + coordinates.y
                        )
                );
            }
        }
    }

    @Override
    public boolean scalable() {
        return false;
    }

    @Override
    public void render(Vector2f coordinates, Vector2f scale) {
        for(Object object :  objects) {
            if (object instanceof EnhancedGuiScreen.Renderable) {
                ((EnhancedGuiScreen.Renderable) object).render(
                        new Vector2f(
                                this.coordinates.x + coordinates.x,
                                this.coordinates.y + coordinates.y
                        ),
                        new Vector2f(scale)
                );
            }
        }
    }

    @Override
    public void resolutionUpdated(Vector2f previousResolution, Vector2f currentResolution) {
        for(Object object :  objects) {
            if (object instanceof EnhancedGuiScreen.Renderable) {
                ((EnhancedGuiScreen.Renderable) object).resolutionUpdated(previousResolution,currentResolution);
            }
            else if (object instanceof EnhancedGuiScreen.Clickable) {
                ((EnhancedGuiScreen.Clickable) object).resolutionUpdated(previousResolution,currentResolution);
            }
        }
    }

    @Override
    public int getZLevel() {
        return 0;
    }

    @Override
    public void Update() {
        for(Object object : objects) {
            if (object instanceof EnhancedGuiScreen.Updatable) {
                ((EnhancedGuiScreen.Updatable) object).Update();
            }
        }
    }

    @Override
    public void setParent(Collection parent) {
        this.parent = parent;
    }
}
