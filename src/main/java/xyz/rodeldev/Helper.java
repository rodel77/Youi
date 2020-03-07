package xyz.rodeldev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.Stack;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import xyz.rodeldev.inventory.PlaceholderInstance;
import xyz.rodeldev.inventory.YouiInventory;

public class Helper {

    public static Gson gson = new Gson();

	public static void stripPlaceholders(ItemStack item){
		ItemMeta meta;
		if(item==null || item.getType()==Material.AIR || !item.hasItemMeta() || !(meta = item.getItemMeta()).hasLore()){
			return;
		}

		List<String> lore = new ArrayList<>();
		for(int i = 0; i < lore.size(); i++){
			String line = lore.get(i);
			if(line.equals(YouiInventory.PLACEHOLDER_LORE)){
				if(i+1<lore.size()){
					lore.remove(i+1);
				}
				lore.remove(i);
			}
		}

		meta.setLore(lore);
		item.setItemMeta(meta);
	}

	public static List<PlaceholderInstance> getPlaceholders(ItemStack item){
		ItemMeta meta;
		if(item==null || item.getType()==Material.AIR || !item.hasItemMeta() || !(meta = item.getItemMeta()).hasLore()){
			return null;
		}

		for(int i = 0; i < meta.getLore().size(); i++){
			String line = meta.getLore().get(i);
			if(line.equals(YouiInventory.PLACEHOLDER_LORE) && i+1<meta.getLore().size()){
				return Arrays.stream(meta.getLore().get(i+1).replace(ChatColor.GOLD.toString(), "").split(YouiInventory.PLACEHOLDER_SEPARATOR)).map(PlaceholderInstance::fromPlain).collect(Collectors.toList());
			}
		}

		return null;
	}

	public static void setPlaceholders(ItemStack item, List<PlaceholderInstance> placeholders){
        if(item==null || item.getType()==Material.AIR){
            return;
        }

        if(placeholders.isEmpty()){
            Helper.stripPlaceholders(item);
            return;
        }

		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
		int index = lore.size();
		for(int i = 0; i < lore.size(); i++){
			String line = lore.get(i);
			if(line.equals(YouiInventory.PLACEHOLDER_LORE)){
				index = i;
				break;
			}
		}

		if(index>=lore.size()){
			lore.add(YouiInventory.PLACEHOLDER_LORE);
		}


        String placeholdersString = ChatColor.GOLD + placeholders.stream().map(PlaceholderInstance::toPlain).collect(Collectors.joining(YouiInventory.PLACEHOLDER_SEPARATOR+ChatColor.GOLD));

		if(index+1>=lore.size()){
			lore.add(placeholdersString);
		}else{
			lore.set(index+1, placeholdersString);
		}

		meta.setLore(lore);
		item.setItemMeta(meta);
	}


    public static JsonElement serializeItemStack(ItemStack item){
        return serializeField(item);
    }

    public static ItemStack deserializeItemStack(JsonObject object){
        return (ItemStack)deserialize(object);
    }

    public static JsonElement serializeField(Object object){
        if(object instanceof ConfigurationSerializable){
            ConfigurationSerializable serializable = (ConfigurationSerializable) object;
            JsonObject json = new JsonObject();
            json.addProperty("==", ConfigurationSerialization.getAlias(serializable.getClass()));
            for(Entry<String, Object> entry : serializable.serialize().entrySet()){
                json.add(entry.getKey(), serializeField(entry.getValue()));
            }
            return json;
        }else if(object instanceof Collection<?>){
            Collection<?> collection = (Collection<?>) object;
            JsonArray array = new JsonArray();
            collection.stream().map(Helper::serializeField).forEach(array::add);
            return array;
        }else if(object instanceof Map<?, ?>){
            JsonObject json = new JsonObject();
            for(Entry<?, ?> entry : ((Map<?, ?>) object).entrySet()) {
                json.add(entry.getKey().toString(), serializeField(entry.getValue()));
            }
            return json;
        }else{
            return jsonPrimitive(object);
        }
    }

    public static class Tuple<A, B> {
        public final A a;
        public final B b;

        public Tuple(A a, B b) {
            this.a = a;
            this.b = b;
        }
    }

    public static Object jsonToMap(JsonElement json, Tuple<Object, Object> parent, Stack<Tuple<Object, Object>> serializationStack){
        if(json.isJsonObject()){
            JsonObject object = (JsonObject) json;
            if(serializationStack!=null && object.has("==")){
                serializationStack.push(parent);
            }
            Map<String, Object> map = new LinkedHashMap<>();
            for(Entry<String, JsonElement> entry : object.entrySet()){
                map.put(entry.getKey(), jsonToMap(entry.getValue(), new Tuple<>(map, entry.getKey()), serializationStack));
            }

            return map;
        }else if(json.isJsonArray()){
            Collection<Object> list = new ArrayList<>();
            for(JsonElement element : (JsonArray) json){
                list.add(jsonToMap(element, new Tuple<>(list, list.size()), serializationStack));
            }
            return list;
        }else if(json.isJsonPrimitive()){
            JsonPrimitive primitive = json.getAsJsonPrimitive();
            if(primitive.isBoolean()) return primitive.getAsBoolean();
            if(primitive.isNumber()){
                if(primitive.getAsString().contains(".")) {
                    return primitive.getAsDouble();
                }else{
                    return primitive.getAsInt();
                }
            }
            if(primitive.isString()) return primitive.getAsString();
        }
        return null;
    }
    
    public static Object deserialize(JsonElement json){
        Stack<Tuple<Object, Object>> stack = new Stack<>();
        Map<String, Object> map = (Map<String, Object>)jsonToMap(json, null, stack);
        while(!stack.isEmpty()){
            Tuple<Object, Object> element = stack.pop();
            if(element==null){
                return ConfigurationSerialization.deserializeObject(map);
            }else if(element.a instanceof List){
                List<Object> list = (List<Object>) element.a;
                int index = (Integer) element.b;
                list.set(index, ConfigurationSerialization.deserializeObject((Map<String, Object>)list.get(index)));
            }else if(element.a instanceof Map){
                Map<String, Object> innerMap = (Map<String, Object>) element.a;
                String key = (String) element.b;
                innerMap.put(key, ConfigurationSerialization.deserializeObject((Map<String, Object>)innerMap.get(key)));
            }
        }
        return null;
    }

    public static String item2Readable(Object o){
        if(o instanceof ItemStack){
            ItemStack item = (ItemStack) o;
            StringBuffer name = new StringBuffer();
            for(String word : item.getType().toString().split("_")){
                name.append(word.charAt(0)).append(word.substring(1).toLowerCase());
            }
    
            if(item.getAmount()>0) name.append(" x "+item.getAmount());
    
            return name.toString();
        }

        return o.toString();
    }

    public static Inventory createInventory(InventoryType type, int size, String title){
        if(type==InventoryType.CHEST){
            if(title!=null){
                return Bukkit.createInventory(null, size, ChatColor.translateAlternateColorCodes('&', title));
            }else{
                return Bukkit.createInventory(null, size);
            }
        }else{
            if(title!=null){
                return Bukkit.createInventory(null, type, ChatColor.translateAlternateColorCodes('&',title));
            }else{
                return Bukkit.createInventory(null, type);
            }
        }
    }

    public static JsonPrimitive jsonPrimitive(Object object){
        JsonPrimitive primitive = null;
        if(object instanceof Boolean)
            primitive = new JsonPrimitive((Boolean)object);
        else if(object instanceof Number)
            primitive = new JsonPrimitive((Number)object);
        else if(object instanceof String)
            primitive = new JsonPrimitive((String)object);
        else if(object instanceof Character)
            primitive = new JsonPrimitive((Character)object);
            
        return primitive;
    }

    public static void sendMessage(CommandSender sender, String message){
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[Youi] "+message));
    }

    public static void sendMessage(CommandSender sender, String message, Object... args){
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[Youi] "+String.format(message, args)));
    }

    public static Number str2num(String str, Class<?> type){
        try {
            return (Number)type.getDeclaredMethod("valueOf", String.class).invoke(type, str);
        } catch(Exception e){
        }
        return null;
    }
}