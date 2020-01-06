package xyz.rodeldev.commands;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.bukkit.command.CommandSender;

import xyz.rodeldev.Helper;
import xyz.rodeldev.YouiPlugin;
import xyz.rodeldev.session.Session;
import xyz.rodeldev.templates.TemplateRegistry;

public class DeleteCommand extends ISubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length>0){
            File file = YouiPlugin.getInstance().getFileSystem().getMenu(args[0]);

            Session locker = YouiPlugin.getInstance().getSessionManager().getMenuSession(args[0]);
            if(locker!=null){
                if(locker.getOwner()==sender){
                    YouiPlugin.getInstance().getSessionManager().destroySession(locker.getOwner().getUniqueId());
                }else{
                    Helper.sendMessage(sender, "&c"+locker.getOwner().getName()+" is editing this menu!");
                    return true;
                }
            }

            if(file.exists()){
                try {
                    Files.delete(file.toPath());
                }catch(Exception e){
                    e.printStackTrace();
                }
                TemplateRegistry.deleteMenu(args[0]);
                Helper.sendMessage(sender, "Menu %s deleted!", args[0]);
            }else{
                Helper.sendMessage(sender, "&cThat menu doesn't exists");
            }
            return true;
        }

        return false;
    }

    @Override
    public void tabComplete(CommandSender sender, String[] args, List<String> result) {
        if(args.length==1){
            result.addAll(YouiPlugin.getInstance().getFileSystem().listMenuNames());
        }
    }

    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getHelp() {
        return super.getHelp()+" <menu> &7(Delete menu)";
    }
}