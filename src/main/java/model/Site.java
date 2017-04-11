package model;



import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Site extends Customer {

    public final CustomerType type = CustomerType.TRACKING;
    public Site(String name,String standartNumber, String googleAnalyticsTrackingId, String eMail,
                List<String> blackIps, String password, int timeToBlock, int outerNumbersCount) throws Exception {
        super(name, eMail, googleAnalyticsTrackingId, password);
        this.standartNumber = standartNumber;
        this.blackIps = blackIps;
        this.timeToBlock = timeToBlock;
        this.outerNumbersCount = outerNumbersCount;
//        ArrayList<String> outerPhones = PhonesDao.getCustomersNumbers(name,false);
//        for (String outerPhone : outerPhones) {
//            phones.add(new Phone(outerPhone));
//        }
//        updateNumbers();
    }

    @Override
    public void updateNumbers() throws Exception {

    }

    private List<String> blackIps;
    private List<Phone> phones = new ArrayList<>();
    private String standartNumber;
    private int timeToBlock;
    private int outerNumbersCount;


    public int getOuterNumbersCount() {
        return outerNumbersCount;
    }

    public void setTimeToBlock(int timeToBlock) {
        this.timeToBlock = timeToBlock;
    }

    public int getTimeToBlock() {
        return timeToBlock;
    }

    public List<Phone> getPhones() {
        return phones;
    }

    public String getStandartNumber() {
        return standartNumber;
    }

//    public long getLastEmailTime() {
//        return lastEmailTime;
//    }

//    public void setLastEmailTime(long lastEmailTime) {
//        this.lastEmailTime = lastEmailTime;
//    }

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
                ", eMail='" + eMail + '\'' +
                ", password='" + password + '\'' +
                ", timeToBlock=" + timeToBlock +
                ", rules=" + rules +
//                ", lastEmailTime=" + lastEmailTime +
                '}';
    }
}
