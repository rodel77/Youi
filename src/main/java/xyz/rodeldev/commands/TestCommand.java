package xyz.rodeldev.commands;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import xyz.rodeldev.Helper;

public class TestCommand extends ISubCommand {
    public JsonObject serialize(ConfigurationSerializable serializable){
        JsonObject json = new JsonObject();
        json.addProperty("==", ConfigurationSerialization.getAlias(serializable.getClass()));
        for(Entry<String, Object> entry : serializable.serialize().entrySet()){
            Object value = entry.getValue();
            if(value instanceof ConfigurationSerializable){
                json.add(entry.getKey(), serialize((ConfigurationSerializable)value));
            }else{
                json.add(entry.getKey(), Helper.jsonPrimitive(value));
            }
        }
        return json;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        Player player = (Player) sender;
        JsonElement element = Helper.serializeField(player.getItemInHand());
        // System.out.println((element = Helper.serializeField(player.getItemInHand())).toString());

        YamlConfiguration conf = new YamlConfiguration();
        conf.set("item", player.getItemInHand());
        System.out.println(conf.saveToString());

        System.out.println(element.toString());

        Object a = Helper.deserialize(element);
        System.out.println(player.getItemInHand().equals((ItemStack)a) +" "+ player.getItemInHand().equals(conf.getItemStack("item")));
        player.getInventory().addItem((ItemStack)a);
        // System.out.println(a);
        // Stack<Helper.Tuple<Object, Object>> stack = new Stack<>();
        // Helper.jsonToMap(element, null, stack);
        // while(!stack.isEmpty()){
        //     Helper.Tuple<Object, Object> serializable = stack.pop();
        //     if(serializable==null){
        //         System.out.println("ROOT!");
        //         continue;
        //     }
        //     if(serializable.a instanceof List){
        //         System.out.println(((List<?>)serializable.a).get((Integer)serializable.b));
        //     }

        //     if(serializable.a instanceof Map){
        //         System.out.println(((Map<String, Object>) serializable.a).get(serializable.b));
        //     }
        // }
        // for(Entry<String, Object> a : player.getItemInHand().serialize().entrySet()){
        //     System.out.println(a.getKey()+" "+a.getValue());
        // }
        return true;
    }

    @Override
    public boolean onlyPlayer() {
        return true;
    }

    @Override
    public String getName() {
        return "test";
    }

    @Override
    public String getHelp() {
        return super.getHelp()+" &7(This should not be used!)";
    }
}