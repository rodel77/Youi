package xyz.rodeldev.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.rodeldev.Helper;
import xyz.rodeldev.YouiPlugin;
import xyz.rodeldev.session.Session;
import xyz.rodeldev.templates.Placeholder;
import xyz.rodeldev.templates.Template;

public class PlaceholderCommand extends ISubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Session session = YouiPlugin.getInstance().getSessionManager().getSession(player.getUniqueId());
        if(session==null){
            Helper.sendMessage(sender, "&cYou are not editing any menu, please use /youi resume <menu> to enter the session");
            return true;
        }

        if(args.length<1){
            session.openPlaceholdersMenu(player);
            return true;
        }

        if(session.getSlotFocus()==-1){
            Helper.sendMessage(sender, "&cYou don't have any slot selected, please shift + right click on a slot to select focus it, then execute this command");
            return true;
        }

        Placeholder placeholder = session.getTemplate().getPlaceholder(args[0]);
        if(placeholder==null){
            Helper.sendMessage(sender, "&cInvalid placeholder, here's the list:");
            session.displayPlaceholderList();
            return true;
        }

        int countPlaceholders = session.getYouiInventory().countPlaceholders(placeholder.getName());
        if(!placeholder.isInRange(countPlaceholders+1)){
            Helper.sendMessage(sender, "&cThis placeholder should appear more than %d but less than %d, now %d", placeholder.getMin(), placeholder.getMax(), countPlaceholders);
            return true;
        }

        if(session.slotHasPlaceholder(placeholder.getName())){
            Helper.sendMessage(sender, "&cThe slot already has this placeholder");
            return true;
        }

        session.getYouiInventory().setPlaceholder(placeholder.getName(), session.getSlotFocus());
        session.focusSlot(-1);
        session.save();
        session.resume(player);
        Helper.sendMessage(sender, "Placeholder set");

        return true;
    }

    @Override
    public void tabComplete(CommandSender sender, String[] args, List<String> result) {
        Player player = (Player) sender;
        Session session = YouiPlugin.getInstance().getSessionManager().getSession(player.getUniqueId());
        if(session!=null && session.getSlotFocus()!=-1){
            Template template = session.getTemplate();
            for(Placeholder placeholder : template.getPlaceholders()){
                if(placeholder.getMax()!=0 && session.getYouiInventory().countPlaceholders(placeholder.getName())+1>placeholder.getMax()) continue;
                
                if(session.slotHasPlaceholder(placeholder.getName())) continue;

                result.add(placeholder.getName());
            }
        }
    }

    @Override
    public String getHelp() {
        return super.getHelp()+" [name] &7(Set placeholder of the selected (shift + right click) slot in the editing menu or visualize the placeholders)";
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }

    @Override
    public String getName() {
        return "placeholder";
    }
}