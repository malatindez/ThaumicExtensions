package com.malatindez.thaumicextensions.client.render.misc.GUI;

import com.sun.istack.internal.NotNull;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Button implements EhnancedGuiScreen.Renderable, EhnancedGuiScreen.Clickable, EhnancedGuiScreen.Bindable {
    protected final GuiTextureMapping.Icon icon;
    protected final Object obj;
    protected final Method method;
    protected final ArrayList<EhnancedGuiScreen.Bind> binds;
    protected final Vector4f borders;
    protected final Vector2f coordinates;
    protected int zLevel;
    protected void updateBorders() {
        borders.set(
                coordinates.x,
                coordinates.y,
                icon.getIconSize().x + coordinates.x,
                icon.getIconSize().y + coordinates.y
        );
    }
    public void updateCoordinates(Vector2f newCoordinates) {
        this.coordinates.set(newCoordinates);
        updateBorders();
    }
    /**
     * Button constructor
     * @param icon buttons icon which should be rendered
     * @param obj object where a method is located
     * @param method Method, which will be called when the button is clicked.
     *               Shouldn't be returning any value and shouldn't have any parameters either
     */
    public Button(@NotNull GuiTextureMapping.Icon icon, @NotNull Vector2f coordinates,
                  @NotNull Object obj, @NotNull Method method, ArrayList<EhnancedGuiScreen.Bind> binds, int zLevel) {
        this.icon = icon;
        this.obj = obj;
        this.method = method;
        this.binds = binds;
        this.coordinates = coordinates;
        this.borders = new Vector4f();
        this.zLevel = zLevel;
        updateBorders();
    }

    @Override
    public void render() {
        icon.render(this.coordinates);
    }

    @Override
    public void render(@NotNull Vector2f coordinates) {
        icon.render(coordinates);
    }

    @Override
    public boolean scalable() {
        return false;
    }

    @Override
    public void render(@NotNull Vector2f coordinates, Vector2f scale) {
        icon.render(coordinates);
    }

    @Override
    public void resolutionUpdated(Vector2f previousResolution, Vector2f currentResolution) {
        this.coordinates.set(
                this.coordinates.x * currentResolution.x / previousResolution.x,
                this.coordinates.y * currentResolution.y / previousResolution.y
        );
        updateBorders();
    }

    @Override
    public int getZLevel() {
        return zLevel;
    }

    @Override
    public Vector4f getBorders() {
        return borders;
    }

    @Override
    public boolean mouseHandler(@NotNull Vector2f currentMousePosition) {
        try {
            method.invoke(obj);
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<EhnancedGuiScreen.Bind> getBinds() {
        return binds;
    }
}