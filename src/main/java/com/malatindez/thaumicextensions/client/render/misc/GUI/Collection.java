package com.malatindez.thaumicextensions.client.render.misc.GUI;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import java.util.ArrayList;

public class Collection implements EhnancedGuiScreen.Renderable,
        EhnancedGuiScreen.Clickable, EhnancedGuiScreen.Bindable, EhnancedGuiScreen.Updatable {

    protected Vector2f coordinates;
    protected Vector4f borders;

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

    @Override
    public Vector4f getBorders() {
        return null;
    }

    @Override
    public boolean mouseHandler(Vector2f currentMousePosition) {
        return false;
    }

    @Override
    public ArrayList<EhnancedGuiScreen.Bind> getBinds() {
        return null;
    }

    @Override
    public void render() {

    }

    @Override
    public void render(Vector2f coordinates) {

    }

    @Override
    public boolean scalable() {
        return false;
    }

    @Override
    public void render(Vector2f coordinates, Vector2f scale) {

    }

    @Override
    public void resolutionUpdated(Vector2f previousResolution, Vector2f currentResolution) {

    }

    @Override
    public int getZLevel() {
        return 0;
    }

    @Override
    public void Update() {

    }
}
