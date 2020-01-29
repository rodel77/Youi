package xyz.rodeldev.commands;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import xyz.rodeldev.Helper;
import xyz.rodeldev.YouiPlugin;
import xyz.rodeldev.templates.Template;
import xyz.rodeldev.templates.TemplateRegistry;

public class ListCommand extends ISubCommand {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if(args.length>0){
            if(args[0].equalsIgnoreCase("templates")){
                Helper.sendMessage(sender, "List of templates:");

                Plugin pluginSelector = null;
                if(args.length>1){
                    pluginSelector = Bukkit.getPluginManager().getPlugin(args[1]);
                    if(pluginSelector==null){
                        Helper.sendMessage(sender, "&cThis plugin doesn't exists");
                        return true;
                    }
                }

                for(Template template : TemplateRegistry.getRegistry()){
                    if(pluginSelector!=null && template.getOwner()!=pluginSelector) continue;
                    Helper.sendMessage(sender, "%s &8%s", template.getFullName(), template.getDescription());
                }

                return true;
            }else if(args[0].equalsIgnoreCase("menus")){
                Helper.sendMessage(sender, "List of menus:");
                for(String fileName : YouiPlugin.getInstance().getFileSystem().getMenusFolder().list()){
                    File file = new File(YouiPlugin.getInstance().getFileSystem().getMenusFolder(), fileName);
                    try(FileReader reader = new FileReader(file)){
                        String menuName = fileName.replace(".json", "");
                        JsonParser parser = new JsonParser();
                        JsonObject json = parser.parse(reader).getAsJsonObject();
                        Template template = TemplateRegistry.get(json.get("template").getAsString());
                        if(template==null){
                            Helper.sendMessage(sender, "Menu %s &cINVALID TEMPLATE", menuName);
                        }else{
                            Helper.sendMessage(sender, "Menu %s, template: %s", menuName, template.getFullName());
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void tabComplete(CommandSender sender, String[] args, List<String> result) {
        if(args.length==1){
            result.add("menus");
            result.add("templates");
        }else if(args.length==2){
            result.addAll(Arrays.asList(Bukkit.getPluginManager().getPlugins()).stream().map(Plugin::getName).collect(Collectors.toList()));
        }
    }

    @Override
    public String getHelp() {
        return super.getHelp()+" <templates|menus> [plugin] (List templates of menus)";
    }

    @Override
    public String getName() {
        return "list";
    }
}