package xyz.rodeldev.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.rodeldev.Helper;
import xyz.rodeldev.YouiPlugin;

public class CloseCommand extends ISubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(YouiPlugin.getInstance().getSessionManager().destroySession(player.getUniqueId())){
            Helper.sendMessage(sender, "Session destroyed");
        }else{
            Helper.sendMessage(sender, "&cNo session to destroy");
        }

        return true;
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }

    @Override
    public String getName() {
        return "close";
    }

    @Override
    public String getHelp() {
        return super.getHelp()+" &7(Close the current editing session)";
    }
}