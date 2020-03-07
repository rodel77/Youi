package xyz.rodeldev.templates;

import java.io.File;
import java.io.FileNotFoundException;
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

import xyz.rodeldev.Helper;
import xyz.rodeldev.YouiPlugin;
import xyz.rodeldev.inventory.CustomMenu;
import xyz.rodeldev.inventory.YouiInventory;

public class TemplateRegistry {
    private static HashMap<String, Template> registry = new HashMap<>();
    private static HashMap<String, String> overrideFile = new HashMap<>();
    private static HashMap<String, CustomMenu> _overrideMap = new HashMap<>();


    public TemplateRegistry(){
        _loadOverrideMap();
    }

    public static void deleteMenu(String menuName){
        for(Entry<String, String> entry : overrideFile.entrySet()){
            if(entry.getValue().equals(menuName)){
                overrideFile.remove(entry.getKey());
                saveOverrideMap();
                _overrideMap.remove(entry.getKey());
                return;
            }
        }
    }
    
    public static void setOverride(String templateFullName, String menuName){
        overrideFile.put(templateFullName, menuName);
        saveOverrideMap();
        updateOverride(templateFullName);
    }

    public static void saveOverrideMap(){
        File file = YouiPlugin.getInstance().getFileSystem().getOverrideFile();
        try(FileOutputStream outputStream = new FileOutputStream(file)){
            JsonObject json = new JsonObject();
            for(Entry<String, String> entry : overrideFile.entrySet()){
                json.addProperty(entry.getKey(), entry.getValue());
            }
            outputStream.write(json.toString().getBytes());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static CustomMenu getOverride(String templateFullName){
        return _overrideMap.get(templateFullName);
    }

    private static String updateOverride(String templateFullName){
        _overrideMap.put(templateFullName, null);

        String menuName = overrideFile.get(templateFullName);
        if(menuName!=null){
            try(FileReader reader = new FileReader(YouiPlugin.getInstance().getFileSystem().getMenu(menuName))){
                JsonObject jsonMenu = Helper.gson.fromJson(reader, JsonObject.class);
                YouiInventory youiInventory = new YouiInventory();
                youiInventory.setName(menuName);
                youiInventory.deserialize(jsonMenu);
                _overrideMap.put(templateFullName, youiInventory);
                return null;
            }catch(FileNotFoundException e){
                return "This menu doesn't exists";
            }catch(Exception e){
                e.printStackTrace();
                return "Internal Error: "+e.getMessage();
            }
        }

        return "Invalid menu";
    }

    public static void _loadOverrideMap(){
        try(FileReader reader = new FileReader(YouiPlugin.getInstance().getFileSystem().getOverrideFile())){
            JsonObject overrideFile = Helper.gson.fromJson(reader, JsonObject.class);
            for(Entry<String, JsonElement> template : overrideFile.entrySet()){
                TemplateRegistry.overrideFile.put(template.getKey(), template.getValue().getAsString());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
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
            updateOverride(template.getFullName());
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