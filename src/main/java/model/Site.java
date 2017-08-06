package model;

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

    private String blackIps = "";

    private User user;

    LinkedList<String> blackLinkedList;

    public LinkedList<String> getBlackList() {
        if (blackLinkedList == null) {
            blackLinkedList = new LinkedList<>();
            if (blackIps == null) {
                blackIps = "";
            }
            String[] spl = blackIps.split(" ");
            for (int i = 1; i < spl.length; i++) {
                blackLinkedList.add(0, spl[i]);
            }
        }
        return blackLinkedList;
    }

    public void addIpToBlackList(String ip) {
        getBlackList().add(0, ip);
        blackIps = (" " + ip) + blackIps;
        checkBlackListSize();
    }

    public void removeIpFromBlackList(String ip) {
        List<String> currentList = getBlackList();
        currentList.removeIf(s -> s.equals(ip));
        StringBuilder sb = new StringBuilder(200);
        for (String s : currentList) {
            sb.append(" ").append(s);
        }
        blackIps = sb.toString();
    }

    private void checkBlackListSize() {
        if (blackLinkedList.size() > 99) {
            for (int i = 0; i < 10; i++) {
                blackLinkedList.removeLast();
            }
        }
        StringBuilder sb = new StringBuilder(200);
        for (String s : blackLinkedList) {
            sb.append(" ").append(s);
        }
        blackIps = sb.toString();
    }

    public List<OuterPhone> getOuterPhones(){
        return user.getOuterPhones().stream()
                .filter(outerPhone -> name.equals(outerPhone.getSitename()))
                .collect(Collectors.toList());
    }
    public int getId() {
        return id;
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

    public String getBlackIps() {
        return blackIps;
    }

    public void setBlackIps(String blackIps) {
        this.blackIps = blackIps;
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
                ", blackIps='" + blackIps + '\'' +
                ", user=" + user.getLogin() +
                '}';
    }
}
