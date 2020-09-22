package com.malatindez.thaumicextensions.client.render.misc.GUI;

import com.sun.istack.internal.NotNull;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class EnhancedGuiScreen extends GuiScreen {
    static class Bind {
        protected int key;
        protected final Method method;
        protected final Object obj;

        /**
         * @param key Key which we should press to invoke method of an object
         * @param method Method without any returning value and parameters either
         * @param obj Object, where method is located.
         */
        public Bind(int key, @NotNull Method method, @NotNull Object obj) {
            this.method = method;
            this.obj = obj;
        }
        public int getKey() {
            return key;
        }
        /**
         * @return boolean if invoke was successful
         */
        public boolean invoke() {
            try {
                this.method.invoke(obj);
            } catch (Exception ignored) {
                return false;
            }
            return true;
        }
    }
    public interface needParent {
        void setParent(Collection parent);
    }
    public interface Updatable  {
        void Update();
    }
    public interface Clickable {
        /**
         * This function should return the top left and the bottom right corners of an object in Vector4f.
         * Where (x1;y1) are the coordinates of top left corner
         * Where (x2;y2) are the coordinates of bottom right corner
         * @return Vector4f(x1,y1,x2,y2)
         */
        Vector4f getBorders();

        /**
         * mouse handler function.
         * @param currentMousePosition current mouse position
         * @return boolean, if true that means that mouse was handled
         * Otherwise, we should work with Clickable object below this.
         */
        boolean mouseHandler(Vector2f currentMousePosition);

        /**
         * Default resolution is 1024 x 768
         * This function is called when resolution was updated.
         * This can be used to update the coordinates and/or scale of the object
         * @param newResolution new resolution(1920.0f, 1080.0f for example)
         */
        void resolutionUpdated(Vector2f newResolution);

        /**
         * Returns Z level of an object which used to handle the mouse
         * @return Z level
         */
        int getZLevel();
    }
    public interface Bindable {
        /**
         * Get keyboard binds
         * @return keyboard binds
         */
        ArrayList<Bind> getBinds();
    }
    public interface Renderable {
        /**
         * Default render function, coordinates depends on object implementation.
         */
        void render();

        /**
         * Render object
         * @param coordinates top left corner offset
         */
        void render(Vector2f coordinates);

        /**
         * @return if object can be scaled
         */
        boolean scalable();

        /**
         * If scalable() is false - scale parameter will be ignored.
         * @param coordinates top left corner offset
         * @param scale scale object
         */
        void render(Vector2f coordinates, Vector2f scale);

        /**
         * Default resolution is 1024 x 768
         * This function is called when screen resolution was updated.
         * This can be used to update the coordinates and/or scale of the object
         * @param newResolution new resolution(1920.0f, 1080.0f for example)
         */
        void resolutionUpdated(Vector2f newResolution);

        /**
         * Returns Z level of an object which used to handle the mouse
         * @return Z level
         */
        int getZLevel();
    }
    protected final Vector2f mousePosition = new Vector2f(0,0);
    public Vector2f getCurrentMousePosition() {
        return new Vector2f(mousePosition);
    }
    public Vector2f getCurrentResolution() {
        return new Vector2f(currentResolution);
    }
    protected Vector2f currentResolution = new Vector2f(DefaultGuiObject.defaultResolution);
    protected Collection gui;
    public void onGuiClosed() {
        super.onGuiClosed();
    }
    public void drawScreen(int mx, int my, float tick) {
        mousePosition.set(mx,my);
        if (currentResolution.x != this.width || currentResolution.y != this.height) {
            Vector2f newResolution = new Vector2f(this.width, this.height);
            gui.resolutionUpdated(newResolution);
            currentResolution = newResolution;
        }
        gui.mouseHandler(new Vector2f(mousePosition));
        gui.render();
    }
/*
    public static class TextLine implements Renderable {
        protected final Vector2f coordinates, scale;
        protected final String text;
        public TextLine(Vector2f coordinates, Vector2f scale, String text) {
            this.coordinates = new Vector2f(coordinates);
            this.scale = new Vector2f(scale);
            this.text = new String(text);
        }
        @Override
        public void render() {

        }

        @Override
        public void render(Vector2f coordinates) {

        }

        @Override
        public boolean scalable() {
            return true;
        }

        @Override
        public void render(Vector2f coordinates, Vector2f scale) {
 
        }
    }

    public static class GradientRect implements Renderable {

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
    }
*/
}