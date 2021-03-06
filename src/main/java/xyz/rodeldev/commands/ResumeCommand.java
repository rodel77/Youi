package xyz.rodeldev.commands;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import xyz.rodeldev.Helper;
import xyz.rodeldev.YouiPlugin;
import xyz.rodeldev.session.Session;

public class ResumeCommand extends ISubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;

        if(args.length>0){
            File file = YouiPlugin.getInstance().getFileSystem().getMenu(args[0]);

            Session locker = YouiPlugin.getInstance().getSessionManager().getMenuSession(args[0]);
            if(locker!=null && locker.getOwner()!=player){
                Helper.sendMessage(sender, "&cThis menu is beign edited by "+locker.getOwner().getName());
                return true;
            }

            if(file.exists()){
                Session session = YouiPlugin.getInstance().getSessionManager().pushSession(player);
                session.load(file);
                session.resume(player);
            }else{
                Helper.sendMessage(sender, "&cNo menu found, list of menus: "+YouiPlugin.getInstance().getFileSystem().listMenuNames().stream().collect(Collectors.joining(", ")));
            }
            return true;
        }

        Session session = YouiPlugin.getInstance().getSessionManager().getSession(player.getUniqueId());
        if(session!=null){
            session.resume(player);
            Helper.sendMessage(sender, "Session resumed");
        }else{
            Helper.sendMessage(sender, "&cNo session to resume, use the second argument to open one of these: "+Arrays.asList(YouiPlugin.getInstance().getFileSystem().getMenusFolder().listFiles()).stream().map(streamFile -> {
                return streamFile.getName().replace(".json", "");
            }).collect(Collectors.joining(", ")));
        }

        return true;
    }

    @Override
    public void tabComplete(CommandSender sender, String[] args, List<String> result) {
        if(args.length==1){
            result.addAll(YouiPlugin.getInstance().getFileSystem().listMenuNames());
        }
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }

    @Override
    public String getName() {
        return "resume";
    }

    @Override
    public String getHelp() {
        return super.getHelp()+" &7(Resume the last editing menu or open a new one)";
    }
}