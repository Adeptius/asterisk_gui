package json;



import java.util.HashMap;

public class JsonPipeline {

    private int id;
    private String name;
    private HashMap<Integer, String> statusesIdAndName = new HashMap<>();

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatusesIdAndName(HashMap<Integer, String> statusesIdAndName) {
        this.statusesIdAndName = statusesIdAndName;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public HashMap<Integer, String> getStatusesIdAndName() {
        return statusesIdAndName;
    }

    @Override
    public String toString() {
        return "JsonPipeline{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", statusesIdAndName=" + statusesIdAndName +
                '}';
    }
}
