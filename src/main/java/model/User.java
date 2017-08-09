package model;


import java.util.ArrayList;
import java.util.List;


public class User {

    private String login;
    private String password;
    private String email;
    private String trackingId;
    private Telephony telephony;
    private AmoAccount amoAccount;
    private RoistatAccount roistatAccount;
    private List<String> availableNumbers;
    private List<Scenario> scenarios = new ArrayList<>();
    private List<Site> sites = new ArrayList<>();
    private List<OuterPhone> outerPhones = new ArrayList<>();
    private List<InnerPhone> innerPhones = new ArrayList<>();

    public Site getSiteByName(String sitename) {
        for (Site site : sites) {
            if (site.getName().equals(sitename)) {
                return site;
            }
        }
        return null;
    }

    public List<OuterPhone> getOuterPhones() {
        return outerPhones;
    }

    public List<InnerPhone> getInnerPhones() {
        return innerPhones;
    }
    public RoistatAccount getRoistatAccount() {
        return roistatAccount;
    }

    public AmoAccount getAmoAccount() {
        return amoAccount;
    }

    public Telephony getTelephony() {
        return telephony;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User that = (User) o;

        if (login != null ? !login.equals(that.login) : that.login != null) return false;
        if (password != null ? !password.equals(that.password) : that.password != null) return false;
        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (trackingId != null ? !trackingId.equals(that.trackingId) : that.trackingId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = login != null ? login.hashCode() : 0;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (trackingId != null ? trackingId.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", trackingId='" + trackingId + '\'' +
                ", telephony=" + telephony +
                '}';
    }
}
