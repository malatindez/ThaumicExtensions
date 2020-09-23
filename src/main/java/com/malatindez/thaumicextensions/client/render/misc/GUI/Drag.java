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

    private void CurrentlyDragging() {
        if (obj != null && dragEnd != null) {
            try {
                currentlyDragging.invoke(obj, getCurrentPosition());
            } catch (Exception ignored) {
            }
        }
        if(getBorders().x < getParentBorders().x) setCoordinates(0, getCoordinates().y);
        if(getBorders().y < getParentBorders().y) setCoordinates(getCoordinates().x, 0);
        if(getBorders().z > getParentBorders().z)
            setCoordinates(getCoordinates().x + getParentBorders().z - getBorders().z, getCoordinates().y);
        if(getBorders().w > getParentBorders().w)
            setCoordinates( getCoordinates().x, getCoordinates().y + getParentBorders().w - getBorders().w);
    }
    private void DraggingEnd() {
        if (obj != null && dragEnd != null) {
            try {
                dragEnd.invoke(obj, getCurrentPosition());
            } catch (Exception ignored) {
            }
        }
        if(getBorders().x < getParentBorders().x) setCoordinates(0, getCoordinates().y);
        if(getBorders().y < getParentBorders().y) setCoordinates(getCoordinates().x, 0);
        if(getBorders().z > getParentBorders().z)
            setCoordinates(getCoordinates().x + getParentBorders().z - getBorders().z, getCoordinates().y);
        if(getBorders().w > getParentBorders().w)
            setCoordinates( getCoordinates().x, getCoordinates().y + getParentBorders().w - getBorders().w);
    }

    @Override
    public boolean mouseHandler(Vector2f currentMousePosition) {
        if(currently_dragging) {
            if (previousMousePos == null) {
                previousMousePos = new Vector2f(currentMousePosition);
            } else {
                this.setCoordinates(
                        getCoordinates().x - previousMousePos.x + currentMousePosition.x,
                        getCoordinates().y - previousMousePos.y + currentMousePosition.y
                );
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
        /**
         * If LMB not pressed -> dragging ends
         */
        if(currently_dragging) {
            currently_dragging = currently_dragging && ((flags & EnhancedGuiScreen.Updatable.Flags.LMB.getType()) != 0);
            if(!currently_dragging) {
                previousMousePos = null;
                DraggingEnd();
            }
        }
        super.Update(flags);
    }

    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {

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

