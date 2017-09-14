package json;


import enums.RuleType;

import java.util.Arrays;
import java.util.HashMap;

public class JsonRule {

    public JsonRule() {
    }

//    private int id;
    private String name;
    private int startHour;
    private int endHour;
    private boolean[] days;
    private RuleType type;
    private HashMap<Integer, JsonChainElement> chain;
    private Integer greeting;
    private Integer message;
    private String melody;
    private String amoResponsibleId;

    public String getAmoResponsibleId() {
        return amoResponsibleId;
    }

    public void setAmoResponsibleId(String amoResponsibleId) {
        this.amoResponsibleId = amoResponsibleId;
    }

    public String getMelody() {
        return melody;
    }

    public void setMelody(String melody) {
        this.melody = melody;
    }

    public Integer getGreeting() {
        return greeting;
    }

    public void setGreeting(Integer greeting) {
        this.greeting = greeting;
    }

    public Integer getMessage() {
        return message;
    }

    public void setMessage(Integer message) {
        this.message = message;
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

    public boolean[] getDays() {
        return days;
    }

    public void setDays(boolean[] days) {
        this.days = days;
    }

    public RuleType getType() {
        return type;
    }



    public void setType(RuleType type) {
        this.type = type;
    }

    public HashMap<Integer, JsonChainElement> getChain() {
        return chain;
    }

    public void setChain(HashMap<Integer, JsonChainElement> chain) {
        this.chain = chain;
    }

    @Override
    public String toString() {
        return "JsonScenario{" +
//                "id=" + id +
                ", name='" + name + '\'' +
                ", startHour=" + startHour +
                ", endHour=" + endHour +
                ", days=" + Arrays.toString(days) +
                '}';
    }
}
