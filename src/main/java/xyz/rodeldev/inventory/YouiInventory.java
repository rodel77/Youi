package xyz.rodeldev.inventory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import xyz.rodeldev.Helper;
import xyz.rodeldev.YouiPlugin;
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
    // private HashMap<String, List<Integer>> placeholders = new HashMap<>();
    private HashMap<Integer, List<String>> _placeholders = new HashMap<>();
    private HashMap<String, List<Integer>> inversePlaceholders = new HashMap<>();
    private Inventory inventory;

    public static final String PLACEHOLDER_LORE = ChatColor.translateAlternateColorCodes('&', "&o&o&e&e&rPlaceholders:");

    public void updateInversePlaceholders(){
        HashMap<String, List<Integer>> map = new HashMap<>();
        for(Entry<Integer, List<String>> entry : _placeholders.entrySet()){
            for(String placeholder : entry.getValue()){
                List<Integer> slots = map.getOrDefault(placeholder, new ArrayList<>());
                slots.add(entry.getKey());
            }
        }
        inversePlaceholders = map;
    }

    public void setPlaceholders(HashMap<Integer, List<String>> placeholders){
        this._placeholders = placeholders;
        updateInversePlaceholders();
    }

    @Override
    public ItemStack[] getContents() {
        ItemStack[] contents = new ItemStack[this.inventory.getContents().length];
        for(int i = 0; i < contents.length; i++){
            if(this.inventory.getContents()[i]!=null){
                contents[i] = this.inventory.getContents()[i].clone();
            }
        }
        return contents;
    }

    @Override
    public List<Integer> slotsWithPlaceholder(String placeholder) {
        if(template.getPlaceholder(placeholder)==null) {YouiPlugin.getInstance().getLogger().warning("Trying to find unexisting placeholder \""+placeholder+"\" in template \""+template.getFullName()+"\".");}

        return inversePlaceholders.getOrDefault(placeholder, new ArrayList<>());
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
        return this._placeholders.getOrDefault(slot, new ArrayList<>());
    }

    @Override
    public boolean hasPlaceholder(int slot, String placeholder) {
        if(template.getPlaceholder(placeholder)==null) YouiPlugin.getInstance().getLogger().warning("Trying to find unexisting placeholder \""+placeholder+"\" in template \""+template.getFullName()+"\".");

        return this._placeholders.containsKey(slot) && this._placeholders.get(slot).contains(placeholder);
    }

    public YouiInventory setTemplate(Template template){
        this.template = template;
        return this;
    }

    public Template getTemplate(){
        return template;
    }

    public void fixPlaceholders(){
        Iterator<Entry<Integer, List<String>>> iterator = this._placeholders.entrySet().iterator();

        while(iterator.hasNext()){
            Entry<Integer, List<String>> slot = iterator.next();
            if(slot.getKey()<0 || slot.getKey()>=inventory.getSize()){
                iterator.remove();
            }
        }
    }

    // public void setPlaceholder(String placeholder, int slot){
    //     // List<Integer> slotList = placeholders.get(placeholder);
    //     // if(slotList==null){
    //     //     slotList = new ArrayList<>();
    //     //     placeholders.put(placeholder, slotList);
    //     // }

    //     // slotList.add(slot);
    //     this._placeholders
    // }

    public List<Integer> getPlaceholder(String placeholder){
        return inversePlaceholders.get(placeholder);
    }

    @Deprecated
    public HashMap<String, List<Integer>> getPlaceholders(){
        return inversePlaceholders;
    }

    public HashMap<Integer, List<String>> _getPlaceholders(){
        return _placeholders;
    }

    public int countPlaceholders(String placeholder){
        if(inversePlaceholders.containsKey(placeholder)){
            return inversePlaceholders.get(placeholder).size();
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

        return setOptionValue(optionName, value);
    }

    public ValidationResult setOptionValue(String optionName, Object value){
        Option<?> option = template.getOption(optionName);
        if(option==null) return ValidationResult.error("Invalid option");

        ValidationResult result = option.checkValidation(value);
        if(result.getError().isPresent()){
            return result;
        }

        options.put(optionName, value);

        ItemStack oldContents[] = inventory.getContents();
        createInventory();
        fixPlaceholders();
        ItemStack newContents[] = new ItemStack[inventory.getSize()];
        System.arraycopy(oldContents, 0, newContents, 0, newContents.length);
        inventory.setContents(newContents);

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
        return this._placeholders.containsKey(slot) ? this._placeholders.get(slot).stream().map((a) -> {return template.getPlaceholder(a);}).collect(Collectors.toList()) : new ArrayList<>();
    }

    @Deprecated
    public void removePlaceholders(int slot){
        this._placeholders.remove(slot);
    }

    private void updatePlaceholderFromItems(){
        this._placeholders.clear();
        for(int i = 0; i < inventory.getContents().length; i++){
            ItemStack item = inventory.getContents()[i];
            List<String> placeholders;
            if((placeholders = Helper.getPlaceholders(item))!=null){
                this._placeholders.put(i, placeholders);
            }
            // Helper.stripPlaceholders(item);
        }
    }

    public JsonElement serialize(){
        JsonObject element = new JsonObject();
        element.addProperty("template", template.getFullName());

        JsonObject options = new JsonObject();
        for(Entry<String, Object> entry : this.options.entrySet()){
            Object value = entry.getValue();
            if(value instanceof ItemStack){
                options.add(entry.getKey(), Helper.serializeItemStack((ItemStack) value));
            }else if(value instanceof Boolean){
                options.addProperty(entry.getKey(), (Boolean)entry.getValue());
            }else if(value instanceof Number){
                options.addProperty(entry.getKey(), (Number)entry.getValue());
            }else{
                options.addProperty(entry.getKey(), entry.getValue().toString());
            }
        }
        element.add("options", options);

        System.out.println(_placeholders);
        updatePlaceholderFromItems();
        System.out.println(_placeholders);
        updateInversePlaceholders();

        JsonObject placeholders = new JsonObject();
        for(Entry<Integer, List<String>> slot : this._placeholders.entrySet()){
            for(String placeholder : slot.getValue()){
                if(!placeholders.has(placeholder)){
                    placeholders.add(placeholder, new JsonArray());
                }

                placeholders.get(placeholder).getAsJsonArray().add(new JsonPrimitive(slot.getKey()));
            }
        }
        element.add("placeholders", placeholders);

        JsonObject items = new JsonObject();
        for(int i = 0; i < inventory.getContents().length; i++){
            if(inventory.getContents()[i]!=null){
                ItemStack item = inventory.getContents()[i].clone();
                Helper.stripPlaceholders(item);
                items.add(String.valueOf(i), Helper.serializeItemStack(item));
            }
        }
        element.add("items", items);

        return element;
    }

    public void deserialize(JsonObject element){
        try{
            template = TemplateRegistry.get(element.get("template").getAsString());

            JsonObject options = element.get("options").getAsJsonObject();
            for(Option<?> option : template.getOptions()){
                if(options.has(option.getName())){
                    JsonElement optionObject = options.get(option.getName());

                    Object value;
                    if(option.getDefaultValue() instanceof ItemStack){
                        value = Helper.deserializeItemStack(optionObject.getAsJsonObject());
                    }else if(option.getDefaultValue() instanceof Number){
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

            this._placeholders.clear();
            JsonObject placeholders = element.get("placeholders").getAsJsonObject();
            for(Entry<String, JsonElement> placeholderInfo : placeholders.entrySet()){
                for(JsonElement jsonSlot : placeholderInfo.getValue().getAsJsonArray()){
                    int slot = jsonSlot.getAsInt();
                    if(!this._placeholders.containsKey(slot)){
                        this._placeholders.put(slot, new ArrayList<>());
                    }

                    this._placeholders.get(slot).add(placeholderInfo.getKey());
                }
            }
            updateInversePlaceholders();

            createInventory();

            for(Entry<String, JsonElement> entry : element.get("items").getAsJsonObject().entrySet()){
                inventory.setItem(Integer.valueOf(entry.getKey()), Helper.deserializeItemStack(entry.getValue().getAsJsonObject()));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}