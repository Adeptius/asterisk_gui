package model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Site {

    public Site() {
    }

    private int id;

    private String login;

    private String name;

    private String standardNumber;

    private int timeToBlock;

    private List<String> blackList = new ArrayList<>();

    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public String getStandardNumber() {
        return standardNumber;
    }


    public int getTimeToBlock() {
        return timeToBlock;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
        if (user != null) {
            login = user.getLogin();
        }
    }


    @Override
    public String toString() {
        return "Site{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", name='" + name + '\'' +
                ", standardNumber='" + standardNumber + '\'' +
                ", timeToBlock=" + timeToBlock +
//                ", blackIps='" + blackIps + '\'' +
                ", user=" + user.getLogin() +
                '}';
    }
}
