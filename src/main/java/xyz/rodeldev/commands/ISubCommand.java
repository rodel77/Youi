package xyz.rodeldev.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

public abstract class ISubCommand {
    abstract public String getName();
    abstract public boolean execute(CommandSender sender, String[] args);
    public String[] getAliases() { return new String[] {}; }
    public boolean onlyPlayer() { return false; }

    public void tabComplete(CommandSender sender, String[] args, List<String> result){ }

    public boolean match(String name) {
        if(getName().equalsIgnoreCase(name)) return true;

        for(String alias : getAliases()) {
            if(getName().equalsIgnoreCase(alias)) return true;
        }

        return false;
    }

    public String getHelp(){
        String aliases[] = getAliases();
        if(aliases.length>0) {
            StringBuilder names = new StringBuilder(getName());

            for(String alias : aliases){
                names.append("|"+alias);
            }

            return "/youi "+names.toString();
        }else{
            return "/youi "+getName();
        }
    }

    public String getPermission(){
        return "youi."+getName();
    }
}