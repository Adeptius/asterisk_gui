package model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Scenario {

    public Scenario() {
    }

    private int id;
    private String name;
    private List<String> fromList = new ArrayList<>();
    private List<String> toList = new ArrayList<>();
    private ForwardType forwardType = ForwardType.TO_ALL;
    private DestinationType destinationType = DestinationType.SIP;
    private ScenarioStatus status = ScenarioStatus.DEACTIVATED;
    private int awaitingTime;
    private String melody;
    private int startHour;
    private int endHour = 24;
    private boolean[] days;

    public List<String> getFromList() {
        return fromList;
    }

    public void setFromList(List<String> fromList) {
        this.fromList = fromList;
    }

    public List<String> getToList() {
        return toList;
    }

    public void setToList(List<String> toList) {
        this.toList = toList;
    }

    public ScenarioStatus getStatus() {
        return status;
    }

    public void setStatus(ScenarioStatus status) {
        this.status = status;
    }

    public boolean[] getDays() {
        return days;
    }

    public void setDays(boolean[] days) {
        this.days = days;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ForwardType getForwardType() {
        return forwardType;
    }

    public void setForwardType(ForwardType forwardType) {
        this.forwardType = forwardType;
    }

    public DestinationType getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(DestinationType destinationType) {
        this.destinationType = destinationType;
    }

    public int getAwaitingTime() {
        return awaitingTime;
    }

    public void setAwaitingTime(int awaitingTime) {
        this.awaitingTime = awaitingTime;
    }

    public String getMelody() {
        return melody;
    }

    public void setMelody(String melody) {
        this.melody = melody;
    }


    @Override
    public String toString() {
        return "Scenario{" +
                "\n  id=" + id +
                "\n  name='" + name + '\'' +
                "\n  fromList=" + fromList +
                "\n  toList=" + toList +
                "\n  forwardType=" + forwardType +
                "\n  destinationType=" + destinationType +
                "\n  status=" + status +
                "\n  awaitingTime=" + awaitingTime +
                "\n  melody='" + melody + '\'' +
                "\n  startHour=" + startHour +
                "\n  endHour=" + endHour +
                "\n  days=" + Arrays.toString(days) +
                "}";
    }
}

