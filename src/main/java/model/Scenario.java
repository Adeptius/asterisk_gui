package model;


import json.JsonRule;

import java.util.List;

public class Scenario {

    private int id;
    private String name;
    private List<JsonRule> rules;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JsonRule> getRules() {
        return rules;
    }

    public void setRules(List<JsonRule> rules) {
        this.rules = rules;
    }

    @Override
    public String toString() {
        return "Scenario{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rules=" + rules +
                '}';
    }
}

