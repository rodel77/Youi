package xyz.rodeldev.templates;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;

public class Option<T> {
    private String name;
    private T defaultValue;
    private boolean editable = true;
    private Function<T, ValidationResult> validate;

    public Option(String name, T defaultValue){
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public Option<T> setDefaultValue(T defaultValue){
        this.defaultValue = defaultValue;
        return this;
    }

    public Option<T> validation(Function<T, ValidationResult> validate){
        this.validate = validate;
        return this;
    }

    public ValidationResult checkValidation(Object value){
        if(isEnum() && !(value instanceof Enum<?>)){
            return ValidationResult.error("Invalid enum result, use: "+Arrays.asList(getDefaultValue().getClass().getEnumConstants()).stream().map(o -> ((Enum<?>) o).name()).collect(Collectors.joining(", ")));
        }

        if(validate!=null){
            return validate.apply((T)value);
        }

        return ValidationResult.ok();
    }

    public Option<T> setEditable(boolean editable){
        this.editable = editable;
        return this;
    }

    public boolean isEditable(){
        return this.editable;
    }

    public String getName(){
        return name;
    }

    public T getDefaultValue(){
        return defaultValue;
    }

    public boolean isEnum(){
        return defaultValue.getClass().isEnum();
    }

    public Object asEnum(String name){
        try {
            return defaultValue.getClass().getDeclaredMethod("valueOf", String.class).invoke(defaultValue.getClass(), name);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean validType(Object t){
        return t.getClass().isEnum() || t instanceof ItemStack || t instanceof String || t instanceof Number || t instanceof Boolean;
    }
}