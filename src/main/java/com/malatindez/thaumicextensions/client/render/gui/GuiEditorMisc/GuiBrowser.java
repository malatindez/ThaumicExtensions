package com.malatindez.thaumicextensions.client.render.gui.GuiEditorMisc;

import com.malatindez.thaumicextensions.client.render.misc.gui.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class GuiBrowser extends Collection implements ScrollBar.Scrollable {
    protected static class Tree implements Comparable<Tree> {
        public final DefaultGuiObject object;
        public final ArrayList<Tree> descendants = new ArrayList<Tree>();
        Tree(DefaultGuiObject object) {
            this.object = object;
            if(object instanceof Collection) {
                for (DefaultGuiObject descendant : ((Collection)object).getDescendants()) {
                    this.descendants.add(new Tree(descendant));
                }
            }
        }

        @Override
        public int compareTo(Tree o) {
            if(!object.getName().equals(o.object.getName()) || o.descendants.size() != descendants.size()) {
                return -1;
            }
            for(int i = 0; i < descendants.size(); i++) {
                if(descendants.get(i).compareTo(o.descendants.get(i)) != 0) {
                    return -1;
                }
            }
            return 0;
        }
    }
    /* { "type": "Collection", "zLevel": COORD_Y, "coordinates": [0, COORD_Y], "size": [67, 10],
"elements": {
"hide_button": {
 "type": "Button", "coordinates": [0, 0], "size": [10,10], "id": 0, "zLevel": 10, "size_scale_type": "none",
 "clicked": { "object_name": "elements_browser", "method_name": "hideButtonClicked" },
 "icon": "button_icon",
 "hovered_icon": "button_hovered_icon",
 "button_icon":{
  "type": "Collection",
   "shown": {
    "type": "Icon", "size": [10,10], "zLevel": 0, "size_scale_type": "none",
    "mapping_resource_domain": "thaumicextensions", "mapping_resource_path": "texture_mappings/icon_mappings.json", "mapping_icon_name": "shown"
   },
   "hidden": {
    "type": "Icon", "size": [10,10], "zLevel": 0, "size_scale_type": "none", "hided": true
    "mapping_resource_domain": "thaumicextensions", "mapping_resource_path": "texture_mappings/icon_mappings.json", "mapping_icon_name": "hidden"
   }
 },
 "button_hovered_icon":{
  "type": "Collection",
   "shown": {
    "type": "Icon", "size": [10,10], "zLevel": 0, "size_scale_type": "none",
    "mapping_resource_domain": "thaumicextensions", "mapping_resource_path": "texture_mappings/icon_mappings.json", "mapping_icon_name": "shown"
   },
   "hidden": {
    "type": "Icon", "size": [10,10], "zLevel": 0, "size_scale_type": "none", "hided": true
    "mapping_resource_domain": "thaumicextensions", "mapping_resource_path": "texture_mappings/icon_mappings.json", "mapping_icon_name": "hidden"
   }
   "hover_rect": {
	"type": "Rect", "size": [10, 10], "colors": { "topLeft": [0.5,0.5,0.5,0.3], "topRight": [0.5,0.5,0.5,0.3], "bottomLeft": [0.5,0.5,0.5,0.3], "bottomRight": [0.5,0.5,0.5,0.3] }
   }}},
"textline_button": {
"type": "Button",
 "zLevel": 1, "auto_scale_x": true, "check_borders": false, "size_scale_type":"scale_x", "size": [67, 10], "id": COORD_Y, "coordinates_scale_type": "scale_x",
 "clicked": { "object_name": "elements_browser", "method_name": "GuiBrowserElementClicked" },
 "hovered": { "object_name": "elements_browser", "method_name": "GuiBrowserElementHovered" },
 "hoveringStopped": { "object_name": "elements_browser", "method_name": "GuiBrowserElementHoveringStopped" },
 "icon": "Icon", "hovered_icon": "hovered_Icon",
 "Icon": { "type": "Collection", "auto_scale_x": true,
  "elements": {
   "text": { "auto_scale_x": true, "zLevel": 1, "type": "TextLine", "text": "ICON_TEXT", "dropShadow": true },
   "background": { "auto_scale_x": true, "type": "Rect", "colors": { "topLeft": [0.3,0.3,0.3,0.5], "topRight": [0.3,0.3,0.3,0.5], "bottomLeft": [0.3,0.3,0.3,0.5], "bottomRight": [0.3,0.3,0.3,0.5] }}
   }},
 "hovered_Icon": {"auto_scale_x": true, "type": "Collection",
  "elements": {
   "text": { "auto_scale_x": true, "zLevel": 1, "type": "TextLine", "text": "ICON_TEXT", "dropShadow": true },
   "background": { "auto_scale_x": true, "type": "Rect", "colors": { "topLeft": [0.7,0.7,0.7,0.6], "topRight": [0.7,0.7,0.7,0.6], "bottomLeft": [0.7,0.7,0.7,0.6], "bottomRight": [0.7,0.7,0.7,0.6] }}
}}}
}}
                         */

    final String defaultButtonText = "{ \"type\": \"Collection\", \"zLevel\": COORD_Y, \"coordinates\": [0, COORD_Y], \"size\": [67, 10], \"auto_scale_x\": true, \"check_borders\": false, \"size_scale_type\":\"scale_x\", \"size\": [67, 10], \"coordinates_scale_type\": \"scale_x\",\n" +
            "\"elements\": {\n" +
            "\"hide_button\": {\n" +
            " \"type\": \"Button\", \"coordinates\": [0, 0], \"size\": [10,10], \"id\": 0, \"zLevel\": 0, \"size_scale_type\": \"none\",\n" +
            " \"clicked\": { \"object_name\": \"elements_browser\", \"method_name\": \"hideButtonClicked\" },\n" +
            " \"icon\": \"hide_button.shown\",\n" +
            " \"switch_icon\": \"hide_button.hidden\",\n" +
            " \"hovered_icon\": \"hide_button.hover_rect\",\n" +
            " \"elements\": {\n" +
            "     \"shown\": {\n" +
            "      \"type\": \"Icon\", \"size\": [10,10], \"zLevel\": 0, \"size_scale_type\": \"none\",\n" +
            "      \"mapping_resource_domain\": \"thaumicextensions\", \"mapping_resource_path\": \"texture_mappings/icons.json\", \"mapping_icon_name\": \"shown\"\n" +
            "     },\n" +
            "     \"hidden\": {\n" +
            "      \"type\": \"Icon\", \"size\": [10,10], \"zLevel\": 1, \"size_scale_type\": \"none\", \"hided\": true,\n" +
            "      \"mapping_resource_domain\": \"thaumicextensions\", \"mapping_resource_path\": \"texture_mappings/icons.json\", \"mapping_icon_name\": \"hidden\"\n" +
            "     },\n" +
            "  \t \"hover_rect\": {\n" +
            "  \t  \"type\": \"Rect\", \"size\": [10, 10], \"zLevel\": 2, \"colors\": { \"topLeft\": [0.7,0.7,0.7,0.3], \"topRight\": [0.7,0.7,0.7,0.3], \"bottomLeft\": [0.7,0.7,0.7,0.3], \"bottomRight\": [0.7,0.7,0.7,0.3] }\n" +
            "     }\n" +
            " }\n" +
            "},\n" +
            "\"textline_button\": { \n" +
            "\"type\": \"Button\",\n" +
            " \"zLevel\": 1, \"auto_scale_x\": true, \"check_borders\": false, \"size_scale_type\":\"scale_x\", \"size\": [57, 10], \"id\": COORD_Y, \"coordinates_scale_type\": \"none\", \"coordinates\": [10, 0],\n" +
            " \"clicked\": { \"object_name\": \"elements_browser\", \"method_name\": \"GuiBrowserElementClicked\" },\n" +
            " \"hovered\": { \"object_name\": \"elements_browser\", \"method_name\": \"GuiBrowserElementHovered\" },\n" +
            " \"hoveringStopped\": { \"object_name\": \"elements_browser\", \"method_name\": \"GuiBrowserElementHoveringStopped\" },\n" +
            " \"icon\": \"textline_button.Icon\", \"hovered_icon\": \"textline_button.hovered_Icon\",\n" +
            " \"elements\": {\n" +
            "  \"Icon\": { \"type\": \"Collection\", \"auto_scale_x\": true,\n" +
            "   \"elements\": {\n" +
            "    \"text\": { \"auto_scale_x\": true, \"zLevel\": 1, \"type\": \"TextLine\", \"text\": \"ICON_TEXT\", \"dropShadow\": true }\n" +
            "    }},\n" +
            "  \"hovered_Icon\": {\"auto_scale_x\": true, \"type\": \"Collection\",\n" +
            "   \"elements\": {\n" +
            "    \"text\": { \"auto_scale_x\": true, \"zLevel\": 1, \"type\": \"TextLine\", \"text\": \"ICON_TEXT\", \"dropShadow\": true }\n" +
            "}}}}\n" +
            "}}";

    public GuiBrowser(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }
    Object gui_ref;
    long lastUpdate = 0;
    public DefaultGuiObject selected = null; // reference to currently selected object
    public Collection subElements = null; // reference to elements_browser.elements
    public DefaultGuiObject background = null; // reference to elements_browser.background
    public DefaultGuiObject hover_rect = null; // reference to elements_browser.hover_rect
    protected int selectedId = 0;

    Tree currentTree = null;

    private int currentOffsetY = 0;
    private int maxXSize = 0;
    private void updateDescendants(Tree o, int currentOffsetX) {
        String buttonJSON = defaultButtonText;
        String s = "";
        //noinspection StatementWithEmptyBody
        for(int i = 0; i < currentOffsetX; i++, s += " ");
        buttonJSON = buttonJSON.replace(
                "ICON_TEXT",
                s + o.object.getName() + " [" + o.object.getClass().getSimpleName() + "]"
        );
        maxXSize = Math.max(maxXSize, (s + o.object.getName() + " [" + o.object.getClass().getSimpleName() + "]").length());
        buttonJSON = buttonJSON.replace("COORD_Y", Integer.toString(currentOffsetY * 10));
        currentOffsetY++;
        Object obj = subElements.addObject(new Collection(o.object.getGlobalName().replace(".", "\n"),
                subElements,
                (JSONObject)JSONValue.parse(buttonJSON)
                ));
        if((o.object.getParent() instanceof Button)) {
            ((Collection)obj).getDescendants().get(1).hide();
        }
        for(Tree descendant : o.descendants) {
            updateDescendants(descendant, currentOffsetX + 2);
        }
    }
    private void updateTree(Tree o) {
        currentOffsetY = 0;
        currentTree = o;
        subElements.getDescendants().clear();
        updateDescendants(o, 0);
    }

    @Override
    public void Update(int flags) {
        if(lastUpdate + 1000 > Minecraft.getSystemTime()) {
            super.Update(flags);
            return;
        }
        lastUpdate = Minecraft.getSystemTime();
        Tree newTree = new Tree((DefaultGuiObject) gui_ref);
        if(newTree.compareTo(currentTree) != 0) {
            updateTree(newTree);
        }
        super.Update(flags);
        updateHideStates();
    }

    private float offsetX = 0;
    private float offsetY = 0;
    public void updateHideStates() {
        if(subElements == null) {
            return;
        }
        float a = Math.min(0, (background.getSize().y -
                subElements.getDescendants().size() *
                        subElements.getDescendants().get(0).getSize().y) * offsetY);
        if(subElements.getCoordinates().y != a){
            subElements.setCoordinates(0, a);
        }
        for(DefaultGuiObject defaultGuiObject : subElements.getDescendants()) {
            if(defaultGuiObject.getCurrentPosition().y < - 1.0f / 24.0f * currentResolution.y
                    || defaultGuiObject.getCurrentPosition().y > background.getBorders().w) {
                defaultGuiObject.hide();
            } else {
                defaultGuiObject.show();
            }
            ((TextLine)((Collection)((Collection)((Collection)defaultGuiObject).getDescendants().get(0)).getDescendants().get(0)).getDescendants().get(0)).setRenderCursor((int) (maxXSize * offsetX));
            ((TextLine)((Collection)((Collection)((Collection)defaultGuiObject).getDescendants().get(0)).getDescendants().get(1)).getDescendants().get(0)).setRenderCursor((int) (maxXSize * offsetX));
        }
    }

    @Override
    public void postInit() {
        gui_ref = getObjectUp("gui_editor.gui.new_gui");
        subElements = (Collection) getObjectDown("elements_browser.elements");
        background = (DefaultGuiObject) getObjectDown("elements_browser.background");
        hover_rect = (DefaultGuiObject) getObjectDown("elements_browser.hover_rect");
        super.postInit();
        updateTree(new Tree((DefaultGuiObject) gui_ref));
        subElements.postInit();
    }

    @Override
    public void resolutionUpdated(Vector2f newResolution) {
        super.resolutionUpdated(newResolution);
        updateHideStates();
    }

    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {
        return super.mouseClicked(currentMousePosition, button);
    }

    @Override
    public void setOffsetX(float offset) {
        offsetX = offset;
        updateHideStates();
    }

    @Override
    public void setOffsetY(float offset) {
        offsetY = offset;
        updateHideStates();
    }

    @Override
    public float getOffsetX() {
        return offsetX;
    }
    @Override
    public float getOffsetY() {
        return offsetY;
    }

    public void GuiBrowserElementClicked(DefaultGuiObject buttonReference, int id) {
        selected = buttonReference; selectedId = id / 10;
    }

    public void GuiBrowserElementHovered(DefaultGuiObject buttonReference, int id) {
        DefaultGuiObject object = (DefaultGuiObject) this.getObjectUp(((DefaultGuiObject)buttonReference.getParent()).getName().replace("\n","."));
        hover_rect.setSize(object.getSize());
        hover_rect.setCoordinates(Vector2f.sub(object.getCurrentPosition(), hover_rect.getParentCoordinates(), null));
    }
    public void GuiBrowserElementHoveringStopped(DefaultGuiObject buttonReference, int id) {
        hover_rect.setSize(0,0);
    }

    public void hideButtonClicked(DefaultGuiObject buttonReference, int id) {
        DefaultGuiObject object = (DefaultGuiObject) this.getObjectUp(((DefaultGuiObject)buttonReference.getParent()).getName().replace("\n","."));
        if(object.hidden()) {
            object.show();
        } else {
            object.hide();
        }
    }
}
