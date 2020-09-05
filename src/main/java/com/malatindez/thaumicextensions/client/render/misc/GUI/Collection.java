package com.malatindez.thaumicextensions.client.render.misc.GUI;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import java.util.ArrayList;

public class Collection implements EhnancedGuiScreen.Renderable,
        EhnancedGuiScreen.Clickable, EhnancedGuiScreen.Bindable, EhnancedGuiScreen.Updatable {

    protected final Vector2f coordinates;
    protected final Vector2f size;
    protected final Vector4f borders;

    protected ArrayList<Object> objects = new ArrayList<Object>();

    private int getObjectZLevel(int i) {
        if (objects.get(i) instanceof EhnancedGuiScreen.Renderable) {
            return ((EhnancedGuiScreen.Renderable) objects.get(i)).getZLevel();
        } else if (objects.get(i) instanceof EhnancedGuiScreen.Clickable) {
            return ((EhnancedGuiScreen.Clickable) objects.get(i)).getZLevel();
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
    public void sortObjects() {
        quickSort(0,objects.size());
    }
    public void addObject(Object object) {
        objects.add(object); sortObjects();
    }
    public void addObjects(ArrayList<Object> objects) {
        this.objects.addAll(objects); sortObjects();
    }
    protected void updateBorders() {
        this.borders.set(
                coordinates.x, coordinates.y,
                coordinates.x + size.x, coordinates.y + size.y
        );
    }

    public Vector2f getCoordinates() {
        return new Vector2f(coordinates);
    }
    public void setCoordinates(Vector2f newCoordinates) {
        this.coordinates.set(newCoordinates);
        updateBorders();
    }

    public Vector2f getSize() {
        return new Vector2f(size);
    }

    public Collection(Vector2f coordinates, Vector2f size)  {
        this.coordinates = new Vector2f(coordinates);
        this.size = new Vector2f(size);
        this.borders = new Vector4f();
        updateBorders();
    }

    @Override
    public Vector4f getBorders() {
        return borders;
    }

    @Override
    public boolean mouseHandler(Vector2f currentMousePosition) {
        for(Object object : objects) {
            if (object instanceof EhnancedGuiScreen.Clickable) {
                Vector4f temp = ((EhnancedGuiScreen.Clickable) object).getBorders();
                if(
                        temp.x < currentMousePosition.x &&
                                temp.z > currentMousePosition.x &&
                        temp.y < currentMousePosition.y &&
                                temp.w > currentMousePosition.y &&
                        ((EhnancedGuiScreen.Clickable) object).mouseHandler(currentMousePosition)
                ) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public ArrayList<EhnancedGuiScreen.Bind> getBinds() {
        ArrayList<EhnancedGuiScreen.Bind> binds = new ArrayList<EhnancedGuiScreen.Bind>();
        for(Object object : objects) {
            if (object instanceof EhnancedGuiScreen.Bindable) {
                binds.addAll(((EhnancedGuiScreen.Bindable) object).getBinds());
            }
        }
        return binds;
    }

    @Override
    public void render() {
        for(Object object :  objects) {
            if (object instanceof EhnancedGuiScreen.Renderable) {
                ((EhnancedGuiScreen.Renderable) object).render(new Vector2f(this.coordinates));
            }
        }
    }

    @Override
    public void render(Vector2f coordinates) {
        for(Object object :  objects) {
            if (object instanceof EhnancedGuiScreen.Renderable) {
                ((EhnancedGuiScreen.Renderable) object).render(
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
            if (object instanceof EhnancedGuiScreen.Renderable) {
                ((EhnancedGuiScreen.Renderable) object).render(
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
            if (object instanceof EhnancedGuiScreen.Renderable) {
                ((EhnancedGuiScreen.Renderable) object).resolutionUpdated(previousResolution,currentResolution);
            }
            else if (object instanceof EhnancedGuiScreen.Clickable) {
                ((EhnancedGuiScreen.Clickable) object).resolutionUpdated(previousResolution,currentResolution);
            }
        }
    }

    @Override
    public int getZLevel() {
        return 0;
    }

    @Override
    public void Update() {

    }
}
