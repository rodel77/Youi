package xyz.rodeldev.templates;

import java.util.HashMap;
import java.util.Optional;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import xyz.rodeldev.inventory.CustomMenu;
import xyz.rodeldev.inventory.DefaultInventory;

public class Template {
    private Plugin owner;
    private String name, description = "";

    private DefaultInventory defaultInventory;

    private HashMap<String, Placeholder> placeholders = new HashMap<>();
    private HashMap<String, Option<?>> options = new HashMap<>();

    public Template(Plugin owner, String name){
        this.owner = owner;
        this.name  = name;
        registerOption("title", "My custom UI");
        registerOption("inventoryType", InventoryType.CHEST);
        registerOption(new Option<Integer>("inventorySize", 9*6).validation(value->{
            if(value%9!=0){
                return ValidationResult.error("The value should be a multiple of 9");
            }else if(value<0 || value>9*6){
                return ValidationResult.error("The value should be in a range of 0-"+9*6);
            }

            return ValidationResult.ok();
        }));
    }

    public DefaultInventory getDefault(){
        return defaultInventory;
    }

    public String getDescription(){
        return description;
    }

    public Template setDescription(String description){
        this.description = description;
        return this;
    }

    public Template defaultAddPlaceholder(String name, int slot){
        if(defaultInventory==null) defaultInventory = new DefaultInventory(this);
        defaultInventory.addDefaultPlaceholder(name, slot);
        return this;
    }

    public Template defaultSetItem(ItemStack item, int slot){
        if(defaultInventory==null) defaultInventory = new DefaultInventory(this);
        defaultInventory.getInventory().setItem(slot, item);
        return this;
    }

    public Template defaultFillInventory(ItemStack item){
        if(defaultInventory==null) defaultInventory = new DefaultInventory(this);
        for(int i = 0; i < defaultInventory.getInventory().getSize(); i++){
            defaultInventory.getInventory().setItem(i, item);
        }
        return this;
    }

    /**
     * Search if there is any player-made UI for this template, otherwise return the default menu (created with the "default..." methods like {@link Template#defaultAddPlaceholder(String, int)}) or null.
     * 
     * @return the player made menu or the default menu or null
     */
    public CustomMenu getOverride(boolean useDefault){
        CustomMenu customMenu = TemplateRegistry.getOverrideMap().get(this);
        if(customMenu!=null){
            return customMenu;
        }
        return useDefault ? defaultInventory : null;
    }

    public ImmutableList<Placeholder> getPlaceholders(){
        return ImmutableList.copyOf(placeholders.values());
    }

    public ImmutableList<Option<?>> getOptions(){
        return ImmutableList.copyOf(options.values());
    }

    public Placeholder getPlaceholder(String name){
        return placeholders.get(name);
    }

    public Option<?> getOption(String name){
        return options.get(name);
    }

    public <T> Option<T> getOption(String name, Class<T> type) {
        return (Option<T>)options.get(name);
    }

    public Template setDefaultTitle(String title, boolean editable){
        getOption("title", String.class).setDefaultValue(title).setEditable(editable);
        return this;
    }

    public Template setInventoryType(InventoryType inventoryType, boolean editable){
        getOption("inventoryType", InventoryType.class).setDefaultValue(inventoryType).setEditable(editable);
        return this;
    }

    public Template setInventorySize(int inventorySize, boolean editable){
        getOption("inventorySize", Integer.class).setDefaultValue(inventorySize).setEditable(editable);
        return this;
    }

    public InventoryType getInventoryType(){
        return getOptionDefaultValue("inventoryType", InventoryType.class).orElse(InventoryType.CHEST);
    }

    public int getInventorySize(){
        return getOptionDefaultValue("inventorySize", Integer.class).orElse(9*6);
    }

    public <T> Optional<T> getOptionDefaultValue(String optionName, Class<T> type){
        if(!options.containsKey(optionName)) return Optional.empty();
        return Optional.of((T) options.get(optionName).getDefaultValue());
    }

    public String getOptionString(String optionName){
        if(options.containsKey(optionName)){
            return options.get(optionName).getDefaultValue().toString();
        }

        return null;
    }

    public <T> Template registerOption(String name, T defaultValue) {
        registerOption(new Option<T>(name, defaultValue));
        return this;
    }

    public Template registerOption(Option<?> option){
        try{
            if(!Option.validType(option.getDefaultValue())){
                throw new UnsupportedOperationException("Can't register option \""+option.getName()+"\" in template \""+this.getFullName()+"\"");
            }

            for(Entry<String, Option<?>> op : options.entrySet()){
                if(op.getKey().equalsIgnoreCase(option.getName())){
                    throw new Exception("Can't register option \""+option.getName()+"\" in template \""+this.getFullName()+"\" because there is already one called like that (not case sensitive)");
                }
            }

            options.put(option.getName(), option);
        }catch(Exception e){
            e.printStackTrace();
        }
        return this;
    }

    public Template registerPlaceholders(Object... placeholders){
        for(Object placeholder : placeholders){
            try{
                if(placeholder instanceof String){
                    registerPlaceholders((String)placeholder);
                }else if(placeholder instanceof Placeholder){
                    registerPlaceholders((Placeholder)placeholder);
                }else{
                    throw new UnsupportedOperationException("Error adding placeholder on template \""+this.getFullName()+"\" only String and Placeholder type are available!");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return this;
    }

    public Template registerPlaceholders(String... names){
        for(String name : names){
            this.registerPlaceholder(new Placeholder(name));
        }
        return this;
    }

    public Template registerPlaceholders(Placeholder... placeholders){
        for(Placeholder placeholder : placeholders){
            this.registerPlaceholder(placeholder);
        }
        return this;
    }

    public Template registerPlaceholder(Placeholder placeholder){
        try {
            for(Entry<String, Placeholder> ph : placeholders.entrySet()){
                if(ph.getKey().equalsIgnoreCase(placeholder.getName())){
                    throw new Exception("Can't register placeholder \""+placeholder.getName()+"\" in template \""+this.getFullName()+"\" because there is already one called like that (not case sensitive)");
                }
            }

            placeholders.put(placeholder.getName(), placeholder);
        }catch(Exception e){
            e.printStackTrace();
        }
        return this;
    }

    public Plugin getOwner(){
        return owner;
    }

    public String getName(){
        return name;
    }

    public String getFullName(){
        return owner.getName().toLowerCase().replace(" ", "_")+":"+name;
    }
}