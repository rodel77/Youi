package xyz.rodeldev.inventory;

import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.Nullable;

import xyz.rodeldev.Helper;

public class PlaceholderInstance {
    private String placeholderName;
    private JsonElement placeholderData;

    public static PlaceholderInstance fromPlain(String input) throws JsonSyntaxException {
        int index = input.lastIndexOf("{");
        int arrayIndex = input.indexOf("[");
        if(index==-1 || (arrayIndex!=-1 && arrayIndex<index)){
            index = arrayIndex;
        }

        if(index<0) return new PlaceholderInstance(input, null);

        return new PlaceholderInstance(input.substring(0, index), Helper.gson.fromJson(input.substring(index), JsonElement.class));
    }

    public PlaceholderInstance(String placeholderName, @Nullable JsonElement placeholderData){
        this.placeholderName = placeholderName;
        this.placeholderData = placeholderData;
    }

    public String toPlain(){
        return this.placeholderData==null ? this.placeholderName : this.placeholderName+this.placeholderData.toString();
    }

    public String getPlaceholderName(){
        return placeholderName;
    }

    public Optional<JsonElement> getPlaceholderData(){
        return Optional.ofNullable(this.placeholderData);
    }

    @Override
    public String toString() {
        return this.toPlain();
    }
}