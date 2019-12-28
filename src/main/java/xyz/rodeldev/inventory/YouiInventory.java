package xyz.rodeldev.inventory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.common.base.Optional;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import xyz.rodeldev.XMaterial;
import xyz.rodeldev.templates.Option;
import xyz.rodeldev.templates.Template;
import xyz.rodeldev.templates.TemplateRegistry;

public class YouiInventory {
    private Template template;
    private String name;
    private HashMap<String, Object> options = new HashMap<>();
    private Inventory inventory;

    public YouiInventory setTemplate(Template template){
        this.template = template;
        return this;
    }

    public Template getTemplate(){
        return template;
    }

    public boolean setOptionValue(String optionName, String valueString){
        Option<?> option = template.getOption(optionName);
        if(option==null) return false;

        Object value = null;
        try {
            Method method = option.getClass().getDeclaredMethod("valueOf", String.class);
            value = method.invoke(option.getClass(), valueString);
        } catch(Exception e){

        }
        // if(option.isEnum()){
        //     value = option.asEnum(valueString);
        // }else{
        //     value = valueString;
        // }

        options.put(optionName, value);

        // ItemStack oldContents[] = inventory.getContents();
        createInventory();
        // inventory.setContents(oldContents);

        return true;
    }

    public Optional<String> getOptionAsString(String optionName){
        if(options.containsKey(optionName)){
            return Optional.of(options.get(optionName).toString());
        }else{
            return Optional.fromNullable(template.getOptionString(optionName));
        }
    }

    public <T> Optional<T> getOptionValue(String optionName, Class<T> type){
        if(!options.containsKey(optionName)) return Optional.absent();
        return Optional.of((T) options.get(optionName)).or(template.getDefaultValue(optionName, type));
    }

    public YouiInventory setName(String name){
        this.name = name;
        return this;
    }

    public String getName(){
        return name;
    }

    public Inventory getInventory(){
        return inventory;
    }

    public YouiInventory setInventory(Inventory inventory){
        this.inventory = inventory;
        return this;
    }

    public void createInventory(){
        Optional<String> title = getOptionValue("title", String.class);
        InventoryType inventoryType = getOptionValue("inventoryType", InventoryType.class).or(InventoryType.CHEST);
        int inventorySize = getOptionValue("inventorySize", Integer.class).or(9*6);
        if(inventoryType==InventoryType.CHEST){
            if(title.isPresent()){
                setInventory(Bukkit.createInventory(null, inventorySize, title.get()));
            }else{
                setInventory(Bukkit.createInventory(null, inventorySize));
            }
        }else{
            if(title.isPresent()){
                setInventory(Bukkit.createInventory(null, inventoryType, title.get()));
            }else{
                setInventory(Bukkit.createInventory(null, inventoryType));
            }
        }
    }

    public JsonElement serialize(){
        JsonObject element = new JsonObject();
        element.addProperty("template", template.getFullName());

        JsonObject options = new JsonObject();
        for(Entry<String, Object> entry : this.options.entrySet()){
            options.addProperty(entry.getKey(), entry.getValue().toString());
        }
        element.add("options", options);

        YamlConfiguration conf = new YamlConfiguration();
        for(int i = 0; i < inventory.getContents().length; i++){
            if(inventory.getContents()[i]!=null){
                conf.set(String.valueOf(i), inventory.getContents()[i]);
                conf.set("xmaterial:"+String.valueOf(i), XMaterial.matchXMaterial(inventory.getContents()[i]).toString());
            }
        }
        element.addProperty("items", conf.saveToString());

        return element;
    }

    public void deserialize(JsonObject element){
        template = TemplateRegistry.get(element.get("template").getAsString());

        JsonObject options = element.get("options").getAsJsonObject();
        for(Option<?> option : template.getOptions()){
            if(options.has(option.getName())){
                JsonElement optionObject = options.get(option.getName());

                Object value;
                if(option.getDefaultValue() instanceof Number){
                    value = optionObject.getAsNumber();
                }else if(option.getDefaultValue() instanceof Boolean){
                    value = optionObject.getAsBoolean();
                }else{
                    value = optionObject.getAsString();
                    if(option.isEnum()){
                        value = option.asEnum((String)value);
                    }
                }

                this.options.put(option.getName(), value);
            }
        }

        createInventory();

        try{
            YamlConfiguration section = new YamlConfiguration();
            section.loadFromString(element.get("items").getAsString());
            for(String key : section.getKeys(false)){
                if(!key.contains("xmaterial")){
                    inventory.setItem(Integer.valueOf(key), section.getItemStack(key));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}