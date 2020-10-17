package com.malatindez.thaumicextensions.client.render.misc.gui;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.lang.reflect.Method;

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
        public static ResolutionRescaleType fromString(String a) {
            if(a.equals("scale_x")) {
                return ResolutionRescaleType.SCALE_X;
            } else if(a.equals("scale_y")) {
                return ResolutionRescaleType.SCALE_Y;
            } else if(a.equals("scale_smooth_x")) {
                return ResolutionRescaleType.SCALE_SMOOTH_X;
            } else if(a.equals("scale_smooth_y")) {
                return ResolutionRescaleType.SCALE_SMOOTH_Y;
            } else if(a.equals("scale_xy")) {
                return ResolutionRescaleType.SCALE_XY;
            } else if(a.equals("scale_smooth_xy")) {
                return ResolutionRescaleType.SCALE_SMOOTH_XY;
            }
            return ResolutionRescaleType.NONE;
        }
    }

    enum FocalPoint {
        TOP_LEFT("top_left"),
        TOP_RIGHT("top_right"),
        BOTTOM_LEFT("bottom_left"),
        BOTTOM_RIGHT("bottom_right");
        private final String a;
        public Vector2f getPoint(DefaultGuiObject object) {
            switch(this) {
                case TOP_LEFT:
                    return object.getTopLeftCorner();
                case TOP_RIGHT:
                    return object.getTopRightCorner();
                case BOTTOM_LEFT:
                    return object.getBottomLeftCorner();
            }
            return object.getBottomRightCorner();
        }
        public String toString() {
            return a;
        }
        FocalPoint(String a) {this.a = a; }
        public static FocalPoint fromString(String a) {
            if(a.equals("top_right")) {
                return TOP_RIGHT;
            }
            if(a.equals("bottom_left")) {
                return BOTTOM_LEFT;
            }
            if(a.equals("bottom_right")) {
                return BOTTOM_RIGHT;
            }
            return TOP_LEFT;
        }
        public Vector2f fromVector4f(Vector4f borders) {
            if(this == TOP_LEFT) {
                return new Vector2f(borders.x, borders.y);
            }
            if(this == TOP_RIGHT) {
                return new Vector2f(borders.z, borders.y);
            }
            if(this == BOTTOM_LEFT) {
                return new Vector2f(borders.x, borders.w);
            }

            return new Vector2f(borders.z, borders.w);
        }
    }

    public static class LinkedPoint {
        final DefaultGuiObject linkedPointObject;
        final FocalPoint refPoint;
        public LinkedPoint(DefaultGuiObject linkedPointObject, FocalPoint p) {
            this.linkedPointObject = linkedPointObject;
            this.refPoint = p;
        }

        /**
         *
         * @param reference to a main object
         * @param object
         */
        public LinkedPoint(DefaultGuiObject reference, JSONObject object, FocalPoint defaultFocalPoint) {
            linkedPointObject = (DefaultGuiObject) reference.getObjectUp((String) object.get("object"));
            if(linkedPointObject == null) {
                throw new NullPointerException(object.get("object") + " wasn't found");
            }
            if(object.containsKey("focal_point")) {
                refPoint = FocalPoint.fromString((String) object.get("focal_point"));
            } else {
                refPoint = defaultFocalPoint;
            }
        }
        public Vector2f getPoint() {
            return refPoint.getPoint(linkedPointObject);
        }

        public JSONObject toJSON() {
            JSONObject object = new JSONObject();
            object.put("object", linkedPointObject.getName());
            object.put("focal_point", refPoint.toString());
            return object;
        }
        public static JSONObject getDefaultJSON() {
            JSONObject object = new JSONObject();
            object.put("object",  ".");
            object.put("focal_point", "none");
            return object;
        }
    }

    public static class LinkedPoints {
        LinkedPoint a;
        LinkedPoint b;
        // mode = 0 -> a is topLeft corner, b is bottomRight
        // mode = 1 -> a is topRight corner, b is bottomLeft
        int mode = -1;
        public LinkedPoints(DefaultGuiObject ref, JSONObject object) {
            if(object.containsKey(FocalPoint.TOP_LEFT.toString()) && object.containsKey(FocalPoint.BOTTOM_RIGHT.toString())) {
                mode = 0;
                a = new LinkedPoint(ref,
                        (JSONObject) object.get(FocalPoint.TOP_LEFT.toString()), FocalPoint.TOP_LEFT);
                b = new LinkedPoint(ref,
                        (JSONObject) object.get(FocalPoint.BOTTOM_RIGHT.toString()), FocalPoint.BOTTOM_RIGHT);
            }
            if(mode == -1) {
                if(object.containsKey(FocalPoint.TOP_RIGHT.toString()) && object.containsKey(FocalPoint.BOTTOM_LEFT.toString())) {
                    mode = 1;
                    a = new LinkedPoint(ref,
                            (JSONObject) object.get(FocalPoint.TOP_RIGHT.toString()), FocalPoint.TOP_RIGHT);
                    b = new LinkedPoint(ref,
                            (JSONObject) object.get(FocalPoint.BOTTOM_LEFT.toString()), FocalPoint.BOTTOM_LEFT);
                }
            }
        }
        public JSONObject toJSON() {
            JSONObject object = new JSONObject();
            if(mode == 0) {
                object.put(FocalPoint.TOP_LEFT.toString(), a.toJSON());
                object.put(FocalPoint.BOTTOM_RIGHT.toString(), a.toJSON());
            } else if(mode == 1) {
                object.put(FocalPoint.TOP_RIGHT.toString(), a.toJSON());
                object.put(FocalPoint.BOTTOM_LEFT.toString(), a.toJSON());
            }

            return object;
        }
        public static JSONObject getDefaultJSON() {
            JSONObject object = new JSONObject();
            object.put(FocalPoint.TOP_LEFT.toString(), LinkedPoint.getDefaultJSON());
            object.put(FocalPoint.BOTTOM_RIGHT.toString(), LinkedPoint.getDefaultJSON());
            return object;
        }
        public Vector4f getBorders() {
            if(mode == 0) {
                return new Vector4f(a.getPoint().x, a.getPoint().y, b.getPoint().x, b.getPoint().y);
            }
            return new Vector4f(b.getPoint().x, a.getPoint().y, a.getPoint().x, b.getPoint().y);
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

    private boolean checkBorders = true;
    protected boolean auto_scale_x;
    protected boolean auto_scale_y;

    LinkedPoints points = null;

    protected final Vector2f currentResolution = new Vector2f(427, 240);
    public static final Vector2f defaultResolution = new Vector2f(427, 240);

    private final Vector2f parentCoordinates = new Vector2f(0,0);
    private final Vector4f parentBorders = new Vector4f(0,0,0,0);
    private final Vector2f currentObjectPosition = new Vector2f(0,0);

    private ResolutionRescaleType sizeRescaleType;
    private ResolutionRescaleType coordinatesRescaleType;

    private boolean hide = false;

    FocalPoint focal_point;


    public boolean hidden() {
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
    public String getGlobalName() {
        if(parent instanceof DefaultGuiObject) {
            return ((DefaultGuiObject) parent).getGlobalName() + "." + getName();
        }
        return getName();
    }
    public Vector4f getBorders() {
        return new Vector4f(borders);
    }
    public Vector2f getTopLeftCorner() {
        return new Vector2f(borders.x, borders.y);
    }
    public Vector2f getTopRightCorner() {
        return new Vector2f(borders.z, borders.y);
    }
    public Vector2f getBottomLeftCorner() {
        return new Vector2f(borders.x, borders.w);
    }
    public Vector2f getBottomRightCorner() {
        return new Vector2f(borders.z, borders.w);
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


    // You shouldn't use this function. This function will have been used only during gui editing.
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

    protected void setParent(DefaultGuiObject parent) {
        this.parent = parent;
    }
    private boolean postInited = false;
    // postInit is called after entire gui is loaded
    public void postInit() {
        postInited = true;
        if(getStartupParameters().containsKey("linked_points")) {
            this.points = new LinkedPoints(this, (JSONObject) getStartupParameters().get("linked_points"));
            updateVectors();
        }
    }

    // this function will be called when vectors were updated
    void VectorsWereUpdated() { }


    protected void updateVectors() {
        if(points != null && points.mode == 0) {
            Vector2f.add(points.a.getPoint(), coordinates, currentObjectPosition);
            Vector2f.sub(points.b.getPoint(), points.a.getPoint(), size);
        }
        else if (points != null && points.mode == 1) {
            Vector2f.add(new Vector2f(points.b.getPoint().x, points.a.getPoint().y), coordinates, currentObjectPosition);
            size.set(points.a.getPoint().x - points.b.getPoint().x, points.b.getPoint().y - points.a.getPoint().y);
        } else {
            Vector2f.add(focal_point.fromVector4f(parentBorders), coordinates, currentObjectPosition);
            if(auto_scale_x) {
                size.x = parentBorders.z - currentObjectPosition.x;
            }
            if(auto_scale_y) {
                size.y = parentBorders.w - currentObjectPosition.y;
            }
        }
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

    private JSONObject getJSON(boolean hideDefaultVariables) {
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
        if(hideDefaultVariables) {
            if (coordinates.x != 0 || coordinates.y != 0)
                a.put("coordinates", VecToJson(coordinates));
            if (scale.x != 1 || scale.y != 1)
                a.put("scale", VecToJson(scale));
            if (zLevel != 0)
                a.put("zLevel", (long) zLevel);
            if (hide)
                a.put("hided", true);
            if (points == null) {
                if (auto_scale_x)
                    a.put("auto_scale_x", auto_scale_x);
                if (auto_scale_y)
                    a.put("auto_scale_y", auto_scale_y);
            }
            if (focal_point != FocalPoint.TOP_LEFT)
                a.put("focal_point", focal_point.toString());
            if (points != null)
                a.put("linked_points", points.toJSON());

            if (parent instanceof DefaultGuiObject) {
                if (!sizeRescaleType.equals(((DefaultGuiObject) parent).sizeRescaleType))
                    a.put("size_scale_type", sizeRescaleType.toString());

                if (!coordinatesRescaleType.equals(((DefaultGuiObject) parent).coordinatesRescaleType))
                    a.put("coordinates_scale_type", coordinatesRescaleType.toString());

                if (!this.size.equals(((DefaultGuiObject) parent).size))
                    a.put("size", VecToJson(size));
            } else {
                if (sizeRescaleType != ResolutionRescaleType.NONE)
                    a.put("size_scale_type", sizeRescaleType.toString());

                if (coordinatesRescaleType != ResolutionRescaleType.NONE)
                    a.put("coordinates_scale_type", coordinatesRescaleType.toString());

                if (size.x != 0 || size.y != 0) {
                    a.put("size", VecToJson(size));
                }
            }
        } else {
            a.put("coordinates", VecToJson(coordinates));
            a.put("scale", VecToJson(scale));
            a.put("zLevel", (long) zLevel);
            a.put("hided", true);
            a.put("auto_scale_x", auto_scale_x);
            a.put("auto_scale_y", auto_scale_y);
            a.put("focal_point", focal_point.toString());
            if (points != null)
                a.put("linked_points", points.toJSON());
            else a.put("linked_points", LinkedPoints.getDefaultJSON());
            a.put("size_scale_type", sizeRescaleType.toString());
            a.put("coordinates_scale_type", coordinatesRescaleType.toString());
            a.put("size", VecToJson(size));
        }
        return returnValue;
    }

    protected JSONObject getFullDefaultJSON() {
        return this.getJSON(false);
    }

    public JSONObject generateJSONObject() {
        return this.getJSON(true);
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
        if(parameters.containsKey("check_borders")) {
            checkBorders = (Boolean)(parameters.get("check_borders"));
        }


        zLevel = 0;
        if(parameters.containsKey("zLevel")) {
            zLevel = (int) Float.parseFloat(parameters.get("zLevel").toString());
        }

        if(parameters.containsKey("hided")) {
            hide = (Boolean)parameters.get("hided");
        }

        if(parameters.containsKey("auto_scale_x")) {
            auto_scale_x = (Boolean)parameters.get("auto_scale_x");
        }

        if(parameters.containsKey("auto_scale_y")) {
            auto_scale_y = (Boolean) parameters.get("auto_scale_y");
        }

        this.focal_point  = FocalPoint.TOP_LEFT;
        if(parameters.containsKey("focal_point")) {
            this.focal_point = FocalPoint.fromString((String) parameters.get("focal_point"));
        }

        if(parameters.containsKey("size_scale_type")) {
            sizeRescaleType = ResolutionRescaleType.fromString((String)parameters.get("size_scale_type"));
        } else if(parent instanceof DefaultGuiObject) {
            sizeRescaleType = ((DefaultGuiObject) parent).sizeRescaleType;
        } else {
            sizeRescaleType = ResolutionRescaleType.NONE;
        }

        if(parameters.containsKey("coordinates_scale_type")) {
            coordinatesRescaleType = ResolutionRescaleType.fromString((String)parameters.get("coordinates_scale_type"));
        } else if(parent instanceof DefaultGuiObject) {
            coordinatesRescaleType = ((DefaultGuiObject) parent).coordinatesRescaleType;
        } else {
            coordinatesRescaleType = ResolutionRescaleType.NONE;
        }

        this.size = new Vector2f(0, 0);
        if(parameters.containsKey("size")) {
            this.size.set(Json2Vec(parameters.get("size")));
        } else if(parent instanceof DefaultGuiObject) {
            this.size.set(((DefaultGuiObject) parent).getSize());
        }

        this.reScale(scale);
    }

    public DefaultGuiObject(String name, Object parent, JSONObject parameters) {
        this.name = name.replace(" ", "").replace(".", "");
        this.parent = parent;
        loadFromJSONObject(parameters);
    }

    @Override
    public int getZLevel() {
        return zLevel;
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
        if(checkBorders)
            checkBorders();
    }

    @Override
    public void updateParentBorders(Vector4f parentBorders) {
        this.parentCoordinates.set(parentBorders.x, parentBorders.y);
        this.parentBorders.set(parentBorders);  updateVectors();
        updateVectors();
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
            System.out.println("Method " + name + " wasn't found in " + objectName);
            return null;
        }
    }

    @SuppressWarnings("rawtypes")
    protected MethodObjectPair getMethodUp(String objectName, String methodName, Class[] parameterTypes) {
        if(this.getName().equals(objectName)) {
            return getMethodFunc(objectName, methodName, parameterTypes);
        }
        int a = objectName.indexOf('.');
        if(a != -1 && a + 1 < objectName.length() && objectName.substring(0, a).equals(this.name)) {
            return getMethodDown(objectName, methodName, parameterTypes);
        }
        if(parent instanceof DefaultGuiObject) {
            return ((DefaultGuiObject)parent).getMethodUp(objectName, methodName, parameterTypes);
        } else if (objectName.equals(".")) {
            try {
                return new MethodObjectPair(parent, parent.getClass().getMethod(methodName, parameterTypes));
            } catch (Exception ignored) {}
        }
        return getMethodDown(objectName, methodName, parameterTypes);
    }

    @SuppressWarnings("rawtypes")
    public MethodObjectPair getMethodDown(String objectName, String name, Class[] parameterTypes) {
        if(objectName.equals(this.getName())) {
            getMethodFunc(objectName, name, parameterTypes);
        }
        if(!(this instanceof Collection)) {
            return null;
        }
        int a = objectName.indexOf('.');
        if(a == -1 || a + 1 > objectName.length() || !objectName.substring(0, a).equals(this.name)) {
            return null;
        }
        String nextString = objectName.substring(a + 1);
        MethodObjectPair retValue = null;
        for(Object obj : ((Collection)this).getDescendants()) {
            if(retValue != null) {
                return retValue;
            }
            retValue = ((DefaultGuiObject)obj).getMethodDown(nextString, name, parameterTypes);
        }
        return retValue;
    }




    protected Object getObjectUp(String objectName) {
        if(this.getName().equals(objectName)) {
            return this;
        }
        int a = objectName.indexOf('.');
        if(a != -1 && a + 1 < objectName.length() && objectName.substring(0, a).equals(this.name)) {
            return getObjectDown(objectName);
        }
        if(parent instanceof DefaultGuiObject) {
            return ((DefaultGuiObject)parent).getObjectUp(objectName);
        } else if(objectName.equals(".")) {
            return parent;
        }
        return getObjectDown(objectName);
    }

    public Object getObjectDown(String objectName) {
        if(objectName.equals(this.getName())) {
            return this;
        }

        int a = objectName.indexOf('.');
        if(a == -1 || a + 1 > objectName.length() || !objectName.substring(0, a).equals(this.name)) {
            return null;
        }
        String nextString = objectName.substring(a + 1);
        Object retValue = null;
        for(Object obj : ((Collection)this).getDescendants()) {
            if(retValue != null) {
                return retValue;
            }
            retValue = ((DefaultGuiObject)obj).getObjectDown(nextString);
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