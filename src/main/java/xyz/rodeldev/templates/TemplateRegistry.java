package xyz.rodeldev.templates;

import java.util.HashMap;

import com.google.common.collect.ImmutableList;

import xyz.rodeldev.YouiPlugin;

public class TemplateRegistry {
    private static HashMap<String, Template> registry = new HashMap<>();

    public TemplateRegistry(){
        // @TODO: remove this
        register(new Template(YouiPlugin.getInstance(), "test").registerPlaceholders("button1").registerPlaceholder(new Placeholder("constrainedph").setConstraint(1, 2)));
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