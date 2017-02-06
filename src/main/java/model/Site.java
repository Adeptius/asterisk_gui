package model;


import java.util.List;

public class Site {
    private Phone[] phones;
    private String googleAnalyticsTrackingId;
    private String standartNumber;
    private String mail;
    private String name;
    private String[] blackIps;

    public Site(String name, List<Phone> phoneList, String standartNumber, String googleId, String email, List<String> blackIps) {


    }

    public Phone[] getPhones() {
        return phones;
    }

    public String getGoogleAnalyticsTrackingId() {
        return googleAnalyticsTrackingId;
    }

    public String getStandartNumber() {
        return standartNumber;
    }

    public String getMail() {
        return mail;
    }

    public String getName() {
        return name;
    }

    public String[] getBlackIps() {
        return blackIps;
    }
}
