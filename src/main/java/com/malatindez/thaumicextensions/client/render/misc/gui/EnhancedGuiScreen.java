package com.malatindez.thaumicextensions.client.render.misc.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

@SuppressWarnings({"EmptyMethod", "unchecked"})
public abstract class EnhancedGuiScreen extends GuiScreen {
    static class Bind {
        protected int key;
        protected final Method method;
        protected final Object obj;

        /**
         * @param key Key which we should press to invoke method of an object
         * @param method Method without any returning value and parameters either
         * @param obj Object, where method is located.
         */
        public Bind(int key, Method method, Object obj) {
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
            LMB         (1),
            RMB         (2),
            MMB         (4);
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
        void updateParentBorders(Vector4f parentCoordinates);

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
    public interface Inputable {
        boolean keyTyped(char par1, int par2);
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
    protected boolean keyTyped1(char par1, int par2) {
        if(gui.keyTyped(par1, par2)) {
            return true;
        }
        super.keyTyped(par1, par2);
        return false;
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
        gui.Update(flags);
        gui.render();
        this.gui.generateJSONObject();
    }
    protected void mouseClicked(int mx, int my, int button) {
        gui.mouseClicked(new Vector2f(mx,my), button);
    }
    @SuppressWarnings("rawtypes")
    public static final HashMap<String, Class> parts = new HashMap<String, Class>(){{
        put("Collection", Collection.class);
        put("ContextMenuField", ContextMenuField.class);
        put("ScrollBar", ScrollBar.class);
        put("Button", Button.class);
        put("Drag", Drag.class);
        put("Icon", IconFactory.Icon.class);
        put("Rect", Rect.class);
        put("TextBox", TextBox.class);
        put("TextLine", TextLine.class);
        put("TextInputBox", TextInputBox.class);
        put("TextInputLine", TextInputLine.class);
        // type name and class instance which will be constructed
    }};
    public static final void addPart(Class c) {
        parts.put(c.getSimpleName(), c);
    }
    public static DefaultGuiObject createObject(String name, Object parent, JSONObject object) {
        if(object.containsKey("type")) {
            String type = (String)object.get("type");
            if (parts.containsKey(type)) {
                try {
                    Constructor<?> constructor = parts.get(type).getConstructor(String.class, Object.class, JSONObject.class);
                    return (DefaultGuiObject) constructor.newInstance(new Object[] {name, parent, object});
                } catch(Exception exception) {
                    System.out.println("Exception caught! Failed to construct " + name);
                    System.out.println("Parent:" + parent.getClass().getSimpleName());
                    System.out.println("JSONObject: " + object.toString());
                    System.out.println("Stack trace: ");
                    exception.printStackTrace();
                }
            }
        }
        System.out.println("Something went wrong in createObject.");
        System.out.println("Parent:" + parent.getClass().getSimpleName());
        System.out.println("JSONObject: " + object.toString());
        System.out.println("Returning null.");
        return null;
    }
    public static Collection loadFromFile(Object parent, ResourceLocation json_file) {
        InputStream x;
        try {
            x = Minecraft.getMinecraft().getResourceManager().getResource(json_file).getInputStream();
        } catch (Exception e) {
            System.out.println("Exception caught! Wrong ResourceFile for IconFactory.");
            System.out.println(json_file);
            e.printStackTrace();
            return null;
        }
        InputStreamReader isReader = new InputStreamReader(x);
        //Creating a BufferedReader object
        BufferedReader reader = new BufferedReader(isReader);
        StringBuilder sb = new StringBuilder();
        String str;
        try {
            while ((str = reader.readLine()) != null) {
                sb.append(str);
            }
        } catch (Exception ignored) { }
        str = sb.toString();
        JSONObject jsonObject = (JSONObject) JSONValue.parse(str);
        Object main = null;
        for(Object obj : jsonObject.keySet()) {
            main = obj; break;
        }
        Collection a = (Collection) createObject((String)main, parent, (JSONObject) jsonObject.get(main));
        //noinspection ConstantConditions
        a.postInit();
        return a;
    }
    protected EnhancedGuiScreen() {}
    protected EnhancedGuiScreen(ResourceLocation json_file) {
        this.gui = loadFromFile(this, json_file);
    }
}