package xyz.rodeldev.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.rodeldev.Helper;
import xyz.rodeldev.YouiPlugin;
import xyz.rodeldev.session.Session;

public class ClearPlaceholdersCommand extends ISubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Session session = YouiPlugin.getInstance().getSessionManager().getSession(player.getUniqueId());
        if(session==null){
            Helper.sendMessage(sender, "&cYou are not editing any menu, please use /youi resume <menu> to enter the session");
            return true;
        }

        if(session.getSlotFocus()==-1){
            Helper.sendMessage(sender, "&cYou don't have any slot selected, please shift + right click on a slot to select focus it, then execute this command");
            return true;
        }

        session.getYouiInventory().removePlaceholders(session.getSlotFocus());
        Helper.sendMessage(sender, "Slot cleared from placeholders");

        return true;
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }

    @Override
    public String getName() {
        return "clearplaceholders";
    }

    @Override
    public String getHelp() {
        return super.getHelp()+" &7(Clear the placeholders from the selected slot)";
    }
}