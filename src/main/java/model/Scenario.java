package model;


import java.util.Arrays;
import java.util.List;


public class Scenario {

    public Scenario() {
    }

    private int id;
    private String name;
    private List<String> fromList;
    private List<String> toList;
    private ForwardType forwardType;
    private DestinationType destinationType;
    private ScenarioStatus status;
    private int awaitingTime;
    private String melody;
    private int startTime;
    private int endTime;
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

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
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
                "id=" + id +
                ", name='" + name + '\'' +
                ", fromList=" + fromList +
                ", toList=" + toList +
                ", forwardType=" + forwardType +
                ", destinationType=" + destinationType +
                ", status=" + status +
                ", awaitingTime=" + awaitingTime +
                ", melody='" + melody + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", days=" + Arrays.toString(days) +
                '}';
    }
}
