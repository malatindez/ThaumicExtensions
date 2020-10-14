package com.malatindez.thaumicextensions.client.render.gui.GuiEditorMisc;

import com.malatindez.thaumicextensions.client.render.misc.Vectors.Vector;
import com.malatindez.thaumicextensions.client.render.misc.gui.*;
import net.minecraft.client.Minecraft;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.lwjgl.util.vector.Vector2f;
import scala.tools.nsc.doc.model.Def;
import scala.util.hashing.Hashing;

import java.util.ArrayList;

public class GuiBrowser extends Collection implements EnhancedGuiScreen.Clickable, ScrollBar.Scrollable,
        EnhancedGuiScreen.Updatable {
    protected class Tree implements Comparable<Tree> {
        public DefaultGuiObject object;
        public ArrayList<Tree> descendants = new ArrayList<Tree>();
        Tree(DefaultGuiObject object) {
            this.object = object;
            for(DefaultGuiObject descendant : object.getDescendants()) {
                this.descendants.add(new Tree(descendant));
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
    /*           { "type": "Button",
		   "auto_scale_x": true, "check_borders": false,
		   "coordinates": [0, COORD_Y],
		   "size": [67, 10], "id": COORD_Y, "zLevel": COORD_Y, "coordinates_scale_type": "scale_x",
                           "clicked": { "object_name": "elements_browser", "method_name": "GuiBrowserElementClicked" },
                           "hovered": { "object_name": "elements_browser", "method_name": "GuiBrowserElementHovered" },
                           "icon": "Icon", "hovered_icon": "hovered_Icon",
                           "Icon": {
                             "type": "Collection", "auto_scale_x": true,
                             "elements": {
                               "text": { "auto_scale_x": true, "zLevel": 1, "type": "TextLine", "text": "ICON_TEXT", "dropShadow": true },
                               "background": { "auto_scale_x": true, "type": "Rect", "colors": { "topLeft": [0.3,0.3,0.3,0.5], "topRight": [0.3,0.3,0.3,0.5], "bottomLeft": [0.3,0.3,0.3,0.5], "bottomRight": [0.3,0.3,0.3,0.5] }}
                             }},
                           "hovered_Icon": {"auto_scale_x": true,
                             "type": "Collection",
                             "elements": {
                               "text": { "auto_scale_x": true, "zLevel": 1, "type": "TextLine", "text": "ICON_TEXT", "dropShadow": true },
                               "background": { "auto_scale_x": true, "type": "Rect", "colors": { "topLeft": [0.7,0.7,0.7,0.6], "topRight": [0.7,0.7,0.7,0.6], "bottomLeft": [0.7,0.7,0.7,0.6], "bottomRight": [0.7,0.7,0.7,0.6] }}
                             }}
                         }
                         */

    String defaultButtonText = "{ \"type\": \"Button\",\n" +
            "   \"auto_scale_x\": true, \"check_borders\": false, \"size_scale_type\":\"scale_x\",\n" +
            "   \"coordinates\": [0, COORD_Y],\n" +
            "   \"size\": [67, 10], \"id\": COORD_Y, \"zLevel\": COORD_Y, \"coordinates_scale_type\": \"scale_x\",\n" +
            "                           \"clicked\": { \"object_name\": \"elements_browser\", \"method_name\": \"GuiBrowserElementClicked\" },\n" +
            "                           \"hovered\": { \"object_name\": \"elements_browser\", \"method_name\": \"GuiBrowserElementHovered\" },\n" +
            "                           \"hoveringStopped\": { \"object_name\": \"elements_browser\", \"method_name\": \"GuiBrowserElementHoveringStopped\" },\n" +
            "                           \"icon\": \"Icon\", \"hovered_icon\": \"hovered_Icon\",\n" +
            "                           \"Icon\": {\n" +
            "                             \"type\": \"Collection\", \"auto_scale_x\": true,\n" +
            "                             \"elements\": {\n" +
            "                               \"text\": { \"auto_scale_x\": true, \"zLevel\": 1, \"type\": \"TextLine\", \"text\": \"ICON_TEXT\", \"dropShadow\": true },\n" +
            "                               \"background\": { \"auto_scale_x\": true, \"type\": \"Rect\", \"colors\": { \"topLeft\": [0.3,0.3,0.3,0.5], \"topRight\": [0.3,0.3,0.3,0.5], \"bottomLeft\": [0.3,0.3,0.3,0.5], \"bottomRight\": [0.3,0.3,0.3,0.5] }}\n" +
            "                             }},\n" +
            "                           \"hovered_Icon\": {\"auto_scale_x\": true,\n" +
            "                             \"type\": \"Collection\",\n" +
            "                             \"elements\": {\n" +
            "                               \"text\": { \"auto_scale_x\": true, \"zLevel\": 1, \"type\": \"TextLine\", \"text\": \"ICON_TEXT\", \"dropShadow\": true },\n" +
            "                               \"background\": { \"auto_scale_x\": true, \"type\": \"Rect\", \"colors\": { \"topLeft\": [0.7,0.7,0.7,0.6], \"topRight\": [0.7,0.7,0.7,0.6], \"bottomLeft\": [0.7,0.7,0.7,0.6], \"bottomRight\": [0.7,0.7,0.7,0.6] }}\n" +
            "                             }}\n" +
            "                         }";

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
        String s = ""; for(int i = 0; i < currentOffsetX; i++, s += " ");
        buttonJSON = buttonJSON.replace(
                "ICON_TEXT",
                s + o.object.getName() + " [" + o.object.getClass().getSimpleName() + "]"
        );
        maxXSize = Math.max(maxXSize, (s + o.object.getName() + " [" + o.object.getClass().getSimpleName() + "]").length());
        buttonJSON = buttonJSON.replace("COORD_Y", Integer.toString(currentOffsetY * 10));
        currentOffsetY++;
        subElements.addObject(new Button(o.object.getGlobalName().replace(".", "\n"),
                subElements,
                (JSONObject)JSONValue.parse(buttonJSON)
                ));
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
            ((TextLine) defaultGuiObject.getDescendants().get(0).getDescendants().get(0)).setRenderCursor((int) (maxXSize * offsetX));
            ((TextLine) defaultGuiObject.getDescendants().get(1).getDescendants().get(0)).setRenderCursor((int) (maxXSize * offsetX));
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

    public void GuiBrowserElementClicked(DefaultGuiObject buttonReference, int id) {
        selected = buttonReference; selectedId = id / 10;
    }

    public void GuiBrowserElementHovered(DefaultGuiObject buttonReference, int id) {
        DefaultGuiObject object = (DefaultGuiObject) this.getObjectUp(buttonReference.getName().replace("\n","."));
        hover_rect.setSize(object.getSize());
        hover_rect.setCoordinates(Vector2f.sub(object.getCurrentPosition(), hover_rect.getParentCoordinates(), null));
    }
    public void GuiBrowserElementHoveringStopped(DefaultGuiObject buttonReference, int id) {
        hover_rect.setSize(0,0);
    }
}
