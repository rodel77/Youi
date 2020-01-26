package xyz.rodeldev;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import xyz.rodeldev.session.Session;

public class EventDispatcher implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        YouiPlugin.getInstance().getSessionManager().destroySession(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        Session session = YouiPlugin.getInstance().getSessionManager().getSession(e.getPlayer().getUniqueId());
        if(session!=null && session.getInventory()==e.getInventory()){
            session.save();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getWhoClicked() instanceof Player){
            Player player = (Player) e.getWhoClicked();
            Session session = YouiPlugin.getInstance().getSessionManager().getSession(player.getUniqueId());
            if(session!=null){
                if(session.getInventory()==e.getInventory() && e.getClick()==ClickType.SHIFT_RIGHT){
                    ItemStack item = e.getCurrentItem();
                    if(item==null){
                        Helper.sendMessage(player, "&cThere is not item to select!");
                    }else{
                        e.setCancelled(true);
                        player.closeInventory();
                        session.focusSlot(e.getSlot());
                        Helper.sendMessage(player, "You focused slot %d to mark it as a placeholder, this is the list of placeholders (use /youi placeholder <name>):", e.getSlot());
                        session.displayPlaceholderList();
                        Helper.sendMessage(player, "&6You can also clear all the placeholders from this slot using /youi clearplaceholders", e.getSlot());
                    }
                }else if(session.getPlaceholderInventory()!=null && session.getPlaceholderInventory()==e.getInventory()){
                    e.setCancelled(true);
                    if(!session.getYouiInventory().getPlaceholdersIn(e.getSlot()).isEmpty()){
                        session.getYouiInventory().removePlaceholders(e.getSlot());
                        session.refreshPlaceholders();
                        session.save();
                        Helper.sendMessage(player, "Slot cleared from placeholders");
                    }
                }
            }
        }
    }
}