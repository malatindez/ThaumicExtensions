package com.malatindez.thaumicextensions.client.render.misc.GUI;

import com.sun.istack.internal.NotNull;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class Button extends DefaultGuiObject
        implements EnhancedGuiScreen.Clickable, EnhancedGuiScreen.Bindable, EnhancedGuiScreen.needParent {
    protected final Object icon;
    protected final Object obj;
    protected final Method method;
    protected final ArrayList<EnhancedGuiScreen.Bind> binds;
    protected Collection parent = null;
    protected int zLevel, id;
    protected void updateBorders() {
        borders.set(
                coordinates.x,
                coordinates.y,
                ((DefaultGuiObject)icon).getSize().x + coordinates.x,
                ((DefaultGuiObject)icon).getSize().y + coordinates.y
        );
    }
    public void updateCoordinates(Vector2f newCoordinates) {
        this.coordinates.set(newCoordinates);
        updateBorders();
    }

    /**
     * Button constructor
     * @param icon buttons icon which should be rendered, icon type should be DefaultGuiObject
     * @param obj object where a method is located
     * @param method Method, which will be called when the button is clicked.
     *               Shouldn't be returning any value. Should have 2 parameters:
     *                   * Param Object which will be set with this class
     *                   * Param int id, special number which you can add in constructor to identify caller
     */
    public Button(@NotNull Object icon, @NotNull Vector2f coordinates,
                  @NotNull Object obj, @NotNull Method method, int id, int zLevel, ArrayList<EnhancedGuiScreen.Bind> binds) {
        super(coordinates,new Vector2f(1.0f, 1.0f),((DefaultGuiObject)icon).getSize(), false);
        this.icon = icon;
        this.obj = obj;
        this.method = method;
        this.binds = binds;
        this.zLevel = zLevel;
        this.id = id;
        updateBorders();
    }

    @Override
    public void render() {
        ((DefaultGuiObject)icon).render(this.coordinates);
    }

    @Override
    public void render(@NotNull Vector2f coordinates) {
        ((DefaultGuiObject)icon).render(new Vector2f(
                coordinates.x + this.coordinates.x,
                coordinates.y + this.coordinates.y
        ));
    }

    @Override
    public boolean scalable() {
        return false;
    }

    @Override
    public void render(@NotNull Vector2f coordinates, Vector2f scale) {
        ((DefaultGuiObject)icon).render(coordinates);
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
            method.invoke(obj, this, id);
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    @Override
    public ArrayList<EnhancedGuiScreen.Bind> getBinds() {
        return binds;
    }

    @Override
    public void setParent(Collection parent) {
        this.parent = parent;
    }
}
