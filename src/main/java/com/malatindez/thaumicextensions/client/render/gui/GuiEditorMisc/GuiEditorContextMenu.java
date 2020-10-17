package com.malatindez.thaumicextensions.client.render.gui.GuiEditorMisc;

import com.malatindez.thaumicextensions.client.render.misc.gui.Collection;
import com.malatindez.thaumicextensions.client.render.misc.gui.ScrollBar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.json.simple.JSONObject;

@SideOnly(Side.CLIENT)
public class GuiEditorContextMenu extends Collection implements ScrollBar.Scrollable {
    public GuiEditorContextMenu(String name, Object parent, JSONObject parameters) {
        super(name, parent, parameters);
    }

    @Override
    public void setOffsetX(float offset) {
       // offsetX = offset;
      //  updateHideStates();
    }

    @Override
    public void setOffsetY(float offset) {
        //offsetY = offset;
       // updateHideStates();
    }

    @Override
    public float getOffsetX() {
        return 0;
    }

    @Override
    public float getOffsetY() {
        return 0;
    }

}
