package model;


import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class RuleHbox {

    public ListView listFrom;
    public ListView listTo;
    public ChoiceBox forwardTypeChoice;
    public ChoiceBox destinationTypeChoice;
    public ChoiceBox melodyChoice;
    public TextField timeChoice;

    public RuleHbox(ListView listFrom, ListView listTo, ChoiceBox forwardTypeChoice, ChoiceBox destinationTypeChoice, ChoiceBox melodyChoice, TextField timeChoice) {
        this.listFrom = listFrom;
        this.listTo = listTo;
        this.forwardTypeChoice = forwardTypeChoice;
        this.destinationTypeChoice = destinationTypeChoice;
        this.melodyChoice = melodyChoice;
        this.timeChoice = timeChoice;
    }


    public ArrayList<String> getListFrom() {
        ArrayList<String> list = new ArrayList<>();
        for (Object o : listFrom.getItems().toArray()) {
            list.add((String) o);
        }
        return list;
    }

    public ArrayList<String> getListTo() {
        ArrayList<String> list = new ArrayList<>();
        for (Object o : listTo.getItems().toArray()) {
            list.add((String) o);
        }
        return list;
    }

    public ForwardType getForwardTypeChoice() {
        String s = forwardTypeChoice.getSelectionModel().getSelectedItem().toString();

        if (s.equals("Всем сразу")) {
            return ForwardType.TO_ALL;
        } else {
            return ForwardType.QUEUE;
        }
    }

    public DestinationType getDestinationTypeChoice() {
        String s = destinationTypeChoice.getSelectionModel().getSelectedItem().toString();
        return DestinationType.valueOf(s);
    }

    public String getMelodyChoice() {
        return melodyChoice.getSelectionModel().getSelectedItem().toString();
    }

    public int getTimeChoice() {
        try {
            return Integer.parseInt(timeChoice.getText());
        } catch (Exception e) {
            return 600;
        }
    }
}
