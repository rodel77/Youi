package xyz.rodeldev;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Helper {
    public static void sendMessage(CommandSender sender, String message){
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[Youi] "+message));
    }
}