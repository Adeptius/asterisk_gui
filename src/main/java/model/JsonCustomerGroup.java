package model;


public class JsonCustomerGroup {

    public String name;
    public CustomerType type;

    public JsonCustomerGroup(String name, CustomerType type) {
        this.name = name;
        this.type = type;
    }
}
