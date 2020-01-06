package xyz.rodeldev.templates;

import java.util.HashMap;
import java.util.Optional;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableList;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;

import xyz.rodeldev.YouiPlugin;
import xyz.rodeldev.inventory.CustomMenu;

public class Template {
    private Plugin owner;
    private String name;

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

    public Optional<CustomMenu> getOverride(){
        return Optional.ofNullable(TemplateRegistry.getOverrideMap().get(this));
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
        return getDefaultValue("inventoryType", InventoryType.class).orElse(InventoryType.CHEST);
    }

    public int getInventorySize(){
        return getDefaultValue("inventorySize", Integer.class).orElse(9*6);
    }

    public <T> Optional<T> getDefaultValue(String optionName, Class<T> type){
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