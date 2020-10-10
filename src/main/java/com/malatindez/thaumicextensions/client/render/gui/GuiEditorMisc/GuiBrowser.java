package com.malatindez.thaumicextensions.client.render.gui.GuiEditorMisc;

import com.malatindez.thaumicextensions.client.render.misc.gui.Button;
import com.malatindez.thaumicextensions.client.render.misc.gui.DefaultGuiObject;
import com.malatindez.thaumicextensions.client.render.misc.gui.EnhancedGuiScreen;
import com.malatindez.thaumicextensions.client.render.misc.gui.ScrollBar;
import net.minecraft.client.Minecraft;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.lwjgl.util.vector.Vector2f;
import scala.util.hashing.Hashing;

import java.util.ArrayList;

public class GuiBrowser extends DefaultGuiObject implements EnhancedGuiScreen.Clickable, ScrollBar.Scrollable,
        EnhancedGuiScreen.Updatable {
    public GuiBrowser(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }
    Object gui_ref;
    long lastUpdate = 0;
    public DefaultGuiObject selected = null;
    protected int selectedId = 0;

    protected class Tree implements Comparable<Tree> {
        public DefaultGuiObject object;
        public ArrayList<Tree> descendants;
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

    Tree currentTree = null;
    /*
"{ \"type\": \"Button\"," +
        "\"coordinates\": [COORD_X, COORD_Y]," + // both coord_x and coord_y should be replaced
        "\"clicked\": { \"object_name\": \"OBJECT_NAME\", \"method_name\": \"GuiBrowserElementClicked\" }," +
        "" +*/
    private void updateDescendants(Tree o, int currentOffset) {
      //  this.descendants.add(new Button(o.object.getName() + "_BUTTON",
     //           this,
     //           (JSONObject)JSONValue.parse()
     //           ))
    }

    @Override
    public void Update(int flags) {
        if(lastUpdate + 500 > Minecraft.getSystemTime()) {
            return;
        }
        lastUpdate = Minecraft.getSystemTime();
        Tree newTree = new Tree((DefaultGuiObject) gui_ref);
        if(newTree != currentTree) {
            currentTree = newTree;
            this.descendants.clear();
        }
    }

    @Override
    public void postInit() {
        gui_ref = getObjectUp("new_gui");
        currentTree = new Tree((DefaultGuiObject) gui_ref);
        Update(0);
    }

    @Override
    public boolean mouseHandler(Vector2f currentMousePosition) {
        return mouseHandlerDescendants(currentMousePosition);
    }

    @Override
    public boolean mouseClicked(Vector2f currentMousePosition, int button) {
        return mouseClickedDescendants(currentMousePosition, button);
    }

    @Override
    public void setOffsetX(float offset) {

    }

    @Override
    public void setOffsetY(float offset) {

    }

    @Override
    public float getScaleX() {
        return 1;
    }

    @Override
    public float getScaleY() {
        return 1;
    }

    void GuiBrowserElementClicked(DefaultGuiObject buttonReference, int id) {
        selected = buttonReference; selectedId = id;
    }
}
