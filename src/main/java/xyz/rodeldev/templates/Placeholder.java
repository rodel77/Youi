package xyz.rodeldev.templates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Placeholder {
    private String name, description = "";
    private int min = 0, max = 0;
    private List<String> selectors = new ArrayList<>();

    public Placeholder(String name){
        this.name = name;
    }

    public Placeholder setSelectors(String... selectors){
        this.selectors.addAll(Arrays.asList(selectors));
        return this;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public int getMax(){
        return max;
    }

    public int getMin(){
        return min;
    }

    public Placeholder setDescription(String description){
        this.description = description;
        return this;
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