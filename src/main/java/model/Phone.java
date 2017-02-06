package model;


import utils.StringUtils;

import java.util.GregorianCalendar;

public class Phone {

    public Phone(String number) {
        this.number = number;
        googleId = "";
        ip       = "";
        busyTime = "";
    }

    private String number;
    private String googleId;
    private String ip;
    private String busyTime; // время которое телефон занят. Отображается в гуи. Вычисляется наблюдателем относительно времени startedBusy
    private String busyTimeText; // время которое телефон занят. Отображается в гуи. Вычисляется наблюдателем относительно времени startedBusy
    private long timeToDie;   // время аренды. Обновляется при вызове extendTime. Если это значение+12000 больше текущего времени - наблюдатель освобождает телефон
    private long startedBusy; // время мс когда был занят телефон. устанавливается при установке айди
    private String utmRequest;

    public String getUtmRequest() {
        return utmRequest;
    }

    public void setUtmRequest(String utmRequest) {
        this.utmRequest = utmRequest;
    }

    public void markFree() {
        this.setGoogleId("");
        this.setIp("");
//        this.busyTime = new SimpleLongProperty();
        this.timeToDie = 0;
        this.startedBusy = 0;
        this.busyTime = "";
    }

    public void extendTime(){
        this.timeToDie = new GregorianCalendar().getTimeInMillis();
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setGoogleId(String googleId) {// если записывается айди - значит он теперь кем-то занят
        this.startedBusy = new GregorianCalendar().getTimeInMillis();
        setBusyTime(0);
        this.googleId = googleId;
    }

    public long getStartedBusy() {
        return startedBusy;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setBusyTime(long busyTime) {
        this.busyTime = StringUtils.getStringedTime(busyTime);
    }

    public void setTimeToDie(long timeToDie) {
        this.timeToDie = timeToDie;
    }

    public String getNumber() {
        return number;
    }

    public String getGoogleId() {
        return googleId;
    }

//    public SimpleStringProperty googleIdProperty() {
//        return googleId;
//    }

    public String getIp() {
        return ip;
    }

//    public SimpleStringProperty ipProperty() {
//        return ip;
//    }

    public String getBusyTimeText() {
        return busyTimeText;
    }

    public void setBusyTimeText(String busyTimeText) {
        this.busyTimeText = busyTimeText;
    }


//    public SimpleStringProperty busyTimeProperty() {
//        return busyTime;
//    }

    public boolean isFree() {// так понятно что телефон ничей
        return googleId.equals("");
    }

    public long getUpdatedTime() {
        return timeToDie;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "number='" + number + '\'' +
                ", googleId='" + googleId + '\'' +
                ", ip='" + ip + '\'' +
                ", busyTime='" + busyTime + '\'' +
                ", busyTimeText='" + busyTimeText + '\'' +
                ", timeToDie=" + timeToDie +
                ", startedBusy=" + startedBusy +
                ", utmRequest='" + utmRequest + '\'' +
                '}';
    }
}
