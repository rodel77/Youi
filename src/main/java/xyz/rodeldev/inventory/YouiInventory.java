package xyz.rodeldev.inventory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import xyz.rodeldev.Helper;
import xyz.rodeldev.XMaterial;
import xyz.rodeldev.templates.Option;
import xyz.rodeldev.templates.Placeholder;
import xyz.rodeldev.templates.Template;
import xyz.rodeldev.templates.TemplateRegistry;
import xyz.rodeldev.templates.ValidationResult;

/**
 * Represents the menu customized by the users.
 */
public class YouiInventory implements CustomMenu {
    private Template template;
    private String name;
    private HashMap<String, Object> options = new HashMap<>();
    private HashMap<String, List<Integer>> placeholders = new HashMap<>();
    private Inventory inventory;

    @Override
    public ItemStack[] getContents() {
        ItemStack[] contents = new ItemStack[this.inventory.getContents().length];
        for(int i = 0; i < contents.length; i++){
            contents[i] = this.inventory.getContents()[i].clone();
        }
        return contents;
    }

    @Override
    public Inventory getNewBukkitInventory() {
        return newInventory();
    }

    @Override
    public <T> Optional<T> getOptionValue(String optionName, Class<T> type){
        if(!options.containsKey(optionName)) return Optional.empty();
        if(options.containsKey(optionName)){
            return Optional.of((T)options.get(optionName));
        }

        return Optional.of(template.getOptionDefaultValue(optionName, type).get());
    }

    @Override
    public List<String> getPlaceholdersIn(int slot) {
        List<String> placeholders = new ArrayList<>();
        for(Entry<String, List<Integer>> placeholderList : this.placeholders.entrySet()) {
            for(int slt : placeholderList.getValue()){
                if(slt==slot){
                    placeholders.add(placeholderList.getKey());
                }
            }
        }
        return placeholders;
    }

    @Override
    public boolean hasPlaceholder(int slot, String placeholder) {
        for(Entry<String, List<Integer>> placeholderList : this.placeholders.entrySet()) {
            if(placeholderList.getKey().equalsIgnoreCase(placeholder)){
                for(int slt : placeholderList.getValue()){
                    if(slt==slot){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public YouiInventory setTemplate(Template template){
        this.template = template;
        return this;
    }

    public Template getTemplate(){
        return template;
    }

    public void fixPlaceholders(){
        Iterator<Entry<String, List<Integer>>> iterator = placeholders.entrySet().iterator();

        while(iterator.hasNext()){
            Entry<String, List<Integer>> placeholder = iterator.next();
            for(int i = placeholder.getValue().size()-1; i >= 0; i--){
                int slot = placeholder.getValue().get(i);
                if(slot<0 || slot>=inventory.getSize()){
                    placeholder.getValue().remove(i);
                }
            }
            if(placeholder.getValue().isEmpty()){
                iterator.remove();
            }
        }
    }

    public void setPlaceholder(String placeholder, int slot){
        List<Integer> slotList = placeholders.get(placeholder);
        if(slotList==null){
            slotList = new ArrayList<>();
            placeholders.put(placeholder, slotList);
        }

        slotList.add(slot);
    }

    public List<Integer> getPlaceholder(String placeholder){
        return placeholders.get(placeholder);
    }

    public HashMap<String, List<Integer>> getPlaceholders(){
        return placeholders;
    }

    public int countPlaceholders(String placeholder){
        if(placeholders.containsKey(placeholder)){
            return placeholders.get(placeholder).size();
        }
        return 0;
    }

    public ValidationResult setOptionValue(String optionName, String valueString){
        Option<?> option = template.getOption(optionName);
        if(option==null) return ValidationResult.error("Invalid option");

        Object value = null;
        try {
            Method method = option.getDefaultValue().getClass().getDeclaredMethod("valueOf", String.class);
            value = method.invoke(option.getClass(), valueString);
        } catch(Exception e){
        }

        if(value==null){
            value = valueString;
        }

        ValidationResult result = option.checkValidation(value);
        if(result.getError().isPresent()){
            return result;
        }

        options.put(optionName, value);

        // ItemStack oldContents[] = inventory.getContents();
        createInventory();
        fixPlaceholders();
        // inventory.setContents(oldContents);

        return ValidationResult.ok();
    }

    public Optional<String> getOptionAsString(String optionName){
        if(options.containsKey(optionName)){
            return Optional.of(options.get(optionName).toString());
        }else{
            return Optional.ofNullable(template.getOptionString(optionName));
        }
    }

    public YouiInventory setName(String name){
        this.name = name;
        return this;
    }

    @Override
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

    public Inventory newInventory(){
        Optional<String> title = getOptionValue("title", String.class);
        InventoryType inventoryType = getOptionValue("inventoryType", InventoryType.class).orElse(InventoryType.CHEST);
        int inventorySize = getOptionValue("inventorySize", Integer.class).orElse(9*6);
        return Helper.createInventory(inventoryType, inventorySize, title.isPresent() ? title.get() : null);
    }

    public void createInventory(){
        setInventory(newInventory());
    }

    public List<Placeholder> getPlaceholders(int slot){
        List<Placeholder> placeholdersList = new ArrayList<>();
        for(Entry<String, List<Integer>> placeholder : placeholders.entrySet()){
            for(int i : placeholder.getValue()){
                if(i==slot){
                    placeholdersList.add(template.getPlaceholder(placeholder.getKey()));
                }
            }
        }
        return placeholdersList;
    }

    public void removePlaceholders(int slot){
        for(Entry<String, List<Integer>> placeholder : placeholders.entrySet()){
            for(int i = placeholder.getValue().size()-1; i >= 0; i--){
                if(slot==placeholder.getValue().get(i)){
                    placeholder.getValue().remove(i);
                }
            }
        }
        fixPlaceholders();
    }

    public JsonElement serialize(){
        JsonObject element = new JsonObject();
        element.addProperty("template", template.getFullName());

        JsonObject options = new JsonObject();
        for(Entry<String, Object> entry : this.options.entrySet()){
            Object value = entry.getValue();
            if(value instanceof Boolean){
                options.addProperty(entry.getKey(), (Boolean)entry.getValue());
            }else if(value instanceof Number){
                options.addProperty(entry.getKey(), (Number)entry.getValue());
            }else{
                options.addProperty(entry.getKey(), entry.getValue().toString());
            }
        }
        element.add("options", options);

        JsonObject placeholders = new JsonObject();
        for(Entry<String, List<Integer>> placeholderInfo : this.placeholders.entrySet()){
            JsonArray slotArray = new JsonArray();
            for(int slotIndex : placeholderInfo.getValue()){
                slotArray.add(new JsonPrimitive(slotIndex));
            }
            placeholders.add(placeholderInfo.getKey(), slotArray);
        }
        element.add("placeholders", placeholders);

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
                    value = Helper.str2num(optionObject.getAsString(), option.getDefaultValue().getClass());
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

        JsonObject placeholders = element.get("placeholders").getAsJsonObject();
        for(Entry<String, JsonElement> placeholderInfo : placeholders.entrySet()){
            List<Integer> slotList = new ArrayList<>();
            for(JsonElement slot : placeholderInfo.getValue().getAsJsonArray()){
                slotList.add(slot.getAsInt());
            }
            this.placeholders.put(placeholderInfo.getKey(), slotList);
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