package xyz.rodeldev.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonSyntaxException;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.rodeldev.Helper;
import xyz.rodeldev.YouiPlugin;
import xyz.rodeldev.inventory.PlaceholderInstance;
import xyz.rodeldev.session.Session;
import xyz.rodeldev.templates.Placeholder;

public class PlaceholderCommand extends ISubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        Session session = YouiPlugin.getInstance().getSessionManager().getSession(player.getUniqueId());
        if(session==null){
            Helper.sendMessage(sender, "&cYou are not editing any menu, please use /youi resume <menu> to enter the session");
            return true;
        }

        if(args.length<1) return false;

        SubAction action = null;
        for(SubAction a : SubAction.values()){
            if(a.name().equalsIgnoreCase(args[0])){
                action = a;
                break;
            }
        }

        if(action==null || (action!=SubAction.LIST && action!=SubAction.CLEAR && args.length<2)) return false;

        ItemStack item = null;

        switch(action) {
            case LIST:
                session.displayPlaceholderList();
                return true;
            case HELP:
                for(Placeholder placeholder : session.getTemplate().getPlaceholders()){
                    if(placeholder.getName().equalsIgnoreCase(args[1])){
                        session.displayPlaceholderInfo(placeholder);
                        return true;
                    }
                }
                Helper.sendMessage(sender, "&cInvalid placeholder, use \"/youi placeholder list\" to see the complete list");
                return true;
            case ADD:
            case REMOVE:
            case CLEAR:
                item = player.getItemInHand();
                if(item==null || item.getType()==Material.AIR){
                    Helper.sendMessage(sender, "&cNo item in hand");
                    return true;
                }
                break;
        }

        if(action==SubAction.CLEAR){
            Helper.stripPlaceholders(item);
            return true;
        }

        List<PlaceholderInstance> placeholders = Helper.getPlaceholders(item);
        if(placeholders==null) placeholders = new ArrayList<>();


        PlaceholderInstance placeholderArg;
        try {
            placeholderArg = PlaceholderInstance.fromPlain(args[1]);
        } catch (JsonSyntaxException e){
            Helper.sendMessage(sender, "&cPlaceholder data syntax error: "+e.getMessage());
            return true;
        }

        switch(action){
            case ADD:
                if(session.getTemplate().getPlaceholder(placeholderArg.getPlaceholderName())==null){
                    Helper.sendMessage(sender, "&cThis placeholder doesn't not exists, please use /youi placeholder list");
                    return true;
                }

                for(PlaceholderInstance placeholder : placeholders){
                    if(placeholder.getPlaceholderName().equalsIgnoreCase(placeholderArg.getPlaceholderName())){
                        Helper.sendMessage(sender, "&cThis placeholder is already added");
                        return true;
                    }
                }

                placeholders.add(placeholderArg);
                Helper.sendMessage(sender, "&aPlaceholder %s added", args[1]);

                break;
            case REMOVE:
                boolean removed = false;
                for(int i = 0; i < placeholders.size(); i++){
                    if(placeholders.get(i).getPlaceholderName().equalsIgnoreCase(placeholderArg.getPlaceholderName())){
                        placeholders.remove(i);
                        Helper.sendMessage(sender, "&aPlaceholder %s removed", args[1]);
                        removed = true;
                        break;
                    }
                }

                if(!removed){
                    Helper.sendMessage(sender, "&cThere is not such \"%s\" placeholder", args[1]);
                    return true;
                }
                break;
            default: break;
        }

        Helper.setPlaceholders(item, placeholders);

        return true;
    }

    @Override
    public void tabComplete(CommandSender sender, String[] args, List<String> result) {
        Player player = (Player) sender;
        if(args.length==1){
            Arrays.stream(SubAction.values()).map(SubAction::name).map(String::toLowerCase).forEach(str -> result.add(str));
        }else if(args.length==2){
            Session session = YouiPlugin.getInstance().getSessionManager().getSession(player.getUniqueId());
            if(session==null) return;
            for(SubAction subAction : SubAction.values()){
                if(subAction.name().equalsIgnoreCase(args[0])){
                    if(subAction==SubAction.ADD || subAction==SubAction.REMOVE || subAction==SubAction.LIST){
                        session.getTemplate().getPlaceholders().stream().map(Placeholder::getName).forEach(rs -> result.add(rs));
                    }
                    return;
                }
            }
        }
    }

    @Override
    public String getHelp() {
        return super.getHelp()+" <"+Arrays.stream(SubAction.values()).map(SubAction::name).map(String::toLowerCase).collect(Collectors.joining("|"))+"> [placeholder-name][placeholder-data]";
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }

    @Override
    public String getName() {
        return "placeholder";
    }

    enum SubAction {
        ADD, REMOVE, CLEAR, LIST, HELP;
    }
}