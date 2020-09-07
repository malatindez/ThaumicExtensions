package com.malatindez.thaumicextensions.client.render.misc.GUI;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public abstract class defaultGuiObject implements EnhancedGuiScreen.Renderable {
    protected final Vector2f coordinates;
    protected final Vector2f scale;
    protected final Vector2f size;
    protected final Vector4f borders;
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
    public Vector2f getScale() {
        return new Vector2f(scale);
    }
    public void setScale(Vector2f scale) {
        this.scale.set(scale);
    }
    public Vector2f getSize() {
        return new Vector2f(size);
    }
    public Vector4f getBorders() {
        return new Vector4f(borders);
    }

    public defaultGuiObject(Vector2f coordinates, Vector2f scale, Vector2f size) {
        this.coordinates = new Vector2f(coordinates);
        this.scale =  new Vector2f(scale);
        this.size =  new Vector2f(size);
        this.borders = new Vector4f();
        updateBorders();
    }
}