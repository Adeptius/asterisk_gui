package model;


public class Call {
    private String calledFrom;
    private String calledToOnePhone;
    private String callState;
    private String direction;
    private String asteriskId;
    private String utm;
    private String googleId;
    private String calledDate;
    private long calledMillis;
    private int secondsFullTime;
//    private int secondsToAnswer;
    private int secondsTalk;

    public Call() {
    }



    public String getCalledFrom() {
        return calledFrom;
    }

    public void setCalledFrom(String calledFrom) {
        this.calledFrom = calledFrom;
    }

    public String getCalledToOnePhone() {
        return calledToOnePhone;
    }

    public void setCalledToOnePhone(String calledToOnePhone) {
        this.calledToOnePhone = calledToOnePhone;
    }

    public String getCallState() {
        return callState;
    }

    public void setCallState(String callState) {
        this.callState = callState;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getAsteriskId() {
        return asteriskId;
    }

    public void setAsteriskId(String asteriskId) {
        this.asteriskId = asteriskId;
    }

    public String getUtm() {
        return utm;
    }

    public void setUtm(String utm) {
        this.utm = utm;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getCalledDate() {
        return calledDate;
    }

    public void setCalledDate(String calledDate) {
        this.calledDate = calledDate;
    }

    public long getCalledMillis() {
        return calledMillis;
    }

    public void setCalledMillis(long calledMillis) {
        this.calledMillis = calledMillis;
    }

    public int getSecondsFullTime() {
        return secondsFullTime;
    }

    public void setSecondsFullTime(int secondsFullTime) {
        this.secondsFullTime = secondsFullTime;
    }

//    public int getSecondsToAnswer() {
//        return secondsToAnswer;
//    }

//    public void setSecondsToAnswer(int secondsToAnswer) {
//        this.secondsToAnswer = secondsToAnswer;
//    }

    public int getSecondsTalk() {
        return secondsTalk;
    }

    public void setSecondsTalk(int secondsTalk) {
        this.secondsTalk = secondsTalk;
    }
}
