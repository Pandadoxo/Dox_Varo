package de.pandadoxo.dox_varo.api.gui.Buttons;


import de.pandadoxo.dox_varo.api.gui.Menu.GuiMenu;

import java.util.ArrayList;
import java.util.List;

public abstract class Button {

    public static List<Button> buttonList = new ArrayList<>();
    private GuiMenu menu;
    private int slot;

    public Button(GuiMenu menu, int slot) {
        buttonList.add(this);
    }

    public GuiMenu getMenu() {
        return menu;
    }

    public int getSlot() {
        return slot;
    }

    public void destroy() {
        buttonList.remove(this);
    }
}
