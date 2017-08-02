package javafx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import dao.Dao;
import json.JsonScenario;
import model.*;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScenarioController implements Initializable {

    private Stage stage;
    private GuiController guiController;
    private User user;


    public ScenarioController(GuiController guiController, Stage stage, User user) {
        this.user = user;
        this.stage = stage;
        this.guiController = guiController;
    }

    @FXML
    private Button newScenarioButton;

    @FXML
    private VBox vBoxForRules;

    //Костыль что бы список доступных номеров менялся на всех правилах
    private List<ChoiceBox<String>> choiseBoxes = new ArrayList<>();
    private List<String> availableNumbers;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vBoxForRules.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
//        List<Scenario> scenarios = user.getScenarios();

//        for (Scenario scenario : scenarios) {
//            System.out.println(scenario);
//        }
//        try {
//            for (Scenario scenario : scenarios) {
//                addScenarioEditorToScreen(scenario);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        newScenarioButton.setOnAction(event -> {
            try {
                Scenario scenario = new Scenario();
                scenario.setDays(new boolean[]{false, false, false, false, false, false, false});
                addScenarioEditorToScreen(scenario);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        refreshScreen();

    }

    private void refreshScreen(){

        if (vBoxForRules.getChildren().size()>0){
            vBoxForRules.getChildren().clear();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        new Thread(() -> {
            Platform.runLater(() -> {
                try {
                    List<Scenario> scenarios = Dao.getScenarios(user);
                    for (Scenario scenario : scenarios) {
                        System.out.println(scenario);
                    }
                    for (Scenario scenario : scenarios) {
                        addScenarioEditorToScreen(scenario);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }

    private void addScenarioEditorToScreen(Scenario scenario) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("item_rule.fxml"));
        Parent root = fxmlLoader.load();
        AnchorPane mainNode = (AnchorPane) root.lookup("#mainNode");
        VBox rootNode = (VBox) root.lookup("#ruleHbox");
        rootNode.setStyle("-fx-border-color: #0072ff");
        rootNode.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        TextField nameTextField = (TextField) rootNode.lookup("#nameTextField");
        nameTextField.setText(scenario.getName());

        Label statusLabel = (Label) rootNode.lookup("#statusLabel");
        ScenarioStatus status = scenario.getStatus();
        if (status == ScenarioStatus.ACTIVATED) {
            statusLabel.setText("Активен");
            statusLabel.setStyle("-fx-background-color: lightgreen");

        } else if (status == ScenarioStatus.DEACTIVATED) {
            statusLabel.setText("Не активен");
            statusLabel.setStyle("-fx-background-color: gray");

        } else if (status == ScenarioStatus.DEFAULT) {
            statusLabel.setText("По умолчанию");
            statusLabel.setStyle("-fx-background-color: khaki");

        }


        // Список номеров с
        ListView listFrom = (ListView) rootNode.lookup("#fromListView");
        ObservableList<String> observableFrom = FXCollections.observableList(scenario.getFromList());
        listFrom.setItems(observableFrom);

        // Кнопка добавления номера с
        Button fromAddButton = (Button) rootNode.lookup("#fromAddButton");
        fromAddButton.setVisible(false);

        // Кнопка добавления номера на
        Button toAddButton = (Button) rootNode.lookup("#toAddButton");
        toAddButton.setVisible(false);

        // Добавление номера на
        TextField fieldTo = (TextField) rootNode.lookup("#fieldTo");
        fieldTo.setOnKeyReleased(event -> {
            String text = fieldTo.getText();
            Matcher regexMatcher = Pattern.compile("^\\d{9}$").matcher(text);
            if (regexMatcher.find()) {
                toAddButton.setVisible(true);
            } else {
                toAddButton.setVisible(false);
            }
        });


        // Список сип номеров
        HBox sipHbox = (HBox) rootNode.lookup("#sipHbox");
        ChoiceBox<String> sipList = (ChoiceBox<String>) rootNode.lookup("#sipList");
        TextField sipField = (TextField) rootNode.lookup("#sipField");
        sipList.setOnAction(event -> {
            String s = sipList.getSelectionModel().getSelectedItem();
            if (s != null) {
                sipField.setText(s);
                toAddButton.setVisible(true);
            }
        });

        if (user.getTelephony() != null) {
            sipList.setItems(FXCollections.observableList(user.getTelephony().getInnerPhonesList()));
        }

        // Выбор номеров с
        ChoiceBox<String> listFromChoiseBox = (ChoiceBox<String>) rootNode.lookup("#listFromChoiseBox");
        choiseBoxes.add(listFromChoiseBox);
        availableNumbers = Dao.getAvailableNumbers(user);
        listFromChoiseBox.setItems(FXCollections.observableList(availableNumbers));
        listFromChoiseBox.setOnAction(e -> {
            if (listFromChoiseBox.getSelectionModel().getSelectedItem() != null) {
                fromAddButton.setVisible(true);
            }
        });
        fromAddButton.setOnMouseClicked(e -> {
            String choised = listFromChoiseBox.getSelectionModel().getSelectedItem();
            if (choised != null) {
                observableFrom.add(choised);
                availableNumbers.remove(choised);
                for (ChoiceBox<String> choiseBox : choiseBoxes) {
                    choiseBox.setItems(FXCollections.observableList(new ArrayList<>()));
                    choiseBox.setItems(FXCollections.observableList(availableNumbers));
                }
            }
        });


        // Выбор времени
        TextField timeChoice = (TextField) rootNode.lookup("#timeChoice");
        timeChoice.setText(scenario.getAwaitingTime() + "");

        // Выбор типа переадресации
        ChoiceBox forwardTypeChoice = (ChoiceBox) rootNode.lookup("#forwardTypeChoice");
        forwardTypeChoice.setItems(FXCollections.observableArrayList("Всем сразу", "По очереди"));
        forwardTypeChoice.setValue(scenario.getForwardType().name);

        if ("Всем сразу".equals(forwardTypeChoice.getSelectionModel().getSelectedItem().toString())) {
            timeChoice.setDisable(true);
            timeChoice.setText(600 + "");
        }
        forwardTypeChoice.getSelectionModel().selectedItemProperty().addListener(observable -> {
            if ("Всем сразу".equals(forwardTypeChoice.getSelectionModel().getSelectedItem().toString())) {
                timeChoice.setDisable(true);
                timeChoice.setText(600 + "");
            } else {
                timeChoice.setDisable(false);
                timeChoice.setText(10 + "");
            }
        });

        // Список номеров на
        ObservableList<String> observableTo = FXCollections.observableList(scenario.getToList());

        // Выбор типа связи
        ChoiceBox destinationTypeChoice = (ChoiceBox) rootNode.lookup("#destinationTypeChoice");
        destinationTypeChoice.setItems(FXCollections.observableArrayList("GSM", "SIP"));
        destinationTypeChoice.setValue(scenario.getDestinationType().toString());
        HBox gsmVbox = (HBox) rootNode.lookup("#gsmVbox");
        destinationTypeChoice.setOnAction(event -> {
            if (destinationTypeChoice.getSelectionModel().getSelectedIndex() == 0) { // если GSM
                toAddButton.setVisible(false);
                gsmVbox.setVisible(true);
                sipHbox.setVisible(false);
                observableTo.clear();
            } else {
                toAddButton.setVisible(false);
                gsmVbox.setVisible(false);
                sipHbox.setVisible(true);
                observableTo.clear();
            }
        });
        if (DestinationType.valueOf(destinationTypeChoice.getSelectionModel().getSelectedItem().toString()) == DestinationType.GSM) {
            gsmVbox.setVisible(true);
            sipHbox.setVisible(false);
        } else {
            gsmVbox.setVisible(false);
            sipHbox.setVisible(true);
        }

        // Список номеров на
        ListView listTo = (ListView) rootNode.lookup("#toListView");
        listTo.setItems(observableTo);
        toAddButton.setOnMouseClicked(e -> {
            if (DestinationType.valueOf(destinationTypeChoice.getSelectionModel().getSelectedItem().toString()) == DestinationType.GSM) {
                String text = "0" + fieldTo.getText();
                observableTo.add(text);
                fieldTo.setText("");
                toAddButton.setVisible(false);
            } else {
                if (sipField.getText() != null && !sipField.getText().equals("")) {
                    observableTo.add(sipField.getText());
                    sipField.setText("");
                    toAddButton.setVisible(false);
                }
            }
        });

        sipField.setOnKeyTyped(event -> {
            if (sipField.getText() != null && !sipField.getText().equals("")) {
                toAddButton.setVisible(true);
            } else {
                toAddButton.setVisible(false);
            }
        });

        // Выбор мелодии
        ChoiceBox melodyChoice = (ChoiceBox) rootNode.lookup("#melodyChoice");
        List<String> melodies = Dao.getMelodies();
        melodyChoice.setItems(FXCollections.observableArrayList(melodies));
        melodyChoice.getSelectionModel().select(0);
        if (scenario.getMelody() == null) {
            melodyChoice.setValue("none");
        } else {
            melodyChoice.setValue(scenario.getMelody());
        }

        // Удаление номера с
        Button fromDeleteButton = (Button) rootNode.lookup("#fromDeleteButton");
        fromDeleteButton.setOnAction(event -> observableFrom.remove(listFrom.getSelectionModel().getSelectedItem().toString()));
        fromDeleteButton.setVisible(false);

        // Скрытие кнопки "удалить"
        listFrom.getSelectionModel().selectedItemProperty().addListener(observable -> {
            if (listFrom.getSelectionModel().getSelectedItem() == null) {
                fromDeleteButton.setVisible(false);
            } else {
                fromDeleteButton.setVisible(true);
            }
        });

        // Удаление номера на
        Button toDeleteButton = (Button) rootNode.lookup("#toDeleteButton");
        toDeleteButton.setOnAction(event -> observableTo.remove(listTo.getSelectionModel().getSelectedItem().toString()));
        toDeleteButton.setVisible(false);


        // Скрытие кнопки "удалить"
        listTo.getSelectionModel().selectedItemProperty().addListener(observable -> {
            if (listTo.getSelectionModel().getSelectedItem() == null) {
                toDeleteButton.setVisible(false);
            } else {
                toDeleteButton.setVisible(true);
            }
        });

//        Дни работы
        CheckBox cb1 = (CheckBox) rootNode.lookup("#CB1");
        CheckBox cb2 = (CheckBox) rootNode.lookup("#CB2");
        CheckBox cb3 = (CheckBox) rootNode.lookup("#CB3");
        CheckBox cb4 = (CheckBox) rootNode.lookup("#CB4");
        CheckBox cb5 = (CheckBox) rootNode.lookup("#CB5");
        CheckBox cb6 = (CheckBox) rootNode.lookup("#CB6");
        CheckBox cb7 = (CheckBox) rootNode.lookup("#CB7");
        boolean[] days = scenario.getDays();
        cb1.setSelected(days[0]);
        cb2.setSelected(days[1]);
        cb3.setSelected(days[2]);
        cb4.setSelected(days[3]);
        cb5.setSelected(days[4]);
        cb6.setSelected(days[5]);
        cb7.setSelected(days[6]);

        ObservableList<String> hours = FXCollections.observableArrayList(Arrays.asList("0", "1", "2", "3", "4",
                "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"));

        ChoiceBox fromHour = (ChoiceBox) rootNode.lookup("#CBfromHour");
        fromHour.setItems(hours);
        fromHour.getSelectionModel().select(scenario.getStartHour());
        ChoiceBox toHour = (ChoiceBox) rootNode.lookup("#CBtoHour");
        toHour.setItems(hours);
        toHour.getSelectionModel().select(scenario.getEndHour());

        //Удаление правила
        Button removeRuleButton = (Button) rootNode.lookup("#removeRuleButton");
        removeRuleButton.setOnAction(event -> {
            try {
                String result = Dao.removeScenario(user, scenario.getId());
                showInformationAlert(result);
            } catch (Exception e) {
                e.printStackTrace();
                showInformationAlert(e.getMessage());
            }
            refreshScreen();
        });

//        Активация сценария
        Button activateRuleButton = (Button) rootNode.lookup("#activateRuleButton");
        activateRuleButton.setOnAction(event -> {
            try {
                String result = Dao.activateScenario(user, scenario.getId());
                showInformationAlert(result);
            } catch (Exception e) {
                showInformationAlert(e.getMessage());
                e.printStackTrace();
            }
            refreshScreen();
        });

        Button deactivateRuleButton = (Button) rootNode.lookup("#deactivateRuleButton");
        deactivateRuleButton.setOnAction(event -> {
            try {
                String result = Dao.deactivateScenario(user, scenario.getId());
                showInformationAlert(result);
            } catch (Exception e) {
                showInformationAlert(e.getMessage());
                e.printStackTrace();
            }
            refreshScreen();
        });


        //Сохранение правила
        Button saveScenarioButton = (Button) rootNode.lookup("#saveRuleButton");
        saveScenarioButton.setOnAction(event -> {

            JsonScenario jsonScenario = new JsonScenario();


            jsonScenario.setId(scenario.getId());
            try {
                String melody = melodyChoice.getSelectionModel().getSelectedItem().toString();
                jsonScenario.setMelody(melody);
            } catch (NullPointerException e) {
                jsonScenario.setMelody("default");
            }
            String text = nameTextField.getText();
            try {
                text = new String(text.getBytes(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            jsonScenario.setName(text);

            try {
                jsonScenario.setAwaitingTime(Integer.parseInt(timeChoice.getText()));
            } catch (Exception e) {
                jsonScenario.setAwaitingTime(600);
            }

            String s = destinationTypeChoice.getSelectionModel().getSelectedItem().toString();
            jsonScenario.setDestinationType(DestinationType.valueOf(s));

            s = forwardTypeChoice.getSelectionModel().getSelectedItem().toString();
            if (s.equals("Всем сразу")) {
                jsonScenario.setForwardType(ForwardType.TO_ALL);
            } else {
                jsonScenario.setForwardType(ForwardType.QUEUE);
            }

            ArrayList<String> list = new ArrayList<>();
            for (Object o : listTo.getItems().toArray()) {
                list.add((String) o);
            }
            jsonScenario.setToNumbers(list);

            list = new ArrayList<>();
            for (Object o : listFrom.getItems().toArray()) {
                list.add((String) o);
            }
            jsonScenario.setFromNumbers(list);

            jsonScenario.setStartHour(fromHour.getSelectionModel().getSelectedIndex());
            jsonScenario.setEndHour(toHour.getSelectionModel().getSelectedIndex());
            jsonScenario.setDays(new boolean[]{
                    cb1.isSelected(),
                    cb2.isSelected(),
                    cb3.isSelected(),
                    cb4.isSelected(),
                    cb5.isSelected(),
                    cb6.isSelected(),
                    cb7.isSelected()
            });

//            jsonScenario.setStatus(scenario.getStatus());

            System.out.println("Сохраняю: " + jsonScenario);

            try {
                String response = Dao.setScenario(user, jsonScenario);
                showInformationAlert(response);
            } catch (Exception e) {
                e.printStackTrace();
                showInformationAlert(e.getMessage());
            }
            refreshScreen();
        });

        vBoxForRules.getChildren().add(mainNode);
    }

    public static void showInformationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
