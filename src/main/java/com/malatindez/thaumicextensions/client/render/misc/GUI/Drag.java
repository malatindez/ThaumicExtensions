package com.malatindez.thaumicextensions.client.render.misc.GUI;

import com.sun.istack.internal.NotNull;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

import java.lang.reflect.Method;

public class Drag extends Collection {
    protected final Object obj;
    protected final Method currentlyDragging;
    protected final Method dragEnd;

    protected Vector2f previousMousePos = null;
    protected DefaultGuiObject objectToFocusOn;
    private boolean currently_dragging = false;

    public void setCoordinates(Vector2f newCoordinates) {
        this.coordinates.set(newCoordinates);
        updateBorders();
    }
    public Vector2f getCoordinates() {
        return new Vector2f(coordinates);
    }
    public Vector4f getDragBorders() {
        return objectToFocusOn.borders;
    }
    /**
     * Drag constructor
     * @param obj object which has currentlyDragged and dragEnd methods.
     *            This parameter can be null.
     * @param currentlyDragging object method which has to have a Vector2f parameter which means current position.
     *                         This parameter can be null.
     * @param dragEnd object method which has to have a Vector2f parameter which means current position.
     *                This parameter can be null.
     */
    public Drag(Collection collection, Object obj, Method currentlyDragging, Method dragEnd) {
        super(collection);
        this.obj = obj;
        this.currentlyDragging = currentlyDragging;
        this.dragEnd = dragEnd;
    }
    public void DragFocus(DefaultGuiObject objectToFocusOn) {
        this.objectToFocusOn = objectToFocusOn;
    }


    @Override
    public boolean mouseHandler(Vector2f currentMousePosition) {
        if(currently_dragging) {
            if (previousMousePos == null) {
                previousMousePos = new Vector2f(currentMousePosition);
            } else {
                coordinates.set(
                        coordinates.x - previousMousePos.x + currentMousePosition.x,
                        coordinates.y - previousMousePos.y + currentMousePosition.y
                );
                previousMousePos.set(currentMousePosition);
            }
            if (obj != null && dragEnd != null) {
                try {
                    currentlyDragging.invoke(obj, coordinates);
                } catch (Exception ignored) {
                }
            }
        }
        return super.mouseHandler(currentMousePosition);
    }
    @Override
    public void Update(int flags) {
        /**
         * If LMB not pressed -> dragging ends
         */
        if(currently_dragging) {
            currently_dragging = currently_dragging && ((flags & EnhancedGuiScreen.Updatable.Flags.LMB.getType()) != 0);
            if(!currently_dragging) {
                previousMousePos = null;
                if (obj != null && dragEnd != null) {
                    try {
                        dragEnd.invoke(obj, coordinates);
                    } catch (Exception ignored) {
                    }
                }
            }
        }
        super.Update(flags);
    }

    Vector2f currentObjectCoordinates = new Vector2f();
    @Override
    public void render() {
        currentObjectCoordinates.set(coordinates);
    }
    @Override
    public void render(@NotNull Vector2f coordinates) {
        Vector2f.add(coordinates,this.coordinates,currentObjectCoordinates);
        super.render(coordinates);
    }
    @Override
    public void render(@NotNull Vector2f coordinates, @NotNull Vector2f scale) {
        Vector2f.add(coordinates,this.coordinates,currentObjectCoordinates);
        super.render(coordinates, scale);
    }
    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {

        if(objectToFocusOn != null &&
           objectToFocusOn.borders.x < currentObjectCoordinates.x + currentMousePosition.x &&
           objectToFocusOn.borders.z > currentObjectCoordinates.x + currentMousePosition.x &&
           objectToFocusOn.borders.y < currentObjectCoordinates.y + currentMousePosition.y &&
           objectToFocusOn.borders.w > currentObjectCoordinates.y + currentMousePosition.y &&
           button == 0
        ) {
            currently_dragging = true;
            return true;
        }
        return super.mouseClicked(currentMousePosition, button);
    }
}

