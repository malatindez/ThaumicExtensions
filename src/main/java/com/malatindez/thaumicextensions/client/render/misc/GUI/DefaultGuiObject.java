package com.malatindez.thaumicextensions.client.render.misc.GUI;

import net.minecraft.client.Minecraft;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public abstract class DefaultGuiObject implements EnhancedGuiScreen.Renderable, Comparable {
    private final Vector2f coordinates;
    private final Vector2f scale;
    private final Vector2f size;
    private final Vector2f parentCoordinates = new Vector2f(0,0);
    protected final Vector2f currentResolution = new Vector2f(427, 240);
    public static final Vector2f defaultResolution = new Vector2f(427, 240);
    private final Vector4f borders;
    protected int zLevel;
    private final Vector2f currentObjectPosition = new Vector2f(0,0);
    // this function will be called when vectors were updates
    protected abstract void VectorsWereUpdated();
    protected void updateVectors() {
        Vector2f.add(parentCoordinates,coordinates,currentObjectPosition);
        this.borders.set(
                currentObjectPosition.x, currentObjectPosition.y,
                currentObjectPosition.x + size.x, currentObjectPosition.y + size.y
        );
        VectorsWereUpdated();
    }
    public Vector2f getCoordinates() {
        return new Vector2f(coordinates);
    }
    public Vector2f getParentCoordinates() {
        return new Vector2f(parentCoordinates);
    }

    public void setCoordinates(float x, float y) {
        this.coordinates.set(x, y);
        updateVectors();
    }
    public void setCoordinates(Vector2f newCoordinates) {
        setCoordinates(newCoordinates.x, newCoordinates.y);
    }
    public void reScale(float x, float y) {
        this.scale.set(x, y);
        this.size.set(this.size.x * x, this.size.y * y);
        updateVectors();
    }
    public void reScale(Vector2f scale) {
        reScale(scale.x, scale.y);
    }
    public void setSize(float x, float y) {
        this.size.set(x, y);
        updateVectors();
    }
    public void setSize(Vector2f size) {
        this.setSize(size.x, size.y);
    }


    public Vector2f getScale() {
        return new Vector2f(scale);
    }

    public Vector2f getSize() {
        return new Vector2f(size);
    }

    public Vector4f getBorders() {
        return new Vector4f(borders);
    }

    public Vector2f getCurrentPosition() {
        return new Vector2f(currentObjectPosition);
    }
    public DefaultGuiObject(Vector2f coordinates, Vector2f scale, Vector2f size, int zLevel) {
        this.coordinates = new Vector2f(coordinates);
        this.currentObjectPosition.set(coordinates);
        this.scale =  new Vector2f(scale);
        this.size =  new Vector2f(size);
        this.borders = new Vector4f();
        this.zLevel = zLevel;
        updateVectors();
    }
    public DefaultGuiObject(Vector2f coordinates, Vector2f scale, Vector2f size, boolean updateVectors) {
        this.coordinates = new Vector2f(coordinates);
        this.scale =  new Vector2f(scale);
        this.size =  new Vector2f(size);
        this.borders = new Vector4f();
        if(updateVectors) {
            updateVectors();
        }
    }
    @Override
    public int getZLevel() {
        return zLevel;
    }
    @Override
    public void resolutionUpdated(Vector2f newResolution) {
        setCoordinates(
                this.coordinates.x * newResolution.x / currentResolution.x,
                this.coordinates.y * newResolution.y / currentResolution.y
        );
        if(this.scalable()) {
            float delta = (float) Math.sqrt(newResolution.x / currentResolution.x * newResolution.y / currentResolution.y);
            reScale(delta, delta);
        }
        currentResolution.set(newResolution);
    }
    @Override
    public void updateParentCoordinates(Vector2f parentCoordinates) {
        this.parentCoordinates.set(parentCoordinates);  updateVectors();
    }
    public int compareTo(Object o) {
        if (o instanceof DefaultGuiObject) {
            return this.getZLevel() - ((DefaultGuiObject) o).getZLevel();
        }
        return this.getZLevel();
    }
}