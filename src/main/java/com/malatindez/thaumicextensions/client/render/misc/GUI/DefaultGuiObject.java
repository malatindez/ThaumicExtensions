package com.malatindez.thaumicextensions.client.render.misc.GUI;

import net.minecraft.client.Minecraft;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import scala.reflect.macros.NonemptyAttachments;
import scala.tools.nsc.doc.model.Def;
import scala.util.hashing.Hashing;

import java.lang.reflect.Method;

public abstract class DefaultGuiObject implements EnhancedGuiScreen.Renderable, Comparable {
    private final Vector2f coordinates;
    private final Vector2f scale;
    private final Vector2f size;
    private final Vector2f parentCoordinates = new Vector2f(0,0);
    private final Vector4f parentBorders = new Vector4f(0,0,0,0);
    private final String name;
    protected final Vector2f currentResolution = new Vector2f(427, 240);
    public static final Vector2f defaultResolution = new Vector2f(427, 240);
    private final Vector4f borders;
    protected int zLevel;
    private final Vector2f currentObjectPosition = new Vector2f(0,0);
    private Object parent;
    class MethodObjectPair {
        public Method method;
        public Object object;
        public MethodObjectPair(Object object, Method method) {
            this.method = method;
            this.object = object;
        }

    }
    protected MethodObjectPair getMethodFunc(String objectName, String name, Class[] parameterTypes) {
        try {
            return new MethodObjectPair(this, this.getClass().getMethod(name, parameterTypes));
        } catch (NoSuchMethodException e) {
            System.out.println("Method" + name + " wasn't found in " + objectName);
            System.out.println(parameterTypes);
            return null;
        }
    }
    protected MethodObjectPair getMethodUp(String objectName, String name, Class[] parameterTypes) {
        if(objectName == this.name) {
            getMethodFunc(objectName, name, parameterTypes);
        }
        if(parent instanceof DefaultGuiObject) {
            return ((DefaultGuiObject)parent).getMethodUp(objectName, name, parameterTypes);
        } else {
            try {
                return new MethodObjectPair(parent, parent.getClass().getMethod(name, parameterTypes));
            } catch (Exception ignored) {}
        }
        return getMethodDown(objectName, name, parameterTypes);
    }
    public abstract MethodObjectPair getMethodDown(String objectName, String name, Class[] parameterTypes);

    protected Object getObjectUp(String objectName) {
        if(objectName == this.name) {
            return this;
        }
        if(parent instanceof DefaultGuiObject) {
            return ((DefaultGuiObject)parent).getObjectUp(objectName);
        }
        return getObjectDown(objectName);
    }
    public abstract Object getObjectDown(String objectName);

    public static final int Vector4fToColor(Vector4f vec) {
        return Integer.parseInt(
        Integer.toHexString((int)(vec.w*255.0f)) +
        Integer.toHexString((int)(vec.x*255.0f)) +
        Integer.toHexString((int)(vec.y*255.0f)) +
        Integer.toHexString((int)(vec.z*255.0f)), 16);
    }
    public static final Vector2f Json2Vec(Object array) {
        return new Vector2f(
                Float.parseFloat(((JSONArray)array).get(0).toString()),
                Float.parseFloat(((JSONArray)array).get(1).toString()));
    }
    public static final Vector3f Json3Vec(Object array) {
        return new Vector3f(
                Float.parseFloat(((JSONArray)array).get(0).toString()),
                Float.parseFloat(((JSONArray)array).get(1).toString()),
                Float.parseFloat(((JSONArray)array).get(2).toString()));
    }
    public static final Vector4f Json4Vec(Object array) {
        return new Vector4f(
                Float.parseFloat(((JSONArray)array).get(0).toString()),
                Float.parseFloat(((JSONArray)array).get(1).toString()),
                Float.parseFloat(((JSONArray)array).get(2).toString()),
                Float.parseFloat(((JSONArray)array).get(3).toString()));
    }
    public static final float JsonToFloat(Object object) {
        return Float.parseFloat(object.toString());
    }
    public Object getParent() {
        return parent;
    }
    // this function will be called when vectors were updated
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
    public Vector4f getParentBorders() {
        return new Vector4f(parentBorders);
    }
    public String getName() {return name;}
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

    enum ResolutionRescaleType {
        NONE,
        SCALE_X,
        SCALE_Y,
        SCALE_XY,
        SCALE_SMOOTH_XY;
    }
    private ResolutionRescaleType type;
    public ResolutionRescaleType getType() {
        return type;
    }
    public void setType(ResolutionRescaleType type) {
        type = type;
    }
    public abstract void preInit(String name, Object parent, JSONObject parameters);
    // postInit is called after entire gui is loaded
    public abstract void postInit();
    /**
     *
     * @param coordinates
     * @param scale
     * @param size
     * @param zLevel
     */
    public DefaultGuiObject(String name, Object parent, Vector2f coordinates, Vector2f scale, Vector2f size,
                            int zLevel, ResolutionRescaleType type) {
        this.coordinates = new Vector2f(coordinates);
        this.currentObjectPosition.set(coordinates);
        this.scale =  new Vector2f(1,1);
        this.size =  new Vector2f(size);
        this.borders = new Vector4f();
        this.zLevel = zLevel;
        this.type = type;
        this.name = name;
        this.parent = parent;
        this.reScale(scale);
    }
    public DefaultGuiObject(String name, Object parent, JSONObject parameters) {
        preInit(name,parent,parameters);
        this.name = name;
        this.parent = parent;
        this.coordinates = new Vector2f(0, 0);
        if(parameters.containsKey("coordinates")) {
            this.coordinates.set(Json2Vec(parameters.get("coordinates")));
        }
        this.currentObjectPosition.set(coordinates);
        this.borders = new Vector4f();
        this.scale = new Vector2f(1,1);
        Vector2f scale = new Vector2f(1, 1);
        if(parameters.containsKey("scale")) {
            scale = Json2Vec(parameters.get("scale"));
        }
        this.size = new Vector2f(0, 0);
        if(parameters.containsKey("size")) {
            this.size.set(Json2Vec(parameters.get("size")));
        }
        zLevel = 0;
        if(parameters.containsKey("zLevel")) {
            zLevel = (int) Float.parseFloat(parameters.get("zLevel").toString());
        }
        type = ResolutionRescaleType.SCALE_SMOOTH_XY;
        if(parameters.containsKey("scale_type")) {
            String scale_type = (String)parameters.get("scale_type");
            if (scale_type.equals("none")) {
                type = ResolutionRescaleType.NONE;
            } else if(scale_type.equals("scale_x")) {
                type = ResolutionRescaleType.SCALE_X;
            } else if(scale_type.equals("scale_y")) {
                type = ResolutionRescaleType.SCALE_Y;
            } else if(scale_type.equals("scale_xy")) {
                type = ResolutionRescaleType.SCALE_XY;
            } else if(scale_type.equals("scale_smooth_xy")) {
                type = ResolutionRescaleType.SCALE_SMOOTH_XY;
            }
        }
        this.reScale(scale);
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
        float deltaX = 1, deltaY = 1;
        switch (type) {
            case SCALE_X:
                deltaX = newResolution.x / currentResolution.x;
                break;
            case SCALE_Y:
                deltaY = newResolution.y / currentResolution.y;
                break;
            case SCALE_XY:
                deltaY = newResolution.y / currentResolution.y;
                deltaX = newResolution.x / currentResolution.x;
                break;
            case SCALE_SMOOTH_XY:
                deltaX = deltaY =
                        (float) Math.sqrt(newResolution.x / currentResolution.x * newResolution.y / currentResolution.y);
                break;
        }
        reScale(deltaX, deltaY);
        currentResolution.set(newResolution);
    }
    @Override
    public void updateParentBorders(Vector4f parentBorders) {
        this.parentCoordinates.set(parentBorders.x, parentBorders.y);
        this.parentBorders.set(parentBorders);  updateVectors();
    }
    public int compareTo(Object o) {
        if (o instanceof DefaultGuiObject) {
            return this.getZLevel() - ((DefaultGuiObject) o).getZLevel();
        }
        return this.getZLevel();
    }
}