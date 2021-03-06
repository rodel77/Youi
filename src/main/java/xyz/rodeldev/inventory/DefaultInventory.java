package xyz.rodeldev.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import xyz.rodeldev.Helper;
import xyz.rodeldev.YouiPlugin;
import xyz.rodeldev.templates.Template;

/**
 * Represents a menu that can be used by uis but all the data is taken from the template or what has been set in the "default..." methods like {@link Template#defaultAddPlaceholder(String, int)}.
 * 
 * This class only instantiate when those methods are called.
 */
public class DefaultInventory implements CustomMenu {
    private Template template;
    private HashMap<String, List<Integer>> placeholders = new HashMap<>();
    private Inventory inventory;

    public DefaultInventory(Template template){
        this.template = template;
        inventory = Helper.createInventory(template.getInventoryType(), template.getInventorySize(), template.getOptionString("title"));
    }

    public HashMap<Integer, List<PlaceholderInstance>> getInversePlaceholders(){
        HashMap<Integer, List<PlaceholderInstance>> map = new HashMap<>();
        for(Entry<String, List<Integer>> entry : placeholders.entrySet()){
            for(int slot : entry.getValue()){
                if(!map.containsKey(slot)){
                    map.put(slot, new ArrayList<>());
                }

                map.get(slot).add(PlaceholderInstance.fromPlain(entry.getKey()));
            }
        }
        return map;
    }

    public void addDefaultPlaceholder(String name, int slot) throws UnsupportedOperationException{
        // if(template.getPlaceholder(name)==null) throw new UnsupportedOperationException("Placeholder \""+name+"\" doesn't exists!");

        List<Integer> slotList = placeholders.get(name);
        if(slotList==null){
            slotList = new ArrayList<>();
            placeholders.put(name, slotList);
        }
        slotList.add(slot);
    }

    public Inventory getInventory(){
        return inventory;
    }

    @Override
    public String getName() {
        return "default";
    }

    @Override
    public <T> Optional<T> getOptionValue(String optionName, Class<T> type) {
        return template.getOptionDefaultValue(optionName, type);
    }

    @Override
    public Inventory getNewBukkitInventory() {
        return Helper.createInventory(template.getInventoryType(), template.getInventorySize(), template.getOptionString("title"));
    }

    @Override
    public List<Integer> slotsWithPlaceholder(String placeholder) {
        if(template.getPlaceholder(placeholder)==null) {YouiPlugin.getInstance().getLogger().warning("Trying to find unexisting placeholder \""+placeholder+"\" in template \""+template.getFullName()+"\".");}
        return this.placeholders.getOrDefault(placeholder, new ArrayList<>());
    }
    
    @Override
    public List<PlaceholderInstance> getPlaceholdersIn(int slot) {
        List<PlaceholderInstance> placeholders = new ArrayList<>();
        for(Entry<String, List<Integer>> placeholderList : this.placeholders.entrySet()) {
            for(int slt : placeholderList.getValue()){
                if(slt==slot){
                    placeholders.add(PlaceholderInstance.fromPlain(placeholderList.getKey()));
                }
            }
        }
        return placeholders;
    }
    
    @Override
    public Template getTemplate() {
        return template;
    }
    
    @Override
    public boolean hasPlaceholder(int slot, String placeholder) {
        if(template.getPlaceholder(placeholder)==null) {YouiPlugin.getInstance().getLogger().warning("Trying to find unexisting placeholder \""+placeholder+"\" in template \""+template.getFullName()+"\".");}
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

    @Override
    public ItemStack[] getContents() {
        ItemStack[] contents = new ItemStack[inventory.getContents().length];
        for(int i = 0; i < contents.length; i++){
            if(inventory.getContents()[i]!=null){
                contents[i] = inventory.getContents()[i].clone();
            }
        }
        return contents;
    }
}