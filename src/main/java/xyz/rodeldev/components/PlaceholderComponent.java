package xyz.rodeldev.components;

public class PlaceholderComponent extends IComponent<PlaceholderComponent> {
    public PlaceholderComponent(String name){
        super(name);
    }

    @Override
    public String getType() {
        return "placeholder";
    }
}