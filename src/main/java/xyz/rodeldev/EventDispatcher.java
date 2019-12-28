package xyz.rodeldev;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import xyz.rodeldev.session.Session;

public class EventDispatcher implements Listener {
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        Session session = YouiPlugin.getInstance().getSessionManager().getSession(e.getPlayer().getUniqueId());
        if(session!=null && session.getInventory()==e.getInventory()){
            session.save();
        }
    }
}