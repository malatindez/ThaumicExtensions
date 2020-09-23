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
        updateVectors();
    }



    @Override
    public void render() {
        ((DefaultGuiObject)icon).render();
    }

    @Override
    public boolean scalable() {
        return false;
    }

    @Override
    public int getZLevel() {
        return zLevel;
    }

    @Override
    protected void VectorsWereUpdated() {
        ((DefaultGuiObject)icon).updateParentCoordinates(this.getCurrentPosition());
    }

    @Override
    public boolean mouseHandler(@NotNull Vector2f currentMousePosition) {
        return true;
    }

    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {
        if(button == 0) {
            try {
                method.invoke(obj, this, id);
            } catch (Exception ignored) {
                return false;
            }
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
