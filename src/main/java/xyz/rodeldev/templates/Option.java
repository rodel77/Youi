package xyz.rodeldev.templates;

import org.bukkit.Material;

public class Option<T> {
    private String name;
    private T defaultValue;
    private boolean editable = true;

    public Option(String name, T defaultValue){
        this.name = name;
        this.defaultValue = defaultValue;

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
        return t.getClass().isEnum() || t instanceof String || t instanceof Number || t instanceof Boolean;
    }
}