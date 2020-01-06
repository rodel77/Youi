package xyz.rodeldev.inventory;

import java.util.List;
import java.util.Optional;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import xyz.rodeldev.templates.Template;

public interface CustomMenu {
    /**
     * @return the template from this menu was created
     */
    public Template getTemplate();

    /**
     * @return the name of the menu
     */
    public String getName();

    /**
     * @return a new clean Bukkit inventory using the described properties such as title, size and type
     */
    public Inventory getNewBukkitInventory();

    /**
     * @return a new Bukkit inventory with all the items prepared to be used
     */
    public Inventory getBukkitInventory();

    /**
     * Fills an inventory with the items described in the custom menu
     * 
     * @param inventory inventory to be filled
     */
    public void setItems(Inventory inventory);

    /**
     * @return the contents of the menu as they should be in the {@link Inventory#getContents()}
     */
    public ItemStack[] getContents();

    /**
     * Returns a list of the placeholders in the slot
     * 
     * @param slot the inventory slot where to check for placeholders
     * @return an {@link java.util.ArrayList} containing all the placeholders
     */
    public List<String> getPlaceholdersIn(int slot);
 
    /**
     * Tells whenever a slot contains a placeholder
     * 
     * @param slot the slot to search
     * @param placeholder the placeholder to find (not case sensitive)
     * @return true if the slot contains that placeholder
     */
    public boolean hasPlaceholder(int slot, String placeholder);

    /**
     * Using the {@link Template#registerOption(xyz.rodeldev.templates.Option)} you can register customizable options to your GUI.
     * 
     * They have a name and a value, here you can fetch the customized value of these.
     * 
     * @param <T> type of the option
     * @param optionName name of the option (not case sensitive)
     * @param type the type in which the option should be returned
     * @return the option casted to the required value wrapped on an optional
     */
    public <T> Optional<T> getOptionValue(String optionName, Class<T> type);
}