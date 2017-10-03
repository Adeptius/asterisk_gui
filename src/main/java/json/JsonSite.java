package json;


import model.OuterPhone;

import java.util.List;

public class JsonSite {

    private String name;
    private String standardNumber;
    private String script;
    private String googleTrackingId;
    private Integer timeToBlock;
    private List<String> blackList;
    private List<OuterPhone> outerPhones;
    private List<String> connectedPhones;

    public String getGoogleTrackingId() {
        return googleTrackingId;
    }

    public void setGoogleTrackingId(String googleTrackingId) {
        this.googleTrackingId = googleTrackingId;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStandardNumber() {
        return standardNumber;
    }

    public void setStandardNumber(String standardNumber) {
        this.standardNumber = standardNumber;
    }

    public Integer getTimeToBlock() {
        return timeToBlock;
    }

    public void setTimeToBlock(Integer timeToBlock) {
        this.timeToBlock = timeToBlock;
    }

    public List<String> getBlackList() {
        return blackList;
    }

    public void setBlackList(List<String> blackList) {
        this.blackList = blackList;
    }

    public List<OuterPhone> getOuterPhones() {
        return outerPhones;
    }

    public void setOuterPhones(List<OuterPhone> outerPhones) {
        this.outerPhones = outerPhones;
    }

    public List<String> getConnectedPhones() {
        return connectedPhones;
    }

    public void setConnectedPhones(List<String> connectedPhones) {
        this.connectedPhones = connectedPhones;
    }

    @Override
    public String toString() {
        return "JsonSite{" +
                "name='" + name + '\'' +
                ", standardNumber='" + standardNumber + '\'' +
                ", timeToBlock=" + timeToBlock +
                ", blackList=" + blackList +
                ", outerPhones=" + outerPhones +
                ", connectedPhones=" + connectedPhones +
                '}';
    }
}
