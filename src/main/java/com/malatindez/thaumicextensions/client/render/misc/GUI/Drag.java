package com.malatindez.thaumicextensions.client.render.misc.GUI;

import com.sun.istack.internal.NotNull;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import java.lang.reflect.Method;

public class Drag extends DefaultGuiObject implements EnhancedGuiScreen.Clickable, EnhancedGuiScreen.needParent {
    protected DefaultGuiObject icon;
    protected final Object obj;
    protected final Method currentlyDragged;
    protected final Method dragEnd;
    protected Collection parent;

    protected Vector4f dragBorders;
    protected Vector2f previousMousePos = null;

    protected void updateBorders() {
        borders.set(
                coordinates.x,
                coordinates.y,
                icon.getSize().x + coordinates.x,
                icon.getSize().y + coordinates.y
        );
    }
    public void setCoordinates(Vector2f newCoordinates) {
        this.coordinates.set(newCoordinates);
        updateBorders();
    }
    public Vector2f getCoordinates() {
        return new Vector2f(coordinates);
    }
    public void setDragBorders(Vector4f vector) {
        this.dragBorders.set(vector);
    }
    public Vector4f getDragBorders() {
        return dragBorders;
    }
    /**
     * Drag constructor
     * @param icon object texture
     * @param obj object which has currentlyDragged and dragEnd methods.
     *            This parameter can be null.
     * @param currentlyDragged object method which has to have a Vector2f parameter which means current position.
     *                         This parameter can be null.
     * @param dragEnd object method which has to have a Vector2f parameter which means current position.
     *                This parameter can be null.
     */
    public Drag(@NotNull GuiTextureMapping.Icon icon, Vector2f coordinates,
                Vector4f dragBorders, int zLevel, Object obj, Method currentlyDragged, Method dragEnd) {
        super(coordinates,new Vector2f(1.0f, 1.0f),icon.getSize(), zLevel);
        this.icon = icon;
        this.dragBorders = new Vector4f(dragBorders);
        this.obj = obj;
        this.currentlyDragged = currentlyDragged;
        this.dragEnd = dragEnd;
    }

    public void drag(@NotNull Vector2f currentMousePos) {
        if(previousMousePos == null) {
            previousMousePos = new Vector2f(currentMousePos);
        } else {
            coordinates.set(
                    coordinates.x + previousMousePos.x - currentMousePos.x,
                    coordinates.y + previousMousePos.y - currentMousePos.y
            );
            previousMousePos.set(currentMousePos);
        }
        if(obj != null && dragEnd != null) {
            try {
                currentlyDragged.invoke(obj, coordinates);
            } catch (Exception ignored) {}
        }
    }
    public void dragEnd() {
        previousMousePos = null;
        if(obj != null && dragEnd != null) {
            try {
                dragEnd.invoke(obj, coordinates);
            } catch (Exception ignored) {}
        }
    }
    @Override
    public void render() {
        this.render(null);
    }

    @Override
    public void render(Vector2f coordinates) {
        icon.render(coordinates);
    }

    @Override
    public boolean scalable() {
        return false;
    }

    @Override
    public void render(Vector2f coordinates, Vector2f scale) {
        icon.render(coordinates);
    }


    @Override
    public Vector4f getBorders() {
        return borders;
    }

    @Override
    public boolean mouseHandler(Vector2f currentMousePosition) {
        return false;
    }

    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {
        return false;
    }

    @Override
    public int getZLevel() {
        return 0;
    }

    @Override
    public void setParent(Collection parent) {
        this.parent = parent;
    }
}

