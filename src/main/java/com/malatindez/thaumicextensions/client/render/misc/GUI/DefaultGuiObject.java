package com.malatindez.thaumicextensions.client.render.misc.GUI;

import net.minecraft.client.Minecraft;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public abstract class DefaultGuiObject implements EnhancedGuiScreen.Renderable, Comparable {
    protected final Vector2f coordinates;
    protected final Vector2f scale;
    protected final Vector2f size;
    protected final Vector2f currentResolution = new Vector2f(427, 240);
    public static final Vector2f defaultResolution = new Vector2f(427, 240);
    protected final Vector4f borders;
    protected int zLevel;
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

    public DefaultGuiObject(Vector2f coordinates, Vector2f scale, Vector2f size, int zLevel) {
        this.coordinates = new Vector2f(coordinates);
        this.scale =  new Vector2f(scale);
        this.size =  new Vector2f(size);
        this.borders = new Vector4f();
        this.zLevel = zLevel;
        updateBorders();
    }
    public DefaultGuiObject(Vector2f coordinates, Vector2f scale, Vector2f size, boolean updateBorders) {
        this.coordinates = new Vector2f(coordinates);
        this.scale =  new Vector2f(scale);
        this.size =  new Vector2f(size);
        this.borders = new Vector4f();
        if(updateBorders) {
            this.updateBorders();
        }
    }
    @Override
    public int getZLevel() {
        return zLevel;
    }
    @Override
    public void resolutionUpdated(Vector2f newResolution) {
        this.coordinates.set(
                this.coordinates.x * newResolution.x / currentResolution.x,
                this.coordinates.y * newResolution.y / currentResolution.y
        );
        if(this.scalable()) {
            float delta = (float) Math.sqrt(newResolution.x / currentResolution.x * newResolution.y / currentResolution.y);
            this.scale.set(scale.x * delta,
                    scale.y * delta);
            this.size.set(
                    size.x * delta,
                    size.y * delta
            );
        }
        currentResolution.set(newResolution);
        updateBorders();
    }

    public int compareTo(Object o) {
        if (o instanceof DefaultGuiObject) {
            return this.getZLevel() - ((DefaultGuiObject) o).getZLevel();
        }
        return this.getZLevel();
    }
}