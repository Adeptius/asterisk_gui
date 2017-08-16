package json;

import java.util.HashMap;

public class JsonScenarioBindings {

    private HashMap<String, Integer> phones = new HashMap<>();

    private HashMap<Integer, String> scenarios = new HashMap<>();

    private HashMap<String, Integer> scenariosReverse = new HashMap<>();

    public HashMap<String, Integer> getScenariosReverse() {
        return scenariosReverse;
    }

    public void setScenariosReverse(HashMap<String, Integer> scenariosReverse) {
        this.scenariosReverse = scenariosReverse;
    }

    public JsonScenarioBindings() {
    }

    public HashMap<String, Integer> getPhones() {
        return phones;
    }

    public void setPhones(HashMap<String, Integer> phones) {
        this.phones = phones;
    }

    public HashMap<Integer, String> getScenarios() {
        return scenarios;
    }

    public void setScenarios(HashMap<Integer, String> scenarios) {
        this.scenarios = scenarios;
    }
}
