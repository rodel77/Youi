package xyz.rodeldev.templates;

public class Placeholder {
    private String name;
    private int min = 0, max = 0;

    public Placeholder(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public Placeholder setConstraint(int min, int max){
        this.min = min;
        this.max = max;
        return this;
    }

    public boolean isConstrained(){
        return min!=max && max!=0;
    }

    public boolean isInRange(int amount){
        if(!isConstrained()) return true;
        return amount>=min && amount<=max;
    }
}