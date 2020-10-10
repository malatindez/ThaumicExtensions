package com.malatindez.thaumicextensions.client.render.gui.GuiEditorMisc;

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
    /*
    "button": { "type": "Button", "coordinates": [0, COORD_Y], "size": [67, 10], "id": 11, "zLevel": COORD_Y, "size_scale_type": "scale_x", "coordinates_scale_type": "scale_x",
            "clicked": { "object_name": "OBJECT_NAME", "method_name": "GuiBrowserElementClicked" },
            "icon": "ICON_NAME", "hovered_icon": "HOVERED_ICON_NAME",
            "ICON_NAME": {
              "type": "Collection",
              "elements": {
                "ICON_NAME_TEXT": { "zLevel": 1, "type": "TextLine", "text": "ICON_TEXT", "dropShadow": true },
                "ICON_NAME_BACKGROUND": { "type": "Rect", "colors": { "topLeft": [0.3,0.3,0.3,0.5], "topRight": [0.3,0.3,0.3,0.5], "bottomLeft": [0.3,0.3,0.3,0.5], "bottomRight": [0.3,0.3,0.3,0.5] }}
              }},
            "HOVERED_ICON_NAME": {
              "type": "Collection",
              "elements": {
                "HOVERED_ICON_NAME_TEXT": { "zLevel": 1, "type": "TextLine", "text": "ICON_TEXT", "dropShadow": true },
                "HOVERED_ICON_NAME_BACKGROUND": { "type": "Rect", "colors": { "topLeft": [0.7,0.7,0.7,0.6], "topRight": [0.7,0.7,0.7,0.6], "bottomLeft": [0.7,0.7,0.7,0.6], "bottomRight": [0.7,0.7,0.7,0.6] }}
              }}
          }
     */
    String defaultButtonText =
            "{ \"type\": \"Button\", \"coordinates\": [0, COORD_Y], \"size\": [67, 10], \"id\": 11, \"zLevel\": COORD_Y, \"size_scale_type\": \"scale_x\", \"coordinates_scale_type\": \"scale_x\",\n" +
                    "        \"clicked\": { \"object_name\": \"OBJECT_NAME\", \"method_name\": \"GuiBrowserElementClicked\" },\n" +
                    "        \"icon\": \"ICON_NAME\", \"hovered_icon\": \"HOVERED_ICON_NAME\",\n" +
                    "        \"ICON_NAME\": {\n" +
                    "          \"type\": \"Collection\",\n" +
                    "          \"elements\": {\n" +
                    "            \"ICON_NAME_TEXT\": { \"zLevel\": 1, \"type\": \"TextLine\", \"text\": \"ICON_TEXT\", \"dropShadow\": true },\n" +
                    "            \"ICON_NAME_BACKGROUND\": { \"type\": \"Rect\", \"colors\": { \"topLeft\": [0.3,0.3,0.3,0.5], \"topRight\": [0.3,0.3,0.3,0.5], \"bottomLeft\": [0.3,0.3,0.3,0.5], \"bottomRight\": [0.3,0.3,0.3,0.5] }}\n" +
                    "          }},\n" +
                    "        \"HOVERED_ICON_NAME\": {\n" +
                    "          \"type\": \"Collection\",\n" +
                    "          \"elements\": {\n" +
                    "            \"HOVERED_ICON_NAME_TEXT\": { \"zLevel\": 1, \"type\": \"TextLine\", \"text\": \"ICON_TEXT\", \"dropShadow\": true },\n" +
                    "            \"HOVERED_ICON_NAME_BACKGROUND\": { \"type\": \"Rect\", \"colors\": { \"topLeft\": [0.7,0.7,0.7,0.6], \"topRight\": [0.7,0.7,0.7,0.6], \"bottomLeft\": [0.7,0.7,0.7,0.6], \"bottomRight\": [0.7,0.7,0.7,0.6] }}\n" +
                    "          }}\n" +
                    "      }";

    public GuiBrowser(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }
    Object gui_ref;
    long lastUpdate = 0;
    public DefaultGuiObject selected = null; // reference
    public Collection subElements = null; // reference to gui_editor_browser_elements
    public DefaultGuiObject background = null; // reference to gui_editor_browser_background
    protected int selectedId = 0;

    Tree currentTree = null;

    private int currentOffsetY = 0;
    private int maxXSize = 0;
    private void updateDescendants(Tree o, int currentOffsetX) {
        String buttonJSON = defaultButtonText;
        String s = ""; for(int i = 0; i < currentOffsetX; i++, s += " ");
        buttonJSON = buttonJSON.replace(
                "ICON_NAME",
                o.object.getName() + "_BUTTON_ICON"
        );
        buttonJSON = buttonJSON.replace(
                "ICON_TEXT",
                s + o.object.getName() + " [" + o.object.getClass().getSimpleName() + "]"
        );
        maxXSize = Math.max(maxXSize, (s + o.object.getName() + " [" + o.object.getClass().getSimpleName() + "]").length());
        buttonJSON = buttonJSON.replace("COORD_Y", Integer.toString(currentOffsetY * 10));
        currentOffsetY++;
        subElements.addObject(new Button(o.object.getName() + "_BUTTON",
                this,
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
        subElements.setCoordinates(0,
                Math.min(0, -(-background.getSize().y +
                        subElements.getDescendants().size() *
                                subElements.getDescendants().get(0).getSize().y) * offsetY));
        for(DefaultGuiObject defaultGuiObject : subElements.getDescendants()) {
            if(defaultGuiObject.getCurrentPosition().y < - 1.0f / 24.0f * currentResolution.y
                    || defaultGuiObject.getCurrentPosition().y > background.getBorders().w) {
                defaultGuiObject.hide();
            } else {
                defaultGuiObject.show();
            }
            ((TextLine) defaultGuiObject.getDescendants().get(0).getDescendants().get(0)).setRenderCursor((int) (maxXSize * offsetX));
        }
    }
    @Override
    public void postInit() {
        gui_ref = getObjectUp("new_gui");
        subElements = (Collection) getObjectDown("gui_editor_browser_elements");
        background = (DefaultGuiObject) getObjectDown("gui_editor_browser_background");
        super.postInit();
        updateTree(new Tree((DefaultGuiObject) gui_ref));
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

    void GuiBrowserElementClicked(DefaultGuiObject buttonReference, int id) {
        selected = buttonReference; selectedId = id;
    }
}
