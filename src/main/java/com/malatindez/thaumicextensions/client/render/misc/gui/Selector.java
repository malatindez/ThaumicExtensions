package com.malatindez.thaumicextensions.client.render.misc.gui;

import com.malatindez.thaumicextensions.client.lib.UtilsFX;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.util.ResourceLocation;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.lwjgl.util.vector.Vector4f;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class Selector extends Collection {
    public Selector(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }
    ArrayList<String> lines;
    TextLine target_line;
    Vector4f backgroundColor;
    private Collection col;
    @SuppressWarnings("unchecked")
    @Override
    public JSONObject generateJSONObject() {
        JSONObject returnValue = super.generateJSONObject();
        JSONObject a = (JSONObject) returnValue.get(getName());
        JSONArray b = new JSONArray();
        b.addAll(lines);
        a.put("lines", b);
        if(target_line != null)
            a.put("target_line", target_line.getDomainName(this));
        a.put("backgroundColor", VecToJson(backgroundColor));
        return returnValue;
    }
    private static String rectColors = "{\"colors\": {\n" +
            "        \"topLeft\": [R, G, B, A],\n" +
            "        \"topRight\": [R, G, B, A],\n" +
            "        \"bottomLeft\": [R, G, B, A],\n" +
            "        \"bottomRight\": [R, G, B, A]\n" +
            "      }}";
    @Override
    public void loadFromJSONObject(JSONObject parameters) {
        super.loadFromJSONObject(parameters);
        lines = new ArrayList<String>();
        JSONArray a = ((JSONArray)parameters.get("lines"));
        for(Object object : a)
            if(object instanceof String)
                lines.add((String)object);
        target_line = (TextLine) this.getObjectDown((String) getStartupParameters().get("target_line"));
        if(parameters.containsKey("backgroundColor")) {
            backgroundColor = Json4Vec(parameters.get("backgroundColor"));
        } else {
            backgroundColor = new Vector4f(0.3f,0.3f,0.3f,1);
        }
        int offset = (int) target_line.getSize().y;
        col = (Collection)this.addObject(new Collection("combined", this, (JSONObject)JSONValue.parse("{\"check_borders\": false}")));
        for(int i = 0; i < lines.size(); i++) {
            Collection object = (Collection) col.addObject(new Collection(lines.get(i), col, (JSONObject)JSONValue.parse("{\"check_borders\": false}")));
            object.setCoordinates(0, offset * (i+1));
            object.setSize(getSize().x, target_line.getSize().y);
            String colors = rectColors
                    .replace("R,", backgroundColor.x + ",")
                    .replace("G", Float.toString(backgroundColor.y))
                    .replace("B", Float.toString(backgroundColor.z))
                    .replace("A", Float.toString(backgroundColor.w));
            object.addObject(new Rect("background", object, (JSONObject)JSONValue.parse(colors)));
            Button button = (Button) object.addObject(new Button("button", object, new JSONObject()));
            try {
                button.clicked = new MethodObjectPair(this,
                        this.getClass().getMethod("textLineClicked", DefaultGuiObject.class, int.class));
            } catch (Exception ignored) {}
            TextLine line = (TextLine)
                    button.addObject(new TextLine("textLine", button,
                            (JSONObject)JSONValue.parse("{\"text\": \"" + lines.get(i) + "\"}")));
            line.color = target_line.color;
            line.dropShadow = target_line.dropShadow;
            line.textScale = target_line.textScale;
            line.textLineWasUpdated();
        }
        col.hide();
        this.target_line.textLine = lines.get(0);
        this.target_line.textLineWasUpdated();
    }
    //expand/collapse button was clicked
    Button buttonRef = null;
    public void ecClicked(DefaultGuiObject object, int id) {
        if(col.hidden()) {
            col.show();
        } else {
            col.hide();
        }
        buttonRef = (Button)object;
    }
    public void textLineClicked(DefaultGuiObject object, int id) {
        this.target_line.textLine = ((TextLine)((Button)object).getDescendants().get(0)).textLine;
        this.target_line.textLineWasUpdated();
        if(buttonRef != null) {
            buttonRef.switchIcons();
            col.hide();
        }
    }

    @Override
    public JSONObject getFullJSON() {
        return generateJSONObject();
    }

    private static final ResourceLocation templateLocation = new ResourceLocation("thaumicextensions", "gui/templates/selector_template.json");
    @Override
    public JSONObject getTemplateJSON() {
        String s = UtilsFX.loadFromFile(templateLocation);
        return (JSONObject) JSONValue.parse(s);
    }

}
