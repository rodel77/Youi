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

public class CreateCommand extends ISubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length<2){
            return false;
        }

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

        String name = args[1].replace(" ", "_");

        if(YouiPlugin.getInstance().getFileSystem().getMenu(name).exists()){
            Helper.sendMessage(sender, "&cThere is already a menu called like that");
            return true;
        }

        Player player = (Player) sender;

        Session session = YouiPlugin.getInstance().getSessionManager().pushSession(player);
        session.setName(name.replace(" ", "_"));
        session.setTemplate(template);
        session.createInventory();
        session.setDefaults();
        session.resume(player);
        session.save();

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
        return super.getHelp()+" <template> <name> &7(Create a new menu from a template)";
    }

    @Override
    public String getName() {
        return "create";
    }
}