package xyz.rodeldev.components;

abstract public class IComponent<T extends IComponent<?>> {
    private String name, description;
    private int minimum = -1, maximum = -1;

    public IComponent(String name) {
        this.name = name;
    }

    abstract public String getType();

    public String getName(){
        return this.name;
    }

    public T setDescription(String description){
        this.description = description;
        return (T) this;
    }

    public String getDescription(){
        return this.description;
    }

    public T setLimit(int minimum, int maximum){
        this.minimum = minimum;
        this.maximum = maximum;
        return (T) this;
    }

    public int getMinimum(){
        return this.minimum;
    }

    public int getMaximum(){
        return this.maximum;
    }
}