package xyz.rodeldev.session;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.rodeldev.Helper;
import xyz.rodeldev.YouiPlugin;
import xyz.rodeldev.inventory.DefaultInventory;
import xyz.rodeldev.inventory.YouiInventory;
import xyz.rodeldev.templates.Placeholder;
import xyz.rodeldev.templates.Template;

public class Session {
    private Player owner;
    private YouiInventory youiInventory;
    private int slotFocus = -1;

    private Inventory placeholderInventory;

    public Session(Player owner){
        this.owner = owner;
        youiInventory = new YouiInventory();
    }

    public Player getOwner(){
        return owner;
    }

    public boolean slotHasPlaceholder(String placeholderName){
        List<Integer> slotList = getYouiInventory().getPlaceholder(placeholderName);
        if(slotList!=null){
            for(int slot : slotList){
                if(slot==getSlotFocus()){
                    return true;
                }
            }
        }
        return false;
    }

    public void displayPlaceholderList(){
        for(Placeholder placeholder : getTemplate().getPlaceholders()){
            displayPlaceholderInfo(placeholder);
        }
    }

    public void displayPlaceholderInfo(Placeholder placeholder){
        if(slotHasPlaceholder(placeholder.getName())){
            Helper.sendMessage(owner, "&c%s (Unavailable) &8%s", placeholder.getName(), placeholder.getDescription());
            return;
        }

        int placeholderCount = youiInventory.countPlaceholders(placeholder.getName());
        if(placeholder.isConstrained()){
            if(placeholder.getMin()==0){
                Helper.sendMessage(owner, "&7%s &6(%d/%d) &8%s", placeholder.getName(), placeholderCount, placeholder.getMax(), placeholder.getDescription());
            }else{
                if(placeholder.getMax()==0){
                    Helper.sendMessage(owner, "&7%s &6(Should be at least %d) &8%s", placeholder.getName(), placeholder.getMin(), placeholder.getDescription());
                }else if(placeholderCount>=placeholder.getMax()){
                    Helper.sendMessage(owner, "&c%s (Unavailable) &8%s", placeholder.getName(), placeholder.getDescription());
                }else{
                    Helper.sendMessage(owner, "&7%s &6(From %d to %d, now %d) &8%s", placeholder.getName(), placeholder.getMin(), placeholder.getMax(), placeholderCount, placeholder.getDescription());
                }
            }
        }else{
            Helper.sendMessage(owner, "&7%s &8%s", placeholder.getName(), placeholder.getDescription());
        }
    }

    public int getSlotFocus(){
        return slotFocus;
    }

    public void focusSlot(int slot){
        slotFocus = slot;
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
    public void refreshPlaceholders(){
        if(placeholderInventory==null){
            placeholderInventory = youiInventory.newInventory();
        }

        placeholderInventory.clear();

        for(Entry<String, List<Integer>> placeholders : youiInventory.getPlaceholders().entrySet()){
            for(int slot : placeholders.getValue()){
                ItemStack item = placeholderInventory.getItem(slot);
                if(item==null || item.getType()==Material.AIR){
                    item = new ItemStack(Material.EMERALD);
                }else{
                    item.setAmount(Math.min(item.getMaxStackSize(), item.getAmount()+1));
                }
                
                ItemMeta meta = item.getItemMeta();
                List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                lore.add(placeholders.getKey());
                meta.setLore(lore);
                item.setItemMeta(meta);
                placeholderInventory.setItem(slot, item);
            }
        }
    }

    public Inventory getPlaceholderInventory(){
        return placeholderInventory;
    }

    public void openPlaceholdersMenu(Player player){
        refreshPlaceholders();
        player.openInventory(placeholderInventory);
    }

    public void createInventory(){
        youiInventory.createInventory();
    }

    public void setDefaults(){
        DefaultInventory defaults = getTemplate().getDefault();
        if(defaults!=null){
            youiInventory.getInventory().setContents(defaults.getContents());
            youiInventory.setPlaceholders((HashMap<Integer, List<String>>)defaults.getInversePlaceholders().clone());
        }
    }

    public void resume(Player player){
        markPlaceholders();
        player.openInventory(getInventory());
    }

    public void load(File file){
        try(FileReader reader = new FileReader(file)){
            setName(file.getName().replace(".json", ""));
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(reader);
            JsonObject jsonObject = element.getAsJsonObject();
            youiInventory.deserialize(jsonObject);
            markPlaceholders();
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
            markPlaceholders();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void markPlaceholders(){
        for(Entry<Integer, List<String>> slots : youiInventory._getPlaceholders().entrySet()){
            Helper.setPlaceholders(youiInventory.getInventory().getItem(slots.getKey()), slots.getValue());
        }
    }

    public void save(){
        YouiPlugin.getInstance().getFileSystem().saveMenu(youiInventory.getName(), youiInventory.serialize().toString());
    }
}