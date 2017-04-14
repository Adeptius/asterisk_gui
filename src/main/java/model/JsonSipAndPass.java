package model;



public class JsonSipAndPass {

    private String sip;
    private String pass;

    public JsonSipAndPass(String sip, String pass) {
        this.sip = sip;
        this.pass = pass;
    }

    public String getSip() {
        return sip;
    }

    public void setSip(String sip) {
        this.sip = sip;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
