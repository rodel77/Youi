package xyz.rodeldev.commands;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.rodeldev.Helper;
import xyz.rodeldev.YouiPlugin;
import xyz.rodeldev.inventory.CustomMenu;
import xyz.rodeldev.session.Session;
import xyz.rodeldev.templates.Template;
import xyz.rodeldev.templates.TemplateRegistry;

public class OverrideCommand extends ISubCommand{
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length==0){
            if(!(sender instanceof Player)) {
                Helper.sendMessage(sender, "&cOverriding function is only for players!");
                return true;
            }
            Player player = (Player) sender;
            Session session = YouiPlugin.getInstance().getSessionManager().getSession(player.getUniqueId());
            if(session!=null){
                TemplateRegistry.setOverride(session.getTemplate().getFullName(), session.getYouiInventory().getName());
                Helper.sendMessage(sender, "\"%s\" is now overriding \"%s\"!", session.getYouiInventory().getName(), session.getTemplate().getFullName());
            }else{
                Helper.sendMessage(sender, "&cYou are not editing any menu!");
            }
        }else{
            ImmutableList<Template> templates = TemplateRegistry.getRegistry();

            Template template = null;
            for(Template it : templates){
                if(it.getFullName().equalsIgnoreCase(args[0]) || it.getName().equalsIgnoreCase(args[0])){
                    template = it;
                    break;
                }
            }
    
            if(template==null){
                Helper.sendMessage(sender, "&cInvalid template, valids: "+templates.stream().map(Template::getFullName).collect(Collectors.joining(", ")));
                return true;
            }else{
                CustomMenu menu = template.getOverride(false);
                if(menu==null){
                    Helper.sendMessage(sender, "This template is not being overrided");
                }else{
                    Helper.sendMessage(sender, "This template is being overrided by menu &6\"%s\"", menu.getName());
                }
            }
        }
        return true;
    }

    @Override
    public void tabComplete(CommandSender sender, String[] args, List<String> result) {
        if(args.length==1){
            result.addAll(TemplateRegistry.getRegistry().stream().map(Template::getFullName).collect(Collectors.toList()));
        }
    }

    @Override
    public String getHelp() {
        return super.getHelp()+" [template-name] &7(See what menu is overriding that template or set the one you are editing as override)";
    }

    @Override
    public String getName() {
        return "override";
    }
}