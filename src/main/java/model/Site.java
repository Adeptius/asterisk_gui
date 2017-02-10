package model;


import java.util.List;

public class Site {

    public Site(String name, List<Phone> phones, String standartNumber, String googleAnalyticsTrackingId, String eMail, List<String> blackIps, String password) {
        this.name = name;
        this.phones = phones;
        this.standartNumber = standartNumber;
        this.googleAnalyticsTrackingId = googleAnalyticsTrackingId;
        this.mail = eMail;
        this.blackIps = blackIps;
        this.password = password;
    }

    private List<String> blackIps;
    private String name;
    private List<Phone> phones;
    private String standartNumber;
    private String googleAnalyticsTrackingId;
    private String mail;
    private long lastEmailTime;
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public String getStandartNumber() {
        return standartNumber;
    }

    public String getGoogleAnalyticsTrackingId() {
        return googleAnalyticsTrackingId;
    }

    public String getMail() {
        return mail;
    }

    public long getLastEmailTime() {
        return lastEmailTime;
    }

    public void setLastEmailTime(long lastEmailTime) {
        this.lastEmailTime = lastEmailTime;
    }

    public List<String> getBlackIps() {
        return blackIps;
    }

    @Override
    public String toString() {
        return "Site{" +
                "blackIps=" + blackIps +
                ", name='" + name + '\'' +
                ", phones=" + phones +
                ", standartNumber='" + standartNumber + '\'' +
                ", googleAnalyticsTrackingId='" + googleAnalyticsTrackingId + '\'' +
                ", eMail='" + mail + '\'' +
                ", lastEmailTime=" + lastEmailTime +
                '}';
    }
}
