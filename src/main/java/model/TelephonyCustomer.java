package model;


import java.util.ArrayList;
import java.util.List;

public class TelephonyCustomer extends Customer{


    public final CustomerType type = CustomerType.TELEPHONY;
    private ArrayList<String> innerPhonesList = new ArrayList<>();
    private ArrayList<String> outerPhonesList = new ArrayList<>();
    private int outerNumbersCount;
    private int innerNumbersCount;

    public TelephonyCustomer(String name, String eMail, String googleAnalyticsTrackingId, String password, int innerNumbersCount, int outerNumbersCount) throws Exception{
        super(name, eMail, googleAnalyticsTrackingId, password);
        this.outerNumbersCount = outerNumbersCount;
        this.innerNumbersCount = innerNumbersCount;
        updateNumbers();
    }

    @Override
    public void updateNumbers() throws Exception{
    }

    public int getOuterNumbersCount() {
        return outerNumbersCount;
    }

    public int getInnerNumbersCount() {
        return innerNumbersCount;
    }

    public ArrayList<String> getInnerPhonesList() {
        return innerPhonesList;
    }

    public void setInnerPhonesList(ArrayList<String> innerPhonesList) {
        this.innerPhonesList = innerPhonesList;
    }

    public ArrayList<String> getOuterPhonesList() {
        return outerPhonesList;
    }

    public void setOuterPhonesList(ArrayList<String> outerPhonesList) {
        this.outerPhonesList = outerPhonesList;
    }

    @Override
    public String toString() {
        return "TelephonyCustomer{" +
                "name='" + name + '\'' +
                ", eMail='" + eMail + '\'' +
                ", googleAnalyticsTrackingId='" + googleAnalyticsTrackingId + '\'' +
                ", password='" + password + '\'' +
                ", rules=" + rules +
                ", innerPhonesList=" + innerPhonesList +
                ", outerPhonesList=" + outerPhonesList +
                "} ";
    }
}
