package json;


import model.InnerPhone;
import model.OuterPhone;

import java.util.List;

public class JsonInnerAndOuterPhones {

    public JsonInnerAndOuterPhones(List<InnerPhone> innerPhones, List<OuterPhone> outerPhones) {
        this.innerPhones = innerPhones;
        this.outerPhones = outerPhones;
    }

    private List<InnerPhone> innerPhones;
    private List<OuterPhone> outerPhones;

    public List<InnerPhone> getInnerPhones() {
        return innerPhones;
    }

    public void setInnerPhones(List<InnerPhone> innerPhones) {
        this.innerPhones = innerPhones;
    }

    public List<OuterPhone> getOuterPhones() {
        return outerPhones;
    }

    public void setOuterPhones(List<OuterPhone> outerPhones) {
        this.outerPhones = outerPhones;
    }
}
