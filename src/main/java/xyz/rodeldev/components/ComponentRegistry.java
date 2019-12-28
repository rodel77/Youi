package xyz.rodeldev.components;

import java.util.HashMap;

public class ComponentRegistry {
    private HashMap<String, IComponent<? extends IComponent<?>>> registry = new HashMap<>();

    public ComponentRegistry(){
        registryComponent(new ButtonComponent("name"));
    }

    public void registryComponent(IComponent<? extends IComponent<?>> component){
        registry.put(component.getType(), component);
    }
}