package model;



public class History {

    private String from;
    private String to;
    private String date;
    private int timeToAnswerInSeconds;
    private int talkingTime;
    private String googleId;
    private String request;
    private String callUniqueId;
    private String timeToAnswerForCustomer;
    private int timeToAnswerForWeb;


    public String getTimeToAnswerForCustomer() {
        return timeToAnswerForCustomer;
    }

    public void setTimeToAnswerForCustomer(String timeToAnswerForCustomer) {
        this.timeToAnswerForCustomer = timeToAnswerForCustomer;
    }

    public int getTimeToAnswerForWeb() {
        return timeToAnswerForWeb;
    }

    public void setTimeToAnswerForWeb(int timeToAnswerForWeb) {
        this.timeToAnswerForWeb = timeToAnswerForWeb;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getTimeToAnswerInSeconds() {
        return timeToAnswerInSeconds;
    }

    public void setTimeToAnswerInSeconds(int timeToAnswerInSeconds) {
        this.timeToAnswerInSeconds = timeToAnswerInSeconds;
    }

    public int getTalkingTime() {
        return talkingTime;
    }

    public void setTalkingTime(int talkingTime) {
        this.talkingTime = talkingTime;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getCallUniqueId() {
        return callUniqueId;
    }

    public void setCallUniqueId(String callUniqueId) {
        this.callUniqueId = callUniqueId;
    }

    @Override
    public String toString() {
        return "History{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", date='" + date + '\'' +
                ", timeToAnswerInSeconds=" + timeToAnswerInSeconds +
                ", talkingTime=" + talkingTime +
                ", googleId='" + googleId + '\'' +
                ", reques='" + request + '\'' +
                ", callUniqueId='" + callUniqueId + '\'' +
                '}';
    }
}
