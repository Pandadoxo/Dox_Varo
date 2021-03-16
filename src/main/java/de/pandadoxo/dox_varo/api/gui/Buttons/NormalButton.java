package de.pandadoxo.dox_varo.api.gui.Buttons;


import de.pandadoxo.dox_varo.Main;
import de.pandadoxo.dox_varo.api.gui.Listener.ButtonPressedListener;
import de.pandadoxo.dox_varo.api.gui.Menu.GuiItem;
import de.pandadoxo.dox_varo.api.gui.Menu.GuiMenu;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class NormalButton extends Button {

    private GuiMenu menu;
    private int slot;
    private GuiItem icon;
    private ButtonPressedListener buttonPressedListener;
    private Listener listener;

    public NormalButton(GuiItem icon, GuiMenu menu, int slot, boolean selfdestroy, boolean playsound, ButtonPressedListener buttonPressedListener) {
        super(menu, slot);
        this.icon = icon;
        this.menu = menu;
        this.slot = slot;
        this.buttonPressedListener = buttonPressedListener;


        menu.getInventory().setItem(slot, icon.getItem());

        listener = new Listener() {

            @EventHandler
            private void onInventoryClick(InventoryClickEvent e) {
                if (getMenu() == null)
                    return;
                if (!getMenu().getViewer().equals(e.getWhoClicked()))
                    return;
                if (!e.getInventory().equals(getMenu().getInventory())) {
                    destroy();
                    return;
                }
                boolean contains = false;
                for (Button btn : buttonList)
                    if (btn.getSlot() == e.getSlot() && btn.getMenu() == getMenu()) {
                        contains = true;
                        break;
                    }

                if (e.getSlot() != getSlot()) {
                    if (contains)
                        if (e.getClickedInventory() != null)
                            if (selfdestroy)
                                destroy();
                    return;
                }
                if (!e.getCurrentItem().getType().equals(icon.getType())) {
                    destroy();
                    return;
                }
                if (!(e.getWhoClicked() instanceof Player))
                    return;
                e.setCancelled(true);
                getButtonPressedListener().onPressed(e.getClick(), menu, getIcon(), getSlot());
                if (playsound) {
                    ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.ORB_PICKUP, 1, 2);
                }
                if (selfdestroy)
                    destroy();
                ((Player) e.getWhoClicked()).updateInventory();

            }

            @EventHandler
            public void onItemMove(InventoryMoveItemEvent event) {
                if (event.getItem().equals(icon.getItem())) event.setCancelled(true);
            }
        };

        Bukkit.getPluginManager().registerEvents(listener, Main.getInstance());

    }

    public GuiMenu getMenu() {
        return menu;
    }

    public void setMenu(GuiMenu menu) {
        this.menu = menu;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public GuiItem getIcon() {
        return icon;
    }

    public void setIcon(GuiItem icon) {
        this.icon = icon;
    }

    public ButtonPressedListener getButtonPressedListener() {
        return buttonPressedListener;
    }

    public void setButtonPressedListener(ButtonPressedListener buttonPressedListener) {
        this.buttonPressedListener = buttonPressedListener;
    }

    public void destroy() {
        HandlerList.unregisterAll(listener);
        icon = null;
        menu = null;
        slot = -1;
        buttonPressedListener = null;
        listener = null;
    }

}
