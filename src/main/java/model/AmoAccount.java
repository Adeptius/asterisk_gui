package model;


public class AmoAccount {

    private String nextelLogin;
    private String amoLogin;
    private String apiKey;
    private String domain;
    private String phoneId;
    private String phoneEnumId;
    private int leadId;
    private User user;
    private boolean cling;

    public boolean isCling() {
        return cling;
    }

    public void setCling(boolean cling) {
        this.cling = cling;
    }

    public int getLeadId() {
        return leadId;
    }

    public void setLeadId(int leadId) {
        this.leadId = leadId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNextelLogin() {
        return nextelLogin;
    }

    public void setNextelLogin(String nextelLogin) {
        this.nextelLogin = nextelLogin;
    }

    public String getAmoLogin() {
        return amoLogin;
    }

    public void setAmoLogin(String amoLogin) {
        this.amoLogin = amoLogin;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(String phoneId) {
        this.phoneId = phoneId;
    }

    public String getPhoneEnumId() {
        return phoneEnumId;
    }

    public void setPhoneEnumId(String phoneEnumId) {
        this.phoneEnumId = phoneEnumId;
    }

    @Override
    public String toString() {
        return "AmoAccount{" +
                "nextelLogin='" + nextelLogin + '\'' +
                ", amoLogin='" + amoLogin + '\'' +
                ", apiKey='" + apiKey + '\'' +
                ", domain='" + domain + '\'' +
                ", phoneId='" + phoneId + '\'' +
                ", phoneEnumId='" + phoneEnumId + '\'' +
                ", user=" + user.getLogin() +
                '}';
    }
}
