package xyz.rodeldev.templates;

import java.util.HashMap;

import com.google.common.collect.ImmutableList;

import xyz.rodeldev.YouiPlugin;

public class TemplateRegistry {
    private static HashMap<String, Template> registry = new HashMap<>();

    public TemplateRegistry(){
        register(new Template(YouiPlugin.getInstance(), "test"));
    }

    public static ImmutableList<Template> getRegistry(){
        return ImmutableList.copyOf(registry.values());
    }

    public static Template get(String fullname){
        return registry.get(fullname);
    }

    public static void register(Template template){
        try{
            if(registry.containsKey(template.getFullName())){
                throw new Exception("Template \""+template.getFullName()+"\" is already registered");
            }

            registry.put(template.getFullName(), template);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}