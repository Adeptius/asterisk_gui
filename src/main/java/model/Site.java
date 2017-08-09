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


    public List<OuterPhone> getOuterPhones(){
        return user.getOuterPhones().stream()
                .filter(outerPhone -> name.equals(outerPhone.getSitename()))
                .collect(Collectors.toList());
    }
    public int getId() {
        return id;
    }

    public List<String> getBlackList() {
        return blackList;
    }

    public void setBlackList(List<String> blackList) {
        this.blackList = blackList;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStandardNumber() {
        return standardNumber;
    }

    public void setStandardNumber(String standardNumber) {
        this.standardNumber = standardNumber;
    }

    public int getTimeToBlock() {
        return timeToBlock;
    }

    public void setTimeToBlock(int timeToBlock) {
        this.timeToBlock = timeToBlock;
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
