package xyz.rodeldev.commands;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.rodeldev.Helper;
import xyz.rodeldev.YouiPlugin;
import xyz.rodeldev.session.Session;
import xyz.rodeldev.templates.Template;
import xyz.rodeldev.templates.TemplateRegistry;

public class OverrideCommand extends ISubCommand{
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        if(args.length==0){
            Session session = YouiPlugin.getInstance().getSessionManager().getSession(player.getUniqueId());
            if(session!=null){
                TemplateRegistry.setOverride(session.getTemplate(), session.getYouiInventory());
                TemplateRegistry.saveOverrideMap();
                TemplateRegistry.loadOverrideMap();
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
    public boolean onlyPlayer() {
        return true;
    }

    @Override
    public String getHelp() {
        return super.getHelp()+" [template-name] &7(See what menu is overriding that template or set the current one for editing)";
    }

    @Override
    public String getName() {
        return "override";
    }
}