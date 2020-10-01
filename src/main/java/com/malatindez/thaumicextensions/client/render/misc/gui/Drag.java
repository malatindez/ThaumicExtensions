package com.malatindez.thaumicextensions.client.render.misc.gui;


import org.json.simple.JSONObject;
import org.lwjgl.util.vector.Vector2f;

public class Drag extends Collection {
    protected MethodObjectPair currentlyDragging;
    protected MethodObjectPair dragEnd;

    protected Vector2f previousMousePos = null;
    protected DefaultGuiObject objectToFocusOn;
    private boolean currently_dragging = false;

    /*
     * Drag constructor
     * @param obj object which has currentlyDragged and dragEnd methods.
     *            This parameter can be null.
     * @param currentlyDragging object method which has to have a Vector2f parameter which means current position.
     *                         This parameter can be null.
     * @param dragEnd object method which has to have a Vector2f parameter which means current position.
     *                This parameter can be null.
    public Drag(Collection collection, Object obj, Method currentlyDragging, Method dragEnd) {
        super(collection);
        this.obj = obj;
        this.currentlyDragging = currentlyDragging;
        this.dragEnd = dragEnd;
    }
     */
    private final String objectToFocusOnName;
    public Drag(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
        objectToFocusOnName = (String)parameters.get("drag_focus");
    }
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject generateJSONObject() {
        JSONObject returnValue = super.generateJSONObject();
        JSONObject a = (JSONObject) returnValue.get(getName());
        putMethod(a, "currently_dragging", currentlyDragging);
        putMethod(a, "dragging_end", dragEnd);
        a.put("drag_focus", objectToFocusOn.getName());
        return returnValue;
    }

    @Override
    public void postInit() {
        super.postInit();
        JSONObject parameters = this.getStartupParameters();
        this.objectToFocusOn = (DefaultGuiObject) this.getObjectUp(objectToFocusOnName);
        if(parameters.containsKey("currently_dragging")) {
            JSONObject obj = (JSONObject) parameters.get("currently_dragging");
            currentlyDragging = this.getMethodUp(
                    (String) obj.get("object_name"),
                    (String) obj.get("method_name"),
                    new Class[] {Object.class, int.class});
        } else {
            currentlyDragging = null;
        }
        if(parameters.containsKey("dragging_end")) {
            JSONObject obj = (JSONObject) parameters.get("dragging_end");
            dragEnd = this.getMethodUp(
                    (String) obj.get("object_name"),
                    (String) obj.get("method_name"),
                    new Class[] {Object.class, int.class});
        } else {
            dragEnd = null;
        }
    }
    private void CurrentlyDragging() {
        if (currentlyDragging != null && dragEnd != null) {
            try {
                currentlyDragging.method.invoke(currentlyDragging.object, getCurrentPosition());
            } catch (Exception ignored) {
            }
        }
    }
    private void DraggingEnd() {
        //noinspection ConstantConditions
        if (dragEnd != null && dragEnd != null) {
            try {
                dragEnd.method.invoke(dragEnd.object, getCurrentPosition());
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public boolean mouseHandler(Vector2f currentMousePosition) {
        if(hided()) {
            return false;
        }
        if(currently_dragging) {
            if (previousMousePos == null) {
                previousMousePos = new Vector2f(currentMousePosition);
            } else {
                this.setCoordinates(
                        getCoordinates().x - previousMousePos.x + currentMousePosition.x,
                        getCoordinates().y - previousMousePos.y + currentMousePosition.y
                );
                checkBorders();
                previousMousePos.set(currentMousePosition);

            }
            CurrentlyDragging();
        }
        return super.mouseHandler(currentMousePosition);
    }

    @Override
    public void resolutionUpdated(Vector2f newResolution) {
        super.resolutionUpdated(newResolution);

    }
    @Override
    public void Update(int flags) {
        /*
          If LMB not pressed -> dragging event ends
         */
        if(currently_dragging) {
            //noinspection ConstantConditions
            currently_dragging = currently_dragging && ((flags & EnhancedGuiScreen.Updatable.Flags.LMB.getType()) != 0);
            if(!currently_dragging) {
                previousMousePos = null;
                DraggingEnd();
                checkBorders();
            }
        }
        super.Update(flags);
    }

    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {
        if(hided()) {
            return false;
        }
        if(objectToFocusOn != null) {
            if (objectToFocusOn.getBorders().x < currentMousePosition.x &&
                    objectToFocusOn.getBorders().z > currentMousePosition.x &&
                    objectToFocusOn.getBorders().y < currentMousePosition.y &&
                    objectToFocusOn.getBorders().w > currentMousePosition.y &&
                    button == 0
            ) {
                currently_dragging = true;
                return true;
            }
        }
        return super.mouseClicked(currentMousePosition, button);
    }
}

