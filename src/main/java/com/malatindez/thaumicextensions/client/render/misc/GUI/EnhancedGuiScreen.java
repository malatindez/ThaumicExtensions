package com.malatindez.thaumicextensions.client.render.misc.GUI;

import com.sun.istack.internal.NotNull;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;
import scala.collection.script.Update;

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
        // if bit is on than that button is currently pressed
        // a - LMB              (0b0000000001)
        // 9 - RMB              (0b0000000010)
        // 8 - MMB              (0b0000000100)
        // 7 - Return           (0b0000001000)
        // 6 - Space            (0b0000010000)
        // 5 - Backspace        (0b0000100000)
        // 4 - Left arrow key   (0b0001000000)
        // 3 - Right arrow key  (0b0010000000)
        // 2 - Up arrow key     (0b0100000000)
        // 1 - Down arrow key   (0b1000000000)
        void Update(int flags);
        enum Flags {
            LMB         (0b1),
            RMB         (0b10),
            MMB         (0b100),
            Return      (0b1000),
            Space       (0b10000),
            Backspace   (0b100000),
            LeftArrow   (0b1000000),
            RightArrow  (0b10000000),
            UpArrow     (0b100000000),
            DownArrow   (0b1000000000);
            private final int type;
            public int getType() {
                return type;
            }
            Flags(int i) {
                this.type = i;
            }
        }
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
         * mouse click handler function.
         * @param currentMousePosition current mouse position
         * @return boolean, if true that means that mouse was handled
         * Otherwise, we should work with Clickable object below this.
         */
        boolean mouseClicked(Vector2f currentMousePosition, int button);

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
         * This function should receive parent coordinates if they were updated.
         * You should use this to reposition rendering
         * This function is defined in DefaultGuiObject but
         *                          every object which has descendants should update their coordinates either
         */
        void updateParentCoordinates(Vector2f parentCoordinates);

        /**
         * @return if object can be scaled
         */
        boolean scalable();

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
        int flags = 0;
        if(Mouse.isButtonDown(0))                   flags |= Updatable.Flags.LMB.getType();
        if(Mouse.isButtonDown(1))                   flags |= Updatable.Flags.RMB.getType();
        if(Mouse.isButtonDown(2))                   flags |= Updatable.Flags.MMB.getType();
        if(Keyboard.isKeyDown(Keyboard.KEY_RETURN)) flags |= Updatable.Flags.Return.getType();
        if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))  flags |= Updatable.Flags.Space.getType();
        if(Keyboard.isKeyDown(Keyboard.KEY_BACK))   flags |= Updatable.Flags.Backspace.getType();
        if(Keyboard.isKeyDown(Keyboard.KEY_LEFT))   flags |= Updatable.Flags.LeftArrow.getType();
        if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))  flags |= Updatable.Flags.RightArrow.getType();
        if(Keyboard.isKeyDown(Keyboard.KEY_UP))     flags |= Updatable.Flags.UpArrow.getType();
        if(Keyboard.isKeyDown(Keyboard.KEY_DOWN))   flags |= Updatable.Flags.DownArrow.getType();
        gui.Update(flags);
        gui.render();
    }
    protected void mouseClicked(int mx, int my, int button) {
        gui.mouseClicked(new Vector2f(mx,my), button);
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