package com.malatindez.thaumicextensions.client.render.misc.gui;


import com.malatindez.thaumicextensions.client.lib.UtilsFX;
import com.malatindez.thaumicextensions.client.render.gui.GuiEditor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.lwjgl.util.vector.Vector2f;

@SideOnly(Side.CLIENT)
public class Drag extends Collection implements GuiEditor.Editable {
    protected MethodObjectPair currentlyDragging;
    protected MethodObjectPair dragEnd;

    protected Vector2f previousMousePos = null;
    protected DefaultGuiObject objectToFocusOn;
    private boolean currently_dragging = false;

    /*
     * Drag constructor
     * @param obj object which has currentlyDragged and dragEnd methods.
     *            This parameter can be null.
     * @param currentlyDragging object method which has to have a Vector2F parameter which means current position.
     *                         This parameter can be null.
     * @param dragEnd object method which has to have a Vector2F parameter which means current position.
     *                This parameter can be null.
    public Drag(Collection collection, Object obj, Method currentlyDragging, Method dragEnd) {
        super(collection);
        this.obj = obj;
        this.currentlyDragging = currentlyDragging;
        this.dragEnd = dragEnd;
    }
     */
    private String objectToFocusOnName;

    public Drag(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject generateJSONObject() {
        JSONObject returnValue = super.generateJSONObject();
        JSONObject a = (JSONObject) returnValue.get(getName());
        putMethod(a, "currently_dragging", currentlyDragging);
        putMethod(a, "dragging_end", dragEnd);
        if(objectToFocusOn != null) {
            a.put("drag_focus", objectToFocusOn.getDomainName(this));
        }
        return returnValue;
    }

    @Override
    public void loadFromJSONObject(JSONObject parameters) {
        super.loadFromJSONObject(parameters);
        objectToFocusOnName = (String)parameters.get("drag_focus");
        currentlyDragging = getMethod(parameters, "currently_dragging", new Class[] {Object.class, int.class});
        dragEnd = getMethod(parameters, "dragging_end", new Class[] {Object.class, int.class});
    }
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getFullJSON() {
        JSONObject returnValue = super.getFullJSON();
        JSONObject a = (JSONObject) returnValue.get(getName());

        if(objectToFocusOn != null) {
            a.put("drag_focus", objectToFocusOn.getDomainName(this));
        } else {
            a.put("drag_focus", ".");
        }
        putMethod(a, "currently_dragging", currentlyDragging, false);
        putMethod(a, "dragging_end", dragEnd, false);
        return returnValue;
    }

    private static final ResourceLocation templateLocation = new ResourceLocation("thaumicextensions", "gui/templates/drag_window_template.json");
    @Override
    public JSONObject getTemplateJSON() {
        String s = UtilsFX.loadFromFile(templateLocation);
        return (JSONObject) JSONValue.parse(s);
    }

    @Override
    public void postInit() {
        super.postInit();
        this.objectToFocusOn = (DefaultGuiObject) this.getObjectUp(objectToFocusOnName);
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
        if (dragEnd != null) {
            try {
                dragEnd.method.invoke(dragEnd.object, getCurrentPosition());
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void mouseHandler(Vector2f currentMousePosition) {
        if(hidden()) {
            return;
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
        super.mouseHandler(currentMousePosition);
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
        if(hidden()) {
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

