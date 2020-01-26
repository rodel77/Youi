package xyz.rodeldev.templates;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.plugin.Plugin;

import xyz.rodeldev.YouiPlugin;
import xyz.rodeldev.inventory.CustomMenu;
import xyz.rodeldev.inventory.YouiInventory;

public class TemplateRegistry {
    private static HashMap<String, Template> registry = new HashMap<>();
    private static HashMap<Template, CustomMenu> overrideMap = new HashMap<>();

    public TemplateRegistry(){
        // @TODO: remove this
        // register(new Template(YouiPlugin.getInstance(), "test").registerPlaceholders("button1").registerPlaceholder(new Placeholder("constrainedph").setConstraint(1, 2)).defaultFillInventory(new ItemStack(Material.ACACIA_DOOR)).defaultAddPlaceholder("button1", 1).defaultSetItem(new ItemStack(Material.DIAMOND), 1));
        loadOverrideMap();
    }

    public static void deleteMenu(String menuName){
        for(Entry<Template, CustomMenu> override : overrideMap.entrySet()){
            if(override.getValue().getName().equals(menuName)){
                overrideMap.remove(override.getKey());
                saveOverrideMap();
                loadOverrideMap();
                return;
            }
        }
    }
    
    public static void setOverride(Template template, CustomMenu menu){
        overrideMap.put(template, menu);
    }

    public static void saveOverrideMap(){
        try {
            JsonObject element = new JsonObject();
            for(Entry<Template, CustomMenu> override : overrideMap.entrySet()) {
                element.addProperty(override.getKey().getFullName(), override.getValue().getName());
            }

            File file = YouiPlugin.getInstance().getFileSystem().getOverrideFile();
            try(FileOutputStream outputStream = new FileOutputStream(file)){
                outputStream.write(element.toString().getBytes());
            }catch(Exception e){
                e.printStackTrace();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void loadOverrideMap(){
        try(FileReader reader = new FileReader(YouiPlugin.getInstance().getFileSystem().getOverrideFile())){
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(reader);
            JsonObject jsonObject = element.getAsJsonObject();
            overrideMap.clear();
            for(Entry<String, JsonElement> override : jsonObject.entrySet()){
                Template template = get(override.getKey());
                if(template==null){
                    YouiPlugin.getInstance().getLogger().log(Level.WARNING, "Can't found template \""+override.getKey()+"\" while loading override map.");
                    continue;
                }

                File menuFile = YouiPlugin.getInstance().getFileSystem().getMenu(override.getValue().getAsString());
                if(!menuFile.exists()){
                    YouiPlugin.getInstance().getLogger().log(Level.WARNING, "Can't found menu file called \""+override.getValue().getAsString()+"\" while loading overriding of template \""+template.getFullName()+"\"");
                    continue;
                }

                try(FileReader reader2 = new FileReader(menuFile)){
                    JsonElement menuElement = parser.parse(reader2);
                    JsonObject menuObject = menuElement.getAsJsonObject();
                    YouiInventory youiInventory = new YouiInventory();
                    youiInventory.deserialize(menuObject);
                    overrideMap.put(template, youiInventory);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static HashMap<Template, CustomMenu> getOverrideMap(){
        return overrideMap;
    }

    public static ImmutableList<Template> getRegistry(){
        return ImmutableList.copyOf(registry.values());
    }

    public static Template get(String fullname){
        return registry.get(fullname);
    }

    public static Template get(Plugin owner, String name){
        return get(owner.getName().toLowerCase().replace(" ", "_")+":"+name);
    }

    public static void register(Template template){
        try{
            if(registry.containsKey(template.getFullName())){
                throw new Exception("Template \""+template.getFullName()+"\" is already registered");
            }

            registry.put(template.getFullName(), template);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void register(Template... templates){
        for(Template template : templates){
            register(template);
        }
    }
}