package xyz.rodeldev;

import com.google.gson.internal.LazilyParsedNumber;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Helper {
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