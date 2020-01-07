package xyz.rodeldev;

import com.google.gson.internal.LazilyParsedNumber;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class Helper {
    public static Inventory createInventory(InventoryType type, int size, String title){
        if(type==InventoryType.CHEST){
            if(title!=null){
                return Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', title));
            }else{
                return Bukkit.createInventory(null, size);
            }
        }else{
            if(title!=null){
                return Bukkit.createInventory(null, type, ChatColor.translateAlternateColorCodes('&',title));
            }else{
                return Bukkit.createInventory(null, type);
            }
        }
    }


    public static void sendMessage(CommandSender sender, String message){
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[Youi] "+message));
    }

    public static void sendMessage(CommandSender sender, String message, Object... args){
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[Youi] "+String.format(message, args)));
    }

    public static Number str2num(String str, Class<?> type){
        try {
            return (Number)type.getDeclaredMethod("valueOf", String.class).invoke(type, str);
        } catch(Exception e){
        }
        return null;
    }
}