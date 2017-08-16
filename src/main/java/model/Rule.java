package model;


import java.util.Arrays;
import java.util.List;

public class Rule {

    private int id;
    private String name;
    private String scenario;
    private List<String> toList;
    private ForwardType forwardType;
    private DestinationType destinationType;
    private RuleType type;
    private ScenarioStatus status;
    private int awaitingTime;
    private String melody;
    private int startHour;
    private int endHour;
    private boolean[] days;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    public List<String> getToList() {
        return toList;
    }

    public void setToList(List<String> toList) {
        this.toList = toList;
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

    public ScenarioStatus getStatus() {
        return status;
    }

    public void setStatus(ScenarioStatus status) {
        this.status = status;
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

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(Integer startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(Integer endHour) {
        this.endHour = endHour;
    }

    public RuleType getType() {
        return type;
    }

    public void setType(RuleType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", scenario='" + scenario + '\'' +
                ", toList=" + toList +
                ", forwardType=" + forwardType +
                ", destinationType=" + destinationType +
                ", status=" + status +
                ", awaitingTime=" + awaitingTime +
                ", melody='" + melody + '\'' +
                ", startHour=" + startHour +
                ", endHour=" + endHour +
                ", days=" + Arrays.toString(days) +
                '}';
    }

    public boolean[] getDays() {
        return days;
    }

    public void setDays(boolean[] days) {
        this.days = days;
    }
}
