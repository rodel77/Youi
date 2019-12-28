package xyz.rodeldev.session;

import java.io.File;
import java.io.FileReader;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import xyz.rodeldev.YouiPlugin;
import xyz.rodeldev.inventory.YouiInventory;
import xyz.rodeldev.templates.Template;

public class Session {
    private YouiInventory youiInventory;
    
    public Session(){
        youiInventory = new YouiInventory();
    }

    public void setTemplate(Template template){
        this.youiInventory.setTemplate(template);
    }

    public Template getTemplate(){
        return this.youiInventory.getTemplate();
    }

    public void setName(String name){
        this.youiInventory.setName(name);
    }

    public YouiInventory getYouiInventory(){
        return youiInventory;
    }

    public Inventory getInventory(){
        return youiInventory.getInventory();
    }

    public void createInventory(){
        youiInventory.createInventory();
    }

    public void resume(Player player){
        player.openInventory(getInventory());
    }

    public void load(File file){
        try {
            setName(file.getName().replace(".json", ""));
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(new FileReader(file));
            JsonObject jsonObject = element.getAsJsonObject();
            youiInventory.deserialize(jsonObject);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void load(String name, String json){
        try {
            setName(name);
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(json);
            JsonObject jsonObject = element.getAsJsonObject();
            youiInventory.deserialize(jsonObject);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void save(){
        YouiPlugin.getInstance().getFileSystem().saveMenu(youiInventory.getName(), youiInventory.serialize().toString());
    }
}