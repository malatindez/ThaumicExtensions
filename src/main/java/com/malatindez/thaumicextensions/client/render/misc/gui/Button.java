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
public class Button extends Collection implements GuiEditor.Editable {
    protected DefaultGuiObject icon; // reference to a default icon
    protected DefaultGuiObject switch_icon; // reference to a switch icon (when the button is clicked, this icon will be rendered)
    protected DefaultGuiObject hovered_icon; // reference to a hovered icon
    protected MethodObjectPair clicked;
    protected MethodObjectPair hovered;
    protected MethodObjectPair hoveringStopped;
    protected int id;
    protected boolean renderOver;

    public void switchIcons() {
        if(switch_icon != null) {
            if(switch_icon.hidden()) {
                switch_icon.show();
                icon.hide();
            } else {
                switch_icon.hide();
                icon.show();
            }
        }
    }
    public Button(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }

    boolean isHovered = false;
    @Override
    public void mouseHandler(Vector2f currentMousePosition) {
        if(hidden()) {
            return;
        }
        Vector4f temp = getBorders();
        if(
                temp.x < currentMousePosition.x &&
                        temp.z > currentMousePosition.x &&
                        temp.y < currentMousePosition.y &&
                        temp.w > currentMousePosition.y
        ) {
            isHovered = true;
            try {
                hovered.method.invoke(hovered.object,this, id);
            } catch (Exception ignored) {}
            if(this.hovered_icon != null) {
                if(!renderOver && this.icon != null) {
                    icon.hide();
                }
                hovered_icon.show();
            }
            return;
        }
        isHovered = false;
        if(this.hovered_icon != null && this.icon != null && (icon.hidden() || !hovered_icon.hidden())) {
            if(!renderOver) {
                icon.show();
            }
            hovered_icon.hide();
            try {
                hoveringStopped.method.invoke(hoveringStopped.object,this, id);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {
        if(hidden()) {
            return false;
        }
        Vector4f temp = getBorders();
        if(button == 0 &&
           temp.x < currentMousePosition.x &&
           temp.z > currentMousePosition.x &&
           temp.y < currentMousePosition.y &&
           temp.w > currentMousePosition.y) {
            switchIcons();
            try {
                clicked.method.invoke(clicked.object,this, id);
                return true;
            } catch (Exception ignored) { }
        }
        return false;
    }



    @SuppressWarnings("unchecked")
    @Override
    public JSONObject generateJSONObject() {
        JSONObject returnValue = super.generateJSONObject();
        JSONObject a = (JSONObject) returnValue.get(getName());
        if(id != 0)
            a.put("id", (long)id);
        if(!renderOver)
            a.put("renderOver", false);
        putMethod(a, "clicked", clicked);
        putMethod(a, "hovered", hovered);
        if(icon != null) {
            a.put("icon", this.icon.getName());
        }
        if(hovered_icon != null) {
            a.put("hovered_icon", this.hovered_icon.getName());
        }
        if(switch_icon != null) {
            a.put("switch_icon", this.switch_icon.getName());
        }
        return returnValue;
    }

    @Override
    public void loadFromJSONObject(JSONObject parameters) {
        super.loadFromJSONObject(parameters);
        clicked = getMethod(parameters, "clicked", new Class[] {DefaultGuiObject.class, int.class});
        hovered = getMethod(parameters, "hovered", new Class[] {DefaultGuiObject.class, int.class});
        hoveringStopped = getMethod(parameters, "hoveringStopped", new Class[] {DefaultGuiObject.class, int.class});

        if(parameters.containsKey("id")) {
            id = (int)JsonToFloat(parameters.get("id"));
        } else {
            id = 0;
        }
        if(parameters.containsKey("render_over")) {
            renderOver = (Boolean) parameters.get("render_over");
        } else {
            renderOver = true;
        }

        if(parameters.containsKey("icon")) {
            icon = (DefaultGuiObject) this.getObjectUp((String) parameters.get("icon"));
            if(icon == null) {
                System.out.println("[DEBUG] ERROR! icon wasn't found in " + getGlobalName());
            }
        }
        if(parameters.containsKey("hovered_icon")) {
            hovered_icon = (DefaultGuiObject) this.getObjectUp((String) parameters.get("hovered_icon"));
            if(hovered_icon != null) {
                hovered_icon.hide();
            } else {
                System.out.println("[DEBUG] ERROR! hovered_icon wasn't found in " + getGlobalName());
            }
        }
        if(parameters.containsKey("switch_icon")) {
            switch_icon = (DefaultGuiObject) this.getObjectUp((String) parameters.get("switch_icon"));
            if(switch_icon != null) {
                switch_icon.hide();
            } else {
                System.out.println("[DEBUG] ERROR! switch_icon wasn't found in " + getGlobalName());
            }
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public JSONObject getFullJSON() {
        JSONObject returnValue = getFullDefaultJSON();
        JSONObject a = (JSONObject) returnValue.get(getName());
        a.put("id", (long)id);
        a.put("renderOver", renderOver);
        putMethod(a, "clicked", clicked, false);
        putMethod(a, "hovered", hovered, false);
        if(icon != null) {
            a.put("icon", this.icon.getName());
        } else {
            a.put("icon", "icon_name");
        }
        if(hovered_icon != null) {
            a.put("hovered_icon", this.hovered_icon.getName());
        } else {
            a.put("icon", "hovered_icon_name");
        }
        if(switch_icon != null) {
            a.put("switch_icon", this.switch_icon.getName());
        } else {
            a.put("icon", "switch_icon_name");
        }
        return returnValue;
    }

    private static final ResourceLocation templateLocation = new ResourceLocation("thaumicextensions", "gui/templates/button_template.json");
    @Override
    public JSONObject getTemplateJSON() {
        String s = UtilsFX.loadFromFile(templateLocation);
        return (JSONObject) JSONValue.parse(s);
    }
}
