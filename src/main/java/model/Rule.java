package model;


import java.util.Arrays;
import java.util.List;

public class Rule {

    private int id;
    private String login;
    private String name;
    private String scenario;
    private List<String> toList;
    private ForwardType forwardType;
    private DestinationType destinationType;
    private ScenarioStatus status;
    private int awaitingTime;
    private String melody;
    private Integer startHour;
    private Integer endHour;
    private boolean[] days;
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public Integer getStartHour() {
        return startHour;
    }

    public void setStartHour(Integer startHour) {
        this.startHour = startHour;
    }

    public Integer getEndHour() {
        return endHour;
    }

    public void setEndHour(Integer endHour) {
        this.endHour = endHour;
    }



    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "id=" + id +
                ", login='" + login + '\'' +
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
                ", user=" + user +
                '}';
    }

    public boolean[] getDays() {
        return days;
    }

    public void setDays(boolean[] days) {
        this.days = days;
    }
}
