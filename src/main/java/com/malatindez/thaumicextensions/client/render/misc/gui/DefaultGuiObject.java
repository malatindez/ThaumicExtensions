package com.malatindez.thaumicextensions.client.render.misc.gui;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

@SuppressWarnings("unchecked")
public abstract class DefaultGuiObject implements EnhancedGuiScreen.Renderable, Comparable<DefaultGuiObject> {
    public static class MethodObjectPair {
        public final Method method;
        public final Object object;
        public MethodObjectPair(Object object, Method method) {
            this.method = method;
            this.object = object;
        }
    }

    enum ResolutionRescaleType {
        NONE("none"),
        SCALE_X("scale_x"),
        SCALE_Y("scale_y"),
        SCALE_SMOOTH_X("scale_smooth_x"),
        SCALE_SMOOTH_Y("scale_smooth_y"),
        SCALE_XY("scale_xy"),
        SCALE_SMOOTH_XY("scale_smooth_xy");
        private final String a;
        public String toString() {
            return a;
        }
        ResolutionRescaleType(String a) {
            this.a = a;
        }
    }

    private class ObjectComparator implements Comparator<DefaultGuiObject> {
        @Override
        public int compare(DefaultGuiObject x, DefaultGuiObject y) {
            if(x == null) {
                return -1;
            } else if (y == null) {
                return 1;
            }
            return x.getZLevel() - y.getZLevel();
        }
    }

    private JSONObject startupParameters;

    private String name;

    private Object parent;

    private Vector2f coordinates;
    private Vector2f scale;
    private Vector2f size;
    private Vector4f borders;
    protected int zLevel;

    protected final Vector2f currentResolution = new Vector2f(427, 240);
    public static final Vector2f defaultResolution = new Vector2f(427, 240);

    private final Vector2f parentCoordinates = new Vector2f(0,0);
    private final Vector4f parentBorders = new Vector4f(0,0,0,0);
    private final Vector2f currentObjectPosition = new Vector2f(0,0);

    private ResolutionRescaleType sizeRescaleType;
    private ResolutionRescaleType coordinatesRescaleType;

    private boolean hide = false;

    protected ArrayList<DefaultGuiObject> descendants;

    public ArrayList<DefaultGuiObject> getDescendants() {
        return descendants;
    }

    private ObjectComparator objectComparator;


    public boolean hided() {
        return hide;
    }
    public Object getParent() {
        return parent;
    }
    public Vector2f getCoordinates() {
        return new Vector2f(coordinates);
    }
    public Vector2f getScale() {
        return new Vector2f(scale);
    }
    public Vector2f getSize() {
        return new Vector2f(size);
    }
    public Vector2f getParentCoordinates() {
        return new Vector2f(parentCoordinates);
    }
    public Vector4f getParentBorders() {
        return new Vector4f(parentBorders);
    }
    public String getName() {
        return name;
    }
    public Vector4f getBorders() {
        return new Vector4f(borders);
    }
    public Vector2f getCurrentPosition() {
        return new Vector2f(currentObjectPosition);
    }
    public ResolutionRescaleType getSizeRescaleType() {
        return sizeRescaleType;
    }
    public ResolutionRescaleType getCoordinatesRescaleType() {
        return coordinatesRescaleType;
    }
    public JSONObject getStartupParameters() {
        return startupParameters;
    }


    // You shouldn't use this function. This function will have been used during gui editing.
    // Otherwise, you can break GUI
    protected void setName(String name) {
        this.name = name;
    }


    public void hide() {
        hide = true;
    }
    public void show() {
        hide = false;
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
    public void setSizeRescaleType(ResolutionRescaleType type) {
        this.sizeRescaleType = type;
    }
    public void setCoordinatesRescaleType(ResolutionRescaleType type) {
        this.coordinatesRescaleType = type;
    }




    protected void sortObjects() {
        Collections.sort(descendants, objectComparator);
        Collections.reverse(descendants);
    }

    @SuppressWarnings("UnusedReturnValue")
    public Object addObject(DefaultGuiObject object) {
        if(object == null) {
            return null;
        }
        object.updateParentBorders(getBorders());
        object.resolutionUpdated(this.currentResolution);
        descendants.add(object); sortObjects();
        return object;
    }




    protected boolean keyTypedDescendants(char par1, int par2) {
        if(hided()) {
            return false;
        }
        for(Object object : descendants) {
            if(object instanceof EnhancedGuiScreen.Inputable &&
                    ((EnhancedGuiScreen.Inputable) object).keyTyped(par1, par2)) {
                return true;
            }
        }
        return false;
    }

    protected void updateDescendants(int flags) {
        for(Object object : descendants) {
            if (object instanceof EnhancedGuiScreen.Updatable) {
                ((EnhancedGuiScreen.Updatable) object).Update(flags);
            }
        }
    }

    protected boolean mouseHandlerDescendants(Vector2f currentMousePosition) {
        if(hided()) {
            return false;
        }
        for(DefaultGuiObject object : descendants) {
            if (object instanceof EnhancedGuiScreen.Clickable) {
                ((EnhancedGuiScreen.Clickable) object).mouseHandler(currentMousePosition);
            }
        }
        return false;
    }

    protected boolean mouseClickedDescendants(Vector2f currentMousePosition, int button)  {
        if(hided()) {
            return false;
        }
        for(DefaultGuiObject
                object : descendants) {
            if (object instanceof EnhancedGuiScreen.Clickable) {
                if(((EnhancedGuiScreen.Clickable) object).mouseClicked(currentMousePosition, button)) {
                    return true;
                }
            }
        }
        return false;
    }







    // postInit is called after entire gui is loaded
    public void postInit() {
        for(DefaultGuiObject descendant : descendants) {
            descendant.postInit();
        }
    }


    // this function will be called when vectors were updated
    protected void VectorsWereUpdated() {
        for(DefaultGuiObject object : descendants) {
            object.updateParentBorders(getBorders());
        }
    }


    protected void updateVectors() {
        Vector2f.add(parentCoordinates, coordinates, currentObjectPosition);
        this.borders.set(
                currentObjectPosition.x, currentObjectPosition.y,
                currentObjectPosition.x + size.x, currentObjectPosition.y + size.y
        );
        VectorsWereUpdated();
    }


    @SuppressWarnings("UnusedReturnValue")
    public boolean checkBorders() {
        if(!(parent instanceof DefaultGuiObject)) {
            return false;
        }
        Vector4f borders = getBorders();
        Vector4f parentBorders = getParentBorders();
        if(parentBorders.z - parentBorders.x < borders.z - borders.x ||
                parentBorders.w - parentBorders.y < borders.w - borders.y) {
            return false;
        }
        boolean flag = false;
        if(borders.z > parentBorders.z) {
            coordinates.set(coordinates.x + parentBorders.z - borders.z, coordinates.y);
            flag = true;
        }
        if(borders.w > parentBorders.w) {
            coordinates.set(coordinates.x, coordinates.y + parentBorders.w - borders.w);
            flag = true;
        }
        if(borders.x < parentBorders.x) {
            coordinates.set(0, coordinates.y);
            flag = true;
        }
        if(borders.y < parentBorders.y) {
            coordinates.set(coordinates.x, 0);
            flag = true;
        }
        if(flag) {
            updateVectors();
        }
        return flag;
    }


    public Vector2f getDeltas(Vector2f newResolution, ResolutionRescaleType type) {
        Vector2f delta = new Vector2f(1,1);
        switch (type) {
            case SCALE_X:
                delta.x = newResolution.x / currentResolution.x;
                break;
            case SCALE_Y:
                delta.y = newResolution.y / currentResolution.y;
                break;
            case SCALE_SMOOTH_X:
                delta.x = (float) Math.sqrt(newResolution.x / currentResolution.x * newResolution.y / currentResolution.y);
                break;
            case SCALE_SMOOTH_Y:
                delta.y = (float) Math.sqrt(newResolution.x / currentResolution.x * newResolution.y / currentResolution.y);
                break;
            case SCALE_XY:
                delta.y = newResolution.y / currentResolution.y;
                delta.x = newResolution.x / currentResolution.x;
                break;
            case SCALE_SMOOTH_XY:
                delta.x = delta.y =
                        (float) Math.sqrt(newResolution.x / currentResolution.x * newResolution.y / currentResolution.y);
                break;
        }
        return delta;
    }

    public JSONObject generateJSONObject() {
        JSONObject returnValue = new JSONObject();
        returnValue.put(getName(), new JSONObject());
        JSONObject a = (JSONObject) returnValue.get(getName());
        Vector2f delta = getDeltas(defaultResolution, coordinatesRescaleType);
        Vector2f coordinates = new Vector2f(this.coordinates.x * delta.x, this.coordinates.y * delta.y);
        delta = getDeltas(defaultResolution, sizeRescaleType);
        Vector2f scale = new Vector2f(this.scale.x * delta.x, this.scale.y * delta.y);
        Vector2f size = new Vector2f(this.size.x * delta.x, this.size.y * delta.y);
        for(String part : EnhancedGuiScreen.parts.keySet()) {
            if(this.getClass().getName().equals(EnhancedGuiScreen.parts.get(part).getName())) {
                a.put("type", part);
                break;
            }
        }
        a.put("coordinates", VecToJson(coordinates));
        a.put("scale", VecToJson(scale));
        a.put("size", VecToJson(size));
        a.put("zLevel", (long)zLevel);
        a.put("hided", hide);
        a.put("size_scale_type", sizeRescaleType.toString());
        a.put("coordinates_scale_type", sizeRescaleType.toString());
        return returnValue;
    }

    public void loadFromJSONObject(JSONObject parameters) {
        startupParameters = parameters;
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
        } else if(parent instanceof DefaultGuiObject) {
            this.size.set(((DefaultGuiObject) parent).getSize());
        }
        zLevel = 0;
        if(parameters.containsKey("zLevel")) {
            zLevel = (int) Float.parseFloat(parameters.get("zLevel").toString());
        }
        if(parameters.containsKey("hided")) {
            hide = (Boolean)parameters.get("hided");
        }
        if(parent instanceof DefaultGuiObject) {
            sizeRescaleType = ((DefaultGuiObject) parent).sizeRescaleType;
        } else {
            sizeRescaleType = ResolutionRescaleType.NONE;
        }
        if(parameters.containsKey("size_scale_type")) {
            String scale_type = (String)parameters.get("size_scale_type");
            if (scale_type.equals("none")) {
                sizeRescaleType = ResolutionRescaleType.NONE;
            } else if(scale_type.equals("scale_x")) {
                sizeRescaleType = ResolutionRescaleType.SCALE_X;
            } else if(scale_type.equals("scale_y")) {
                sizeRescaleType = ResolutionRescaleType.SCALE_Y;
            }else if(scale_type.equals("scale_smooth_x")) {
                sizeRescaleType = ResolutionRescaleType.SCALE_SMOOTH_X;
            } else if(scale_type.equals("scale_smooth_y")) {
                sizeRescaleType = ResolutionRescaleType.SCALE_SMOOTH_Y;
            } else if(scale_type.equals("scale_xy")) {
                sizeRescaleType = ResolutionRescaleType.SCALE_XY;
            } else if(scale_type.equals("scale_smooth_xy")) {
                sizeRescaleType = ResolutionRescaleType.SCALE_SMOOTH_XY;
            }
        }
        if(parent instanceof DefaultGuiObject) {
            coordinatesRescaleType = ((DefaultGuiObject) parent).coordinatesRescaleType;
        } else {
            coordinatesRescaleType = ResolutionRescaleType.NONE;
        }
        if(parameters.containsKey("coordinates_scale_type")) {
            String scale_type = (String)parameters.get("coordinates_scale_type");
            if (scale_type.equals("none")) {
                coordinatesRescaleType = ResolutionRescaleType.NONE;
            } else if(scale_type.equals("scale_x")) {
                coordinatesRescaleType = ResolutionRescaleType.SCALE_X;
            } else if(scale_type.equals("scale_y")) {
                coordinatesRescaleType = ResolutionRescaleType.SCALE_Y;
            }else if(scale_type.equals("scale_smooth_x")) {
                coordinatesRescaleType = ResolutionRescaleType.SCALE_SMOOTH_X;
            } else if(scale_type.equals("scale_smooth_y")) {
                coordinatesRescaleType = ResolutionRescaleType.SCALE_SMOOTH_Y;
            } else if(scale_type.equals("scale_xy")) {
                coordinatesRescaleType = ResolutionRescaleType.SCALE_XY;
            } else if(scale_type.equals("scale_smooth_xy")) {
                coordinatesRescaleType = ResolutionRescaleType.SCALE_SMOOTH_XY;
            }
        }
        this.reScale(scale);
    }


    public DefaultGuiObject(String name, Object parent, JSONObject parameters) {
        this.descendants = new ArrayList<DefaultGuiObject>();
        this.name = name;
        this.parent = parent;
        loadFromJSONObject(parameters);
    }



    @Override
    public int getZLevel() {
        return zLevel;
    }

    @Override
    public void render() {
        if(hided()) {
            return;
        }
        for(int i = descendants.size() - 1; i >= 0; i--) {
            try {
                descendants.get(i).render();
            } catch (Exception e) {
                System.out.println("[DEBUG] Caught an exception during rendering. Stacktrace: ");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void resolutionUpdated(Vector2f newResolution) {
        Vector2f delta = getDeltas(newResolution, coordinatesRescaleType);
        setCoordinates(
                this.coordinates.x * delta.x,
                this.coordinates.y * delta.y
        );
        delta = getDeltas(newResolution, sizeRescaleType);
        reScale(delta.x, delta.y);
        currentResolution.set(newResolution);
        checkBorders();
        for(DefaultGuiObject object : descendants) {
            object.resolutionUpdated(newResolution);
        }
    }

    @Override
    public void updateParentBorders(Vector4f parentBorders) {
        this.parentCoordinates.set(parentBorders.x, parentBorders.y);
        this.parentBorders.set(parentBorders);  updateVectors();
    }




    @SuppressWarnings("rawtypes")
    MethodObjectPair getMethod(JSONObject parameters, String methodName, Class[] methodParameters) {
        if(parameters.containsKey(methodName)) {
            JSONObject obj = (JSONObject) parameters.get(methodName);
            return this.getMethodUp(
                    (String) obj.get("object_name"),
                    (String) obj.get("method_name"),
                    methodParameters);
        }
        return null;
    }




    @SuppressWarnings({"UnusedReturnValue", "rawtypes"})
    protected MethodObjectPair getMethodFunc(String objectName, String name, Class[] parameterTypes) {
        try {
            return new MethodObjectPair(this, this.getClass().getMethod(name, parameterTypes));
        } catch (NoSuchMethodException e) {
            System.out.println("Method" + name + " wasn't found in " + objectName);
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    protected MethodObjectPair getMethodUp(String objectName, String name, Class[] parameterTypes) {
        if(this.getName().equals(objectName)) {
            return getMethodFunc(objectName, name, parameterTypes);
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

    @SuppressWarnings("rawtypes")
    public MethodObjectPair getMethodDown(String objectName, String name, Class[] parameterTypes) {
        if(objectName.equals(this.getName())) {
            getMethodFunc(objectName, name, parameterTypes);
        }
        MethodObjectPair retValue = null;
        for(Object obj : descendants) {
            if(retValue != null) {
                return retValue;
            }
            retValue = ((DefaultGuiObject)obj).getMethodDown(objectName, name, parameterTypes);
        }
        return retValue;
    }




    protected Object getObjectUp(String objectName) {
        if(this.getName().equals(objectName)) {
            return this;
        }
        if(parent instanceof DefaultGuiObject) {
            return ((DefaultGuiObject)parent).getObjectUp(objectName);
        }
        return getObjectDown(objectName);
    }

    public Object getObjectDown(String objectName) {
        if(objectName.equals(this.getName())) {
            return this;
        }
        Object retValue = null;
        for(Object obj : descendants) {
            if(retValue != null) {
                return retValue;
            }
            retValue = ((DefaultGuiObject)obj).getObjectDown(objectName);
        }
        return retValue;
    }




    public int compareTo(DefaultGuiObject o) {
        return this.getZLevel() - o.getZLevel();
    }




    public static void putMethod(JSONObject objRef, String name, MethodObjectPair pair) {
        if(pair == null || objRef == null) {
            return;
        }
        JSONObject x = new JSONObject();
        String objectName = "none";
        if(pair.object instanceof DefaultGuiObject) {
            objectName = ((DefaultGuiObject) pair.object).getName();
        }
        x.put("object_name", objectName);
        x.put("method_name", pair.method.getName());
        objRef.put(name, x);
    }
    public static long Vector4fToColor(Vector4f vec) {
        return Long.parseLong(
                Integer.toHexString((int)(vec.w*255.0f)) +
                        Integer.toHexString((int)(vec.x*255.0f)) +
                        Integer.toHexString((int)(vec.y*255.0f)) +
                        Integer.toHexString((int)(vec.z*255.0f)), 16);
    }
    public static Vector2f Json2Vec(Object array) {
        return new Vector2f(
                Float.parseFloat(((JSONArray)array).get(0).toString()),
                Float.parseFloat(((JSONArray)array).get(1).toString()));
    }
    public static Vector3f Json3Vec(Object array) {
        return new Vector3f(
                Float.parseFloat(((JSONArray)array).get(0).toString()),
                Float.parseFloat(((JSONArray)array).get(1).toString()),
                Float.parseFloat(((JSONArray)array).get(2).toString()));
    }
    public static Vector4f Json4Vec(Object array) {
        return new Vector4f(
                Float.parseFloat(((JSONArray)array).get(0).toString()),
                Float.parseFloat(((JSONArray)array).get(1).toString()),
                Float.parseFloat(((JSONArray)array).get(2).toString()),
                Float.parseFloat(((JSONArray)array).get(3).toString()));
    }
    public static JSONArray VecToJson(Vector2f vec) {
        JSONArray ja = new JSONArray();
        ja.add(vec.x);
        ja.add(vec.y);
        return ja;
    }
    public static JSONArray VecToJson(Vector3f vec) {
        JSONArray ja = new JSONArray();
        ja.add(vec.x);
        ja.add(vec.y);
        ja.add(vec.z);
        return ja;
    }
    public static JSONArray VecToJson(Vector4f vec) {
        JSONArray ja = new JSONArray();
        ja.add(vec.x);
        ja.add(vec.y);
        ja.add(vec.z);
        ja.add(vec.w);
        return ja;
    }
    public static float JsonToFloat(Object object) {
        return Float.parseFloat(object.toString());
    }
}