package model;


import java.util.ArrayList;
import java.util.List;


public class User {

    private String login;
    private String password;
    private String email;
    private String trackingId;
    private AmoAccount amoAccount;
    private RoistatAccount roistatAccount;
    private List<String> availableNumbers;
    private List<Scenario> scenarios = new ArrayList<>();
    private List<Site> sites = new ArrayList<>();
    private List<OuterPhone> outerPhones = new ArrayList<>();
    private List<InnerPhone> innerPhones = new ArrayList<>();

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getTrackingId() {
        return trackingId;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", trackingId='" + trackingId + '\'' +
                '}';
    }
}
