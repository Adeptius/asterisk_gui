package model;

public class InnerPhone {

    private String number;

    private String busy;

    private String pass;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getBusy() {
        return busy;
    }

    public void setBusy(String busy) {
        this.busy = busy;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String password) {
        this.pass = password;
    }

    @Override
    public String toString() {
        return "InnerPhone{" +
                "number='" + number + '\'' +
                ", busy='" + busy + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }
}
