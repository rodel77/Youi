package xyz.rodeldev.templates;

import java.util.HashMap;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;

import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.Plugin;

public class Template {
    private Plugin owner;
    private String name;

    // private InventoryType inventoryType = InventoryType.CHEST;
    // private boolean inventoryTypeEditable = false;

    // private int inventorySize = 9*6;
    // private boolean inventorySizeEditable = true;

    private HashMap<String, Placeholder> placeholders = new HashMap<>();
    private HashMap<String, Option<?>> options = new HashMap<>();

    public Template(Plugin owner, String name){
        this.owner = owner;
        this.name  = name;
        registerOption("title", "My custom UI");
        registerOption("inventoryType", InventoryType.CHEST);
        registerOption("inventorySize", 9*6);
    }

    public ImmutableList<Placeholder> getPlaceholders(){
        return ImmutableList.copyOf(placeholders.values());
    }

    public ImmutableList<Option<?>> getOptions(){
        return ImmutableList.copyOf(options.values());
    }

    public Option<?> getOption(String name){
        return options.get(name);
    }

    public Template setDefaultTitle(String title){
        registerOption("title", title);
        return this;
    }

    public Template setInventoryType(InventoryType inventoryType, boolean editable){
        registerOption(new Option<InventoryType>(name, inventoryType).setEditable(editable));
        return this;
    }

    public InventoryType getInventoryType(){
        return getDefaultValue("inventoryType", InventoryType.class).or(InventoryType.CHEST);
    }

    public Template setInventorySize(int inventorySize, boolean editable){
        registerOption(new Option<Integer>(name, inventorySize).setEditable(editable));
        return this;
    }

    public int getInventorySize(){
        return getDefaultValue("inventorySize", Integer.class).or(9*6);
    }

    public <T> Optional<T> getDefaultValue(String optionName, Class<T> type){
        if(!options.containsKey(optionName)) return Optional.absent();
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

            options.put(option.getName(), option);
        }catch(Exception e){
            e.printStackTrace();
        }
        return this;
    }

    public Template addPlaceholders(Object... placeholders){
        for(Object placeholder : placeholders){
            try{
                if(placeholder instanceof String){
                    addPlaceholders((String)placeholder);
                }else if(placeholder instanceof Placeholder){
                    addPlaceholders((Placeholder)placeholder);
                }else{
                    throw new UnsupportedOperationException("Error adding placeholder on template \""+this.getFullName()+"\" only String and Placeholder type are available!");
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return this;
    }

    public Template addPlaceholders(String... names){
        for(String name : names){
            this.addPlaceholder(new Placeholder(name));
        }
        return this;
    }

    public Template addPlaceholders(Placeholder... placeholders){
        for(Placeholder placeholder : placeholders){
            this.addPlaceholder(placeholder);
        }
        return this;
    }

    public Template addPlaceholder(Placeholder placeholder){
        try {
            if(placeholders.containsKey(placeholder.getName())){
                throw new Exception("Trying to add a placeholder \""+placeholder.getName()+"\" in the template \""+this.getFullName()+"\"");
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