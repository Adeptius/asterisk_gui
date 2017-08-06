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

    public void setOuterPhones(List<OuterPhone> outerPhones) {
        this.outerPhones = outerPhones;
    }

    public List<Site> getSites() {
        return sites;
    }

    public void setSites(List<Site> sites) {
        this.sites = sites;
    }

    public List<String> getAvailableNumbers() {
        return availableNumbers;
    }


    public RoistatAccount getRoistatAccount() {
        return roistatAccount;
    }

    public void setRoistatAccount(RoistatAccount roistatAccount) {
        this.roistatAccount = roistatAccount;
    }

    public AmoAccount getAmoAccount() {
        return amoAccount;
    }

    public void setAmoAccount(AmoAccount amoAccount) {
        this.amoAccount = amoAccount;
    }

    public void setAvailableNumbers(List<String> availableNumbers) {
        this.availableNumbers = availableNumbers;
    }

    public Telephony getTelephony() {
        return telephony;
    }

    public void setTelephony(Telephony telephony) {
        this.telephony = telephony;
    }

    public String getLogin() {
        return login;
    }

    public List<Scenario> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<Scenario> scenarios) {
        this.scenarios = scenarios;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
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
//                ", site=" + tracking +
                ", telephony=" + telephony +
                '}';
    }
}
