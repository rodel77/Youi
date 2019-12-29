package xyz.rodeldev.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import xyz.rodeldev.Helper;

public class YouiCommand implements CommandExecutor, TabCompleter {
    private List<ISubCommand> commands = new ArrayList<>();

    public YouiCommand(){
        commands.add(new CreateCommand());
        commands.add(new ResumeCommand());
        commands.add(new OptionCommand());
        commands.add(new SetPlaceholderCommand());
        commands.add(new ListCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length<1) {
            displayHelp(sender);
            return true;
        }
    
        String subCommandName = args[0];

        for(ISubCommand subCommand : commands){
            if(subCommand.match(subCommandName)) {
                if(!sender.hasPermission(subCommand.getPermission())){
                    Helper.sendMessage(sender, "&cNo permission to execute this command");
                    return true;
                }

                if(subCommand.onlyPlayer() && !(sender instanceof Player)){
                    Helper.sendMessage(sender, "&cThis command is only for players");
                    return true;
                }

                String subCommandArgs[];
                if(args.length>1){
                    subCommandArgs = new String[args.length-1];
                    System.arraycopy(args, 1, subCommandArgs, 0, subCommandArgs.length);
                }else{
                    subCommandArgs = new String[] {};
                }

                if(!subCommand.execute(sender, subCommandArgs)){
                    Helper.sendMessage(sender, subCommand.getHelp());
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> results = new ArrayList<>();

        if(args.length==1) {
            for(ISubCommand subCommand : commands){
                if(subCommand.onlyPlayer() && !(sender instanceof Player)) continue;
                results.add(subCommand.getName());
            }
        }else if(args.length>1){
            String subCommandName = args[0];
            for(ISubCommand subCommand : commands){
                if(subCommand.match(subCommandName)) {
                    if(subCommand.onlyPlayer() && !(sender instanceof Player)) continue;
                    String[] subCommandArgs = new String[args.length-1];
                    System.arraycopy(args, 1, subCommandArgs, 0, subCommandArgs.length);
                    if(subCommand.match(subCommandName)){
                        subCommand.tabComplete(sender, subCommandArgs, results);
                        break;
                    }
                }
            }
        }

        String last = args[args.length-1];
        if(!last.isEmpty()){
            for(int i = results.size() -1; i >= 0; i--){
                if(!results.get(i).toLowerCase().startsWith(last.toLowerCase())){
                    results.remove(i);
                }
            }
        }

        return results;
    }

    public void displayHelp(CommandSender sender){
        for(ISubCommand subCommand : commands){
            Helper.sendMessage(sender, subCommand.getHelp());
        }
    }
}