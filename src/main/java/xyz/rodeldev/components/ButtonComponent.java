package xyz.rodeldev.components;

public class ButtonComponent extends IComponent<ButtonComponent> {
    public ButtonComponent(String name) {
        super(name);
    }

    @Override
    public String getType() {
        return "button";
    }
}