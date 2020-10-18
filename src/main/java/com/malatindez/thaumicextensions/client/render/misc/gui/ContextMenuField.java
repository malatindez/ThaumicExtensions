package com.malatindez.thaumicextensions.client.render.misc.gui;

import com.malatindez.thaumicextensions.client.lib.UtilsFX;
import com.malatindez.thaumicextensions.client.render.gui.GuiEditor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

@SideOnly(Side.CLIENT)
public class ContextMenuField extends Collection implements GuiEditor.Editable {
    protected MethodObjectPair rmbClicked;
    protected MethodObjectPair lmbClicked;
    public DefaultGuiObject icon;

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject generateJSONObject() {
        JSONObject returnValue = super.generateJSONObject();
        JSONObject a = (JSONObject) returnValue.get(getName());
        a.put("icon", icon.getName());
        putMethod(a, "rmbClicked", rmbClicked);
        putMethod(a, "lmbClicked", lmbClicked);
        return returnValue;
    }

    @Override
    public void loadFromJSONObject(JSONObject parameters) {
        super.loadFromJSONObject(parameters);
        String icon = (String) parameters.get("icon");
        this.icon = (DefaultGuiObject) this.getObjectUp(icon);
        this.addObject(this.icon);
    }
    @Override
    public void postInit() {
        JSONObject parameters = getStartupParameters();
        lmbClicked = getMethod(parameters, "lmbClicked", new Class[] {DefaultGuiObject.class});
        rmbClicked = getMethod(parameters, "rmbClicked", new Class[] {DefaultGuiObject.class, Vector2f.class});
    }

    public ContextMenuField(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getFullJSON() {
        JSONObject returnValue = super.getFullJSON();
        JSONObject a = (JSONObject) returnValue.get(getName());
        a.put("icon", icon.getName());
        putMethod(a, "lmbClicked", lmbClicked, false);
        putMethod(a, "rmbClicked", rmbClicked, false);
        return returnValue;
    }

    private static final ResourceLocation templateLocation = new ResourceLocation("thaumicextensions", "gui/templates/contextmenufield_template.json");
    @Override
    public JSONObject getTemplateJSON() {
        String s = UtilsFX.loadFromFile(templateLocation);
        return (JSONObject) JSONValue.parse(s);
    }

    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {
        if(hidden()) {
            return false;
        }
        if(super.mouseClicked(currentMousePosition, button)) {
            return true;
        }
        if(icon instanceof EnhancedGuiScreen.Clickable && !icon.hidden()) {
            Vector4f temp = icon.getBorders();
            if (temp.x < currentMousePosition.x &&
                    temp.z > currentMousePosition.x &&
                    temp.y < currentMousePosition.y &&
                    temp.w > currentMousePosition.y) {
                return false;
            }
        }
        Vector4f temp = getBorders();
        if(temp.x < currentMousePosition.x &&
           temp.z > currentMousePosition.x &&
           temp.y < currentMousePosition.y &&
           temp.w > currentMousePosition.y) {
            if(button == 0) {
                try {
                    lmbClicked.method.invoke(lmbClicked.object, this);
                } catch (Exception ignored) {
                    return false;
                }
            } else if(button == 1) {
                try {
                    rmbClicked.method.invoke(rmbClicked.object, this, currentMousePosition);
                } catch (Exception ignored) {
                    return false;
                }
            }
        }
        return false;
    }
}
