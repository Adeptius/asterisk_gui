package javafx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import dao.Dao;
import json.JsonRule;
import model.*;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.Deflater;

import static javafx.GuiController.showErrorAlert;
import static model.DestinationType.SIP;
import static model.ForwardType.TO_ALL;
import static model.RuleType.DEFAULT;
import static model.RuleType.NORMAL;
import static model.ScenarioStatus.DEACTIVATED;

public class ScenarioController implements Initializable {

    private Stage stage;
    private GuiController guiController;
    private String scenarioName;
    private User user;


    public ScenarioController(GuiController guiController, Stage stage, User user, String scenario) {
        this.user = user;
        this.stage = stage;
        this.guiController = guiController;
        this.scenarioName = scenario;
    }

    @FXML
    private Button newRuleButton;

    @FXML
    private Button saveScenarioButton;

    @FXML
    private VBox vBoxForRules;

    //Костыль что бы список доступных номеров менялся на всех правилах
//    private List<ComboBox<String>> choiseBoxes = new ArrayList<>();
//    private List<String> availableNumbers;

    List<InnerPhone> innerPhones;
    List<String> melodies;
    int currentScenarioId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vBoxForRules.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        try {
            innerPhones = Dao.getInnerAndOuterPhones(user).getInnerPhones();
            melodies = Dao.getMelodies();
        } catch (Exception e) {
            e.printStackTrace();
            showInformationAlert(e.toString());
            return;
        }

        newRuleButton.setOnAction(e -> addNewRuleToScreen());

        saveScenarioButton.setOnAction(event -> {
            ObservableList<Node> childrens = vBoxForRules.getChildren();
            List<Rule> rules = new ArrayList<>();
            for (Node childNode : childrens) {
                Rule rule = new Rule();
                rule.setName(((TextField) childNode.lookup("#nameTextField")).getText());

                ListView listTo = (ListView) childNode.lookup("#toListView");
                rule.setToList(new ArrayList<>(listTo.getItems()));

                ComboBox ruleTypeChoice = (ComboBox) childNode.lookup("#ruleTypeChoice");
                rule.setType(RuleType.valueOf(ruleTypeChoice.getSelectionModel().getSelectedItem().toString()));

                ComboBox forwardTypeChoice = (ComboBox) childNode.lookup("#forwardTypeChoice");
                rule.setForwardType(ForwardType.valueOf(forwardTypeChoice.getSelectionModel().getSelectedItem().toString()));

                ComboBox destinationTypeChoice = (ComboBox) childNode.lookup("#destinationTypeChoice");
                String s = destinationTypeChoice.getSelectionModel().getSelectedItem().toString();
                rule.setDestinationType(DestinationType.valueOf(s));


                rule.setMelody(((ComboBox) childNode.lookup("#melodyChoice")).getSelectionModel().getSelectedItem().toString());

                rule.setAwaitingTime(Integer.parseInt(((TextField) childNode.lookup("#timeChoice")).getText()));

                rule.setDays(new boolean[]{
                        ((CheckBox) childNode.lookup("#CB1")).isSelected(),
                        ((CheckBox) childNode.lookup("#CB2")).isSelected(),
                        ((CheckBox) childNode.lookup("#CB3")).isSelected(),
                        ((CheckBox) childNode.lookup("#CB4")).isSelected(),
                        ((CheckBox) childNode.lookup("#CB5")).isSelected(),
                        ((CheckBox) childNode.lookup("#CB6")).isSelected(),
                        ((CheckBox) childNode.lookup("#CB7")).isSelected()
                });

                rule.setStartHour(((ComboBox) childNode.lookup("#CBfromHour")).getSelectionModel().getSelectedIndex());

                rule.setEndHour(((ComboBox) childNode.lookup("#CBtoHour")).getSelectionModel().getSelectedIndex());

                rules.add(rule);
            }

            Scenario scenario = new Scenario();
            scenario.setId(currentScenarioId);
            scenario.setName(scenarioName);
            scenario.setRules(rules);
            String result = null;
            try {
                result = Dao.setScenario(user, scenario);
                showInformationAlert(result);
            } catch (Exception e) {
                e.printStackTrace();
                showErrorAlert(result);
            }
        });
        refreshScreen();
    }

    private void addNewRuleToScreen() {
        try {
            Rule rule = new Rule();
            rule.setDays(new boolean[]{false, false, false, false, false, false, false});
            rule.setForwardType(TO_ALL);
            rule.setToList(new ArrayList<>());
            rule.setDestinationType(SIP);
            rule.setStatus(DEACTIVATED);
            rule.setType(NORMAL);
            addScenarioEditorToScreen(rule);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshScreen() {
        if (vBoxForRules.getChildren().size() > 0) {
            vBoxForRules.getChildren().clear();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        new Thread(() -> Platform.runLater(() -> {
            try {
                List<Scenario> scenarios = Dao.getScenarios(user);
                Optional<Scenario> first = scenarios.stream().filter(scenario1 -> scenario1.getName().equals(scenarioName)).findFirst();
                if (first.isPresent()) {
                    Scenario scenario = first.get();
                    currentScenarioId = scenario.getId();
                    List<Rule> rules = scenario.getRules();
                    for (Rule rule : rules) {
                        addScenarioEditorToScreen(rule);
                    }
                } else {
                    addNewRuleToScreen();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        })).start();
    }

    private void addScenarioEditorToScreen(Rule rule) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("item_rule.fxml"));
        Parent root = fxmlLoader.load();
        AnchorPane mainNode = (AnchorPane) root.lookup("#mainNode");
        VBox rootNode = (VBox) root.lookup("#ruleHbox");
        rootNode.setStyle("-fx-border-color: #0072ff");
        rootNode.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        TextField nameTextField = (TextField) rootNode.lookup("#nameTextField");
        nameTextField.setText(rule.getName());

        if (rule.getType() == DEFAULT) {
            rootNode.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        }
//        Label statusLabel = (Label) rootNode.lookup("#statusLabel");
//        ScenarioStatus status = rule.getStatus();
//        if (status == ScenarioStatus.ACTIVATED) {
//            statusLabel.setText("Активен");
//            statusLabel.setStyle("-fx-background-color: lightgreen");
//
//        } else if (status == DEACTIVATED) {
//            statusLabel.setText("Не активен");
//            statusLabel.setStyle("-fx-background-color: gray");
//
//        } else if (status == ScenarioStatus.DEFAULT) {
//            statusLabel.setText("По умолчанию");
//            statusLabel.setStyle("-fx-background-color: khaki");
//        }


        // Кнопка добавления номера на
        Button toAddButton = (Button) rootNode.lookup("#toAddButton");
        toAddButton.setVisible(false);

        // Список сип номеров
//        HBox sipHbox = (HBox) rootNode.lookup("#sipHbox");
        ComboBox<String> sipList = (ComboBox<String>) rootNode.lookup("#sipList");
        TextField sipField = (TextField) rootNode.lookup("#sipField");
        sipList.setOnAction(event -> {
            String s = sipList.getSelectionModel().getSelectedItem();
            if (s != null) {
                sipField.setText(s);
                toAddButton.setVisible(true);
            }
        });

        sipList.setItems(FXCollections.observableList(innerPhones.stream().map(InnerPhone::getNumber).collect(Collectors.toList())));

        // Выбор времени
        TextField timeChoice = (TextField) rootNode.lookup("#timeChoice");
        timeChoice.setText(rule.getAwaitingTime() + "");


        ComboBox<String> ruleTypeChoice = (ComboBox<String>) rootNode.lookup("#ruleTypeChoice");
        ruleTypeChoice.setItems(FXCollections.observableArrayList("DEFAULT", "NORMAL"));
        ruleTypeChoice.setValue(rule.getType().toString());
        ruleTypeChoice.setOnAction(event -> {
            String selectedItem = ruleTypeChoice.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                return;
            }
            if (selectedItem.equals("DEFAULT")) {
                rootNode.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            } else if (selectedItem.equals("NORMAL")) {
                rootNode.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });

        // Выбор типа переадресации
        ComboBox<String> forwardTypeChoice = (ComboBox<String>) rootNode.lookup("#forwardTypeChoice");
        forwardTypeChoice.setItems(FXCollections.observableArrayList("TO_ALL", "QUEUE"));
        forwardTypeChoice.setValue(rule.getForwardType().toString());

        if ("TO_ALL".equals(forwardTypeChoice.getSelectionModel().getSelectedItem())) {
            timeChoice.setDisable(true);
            timeChoice.setText(600 + "");
        }
        forwardTypeChoice.getSelectionModel().selectedItemProperty().addListener(observable -> {
            if ("TO_ALL".equals(forwardTypeChoice.getSelectionModel().getSelectedItem())) {
                timeChoice.setDisable(true);
                timeChoice.setText(600 + "");
            } else {
                timeChoice.setDisable(false);
                timeChoice.setText(10 + "");
            }
        });

        // Список номеров на
        ObservableList<String> observableTo = FXCollections.observableList(rule.getToList());

        // Выбор типа связи
        ComboBox destinationTypeChoice = (ComboBox) rootNode.lookup("#destinationTypeChoice");
        destinationTypeChoice.setItems(FXCollections.observableArrayList("GSM", "SIP"));
        destinationTypeChoice.setValue(rule.getDestinationType().toString());

        // Список номеров на
        ListView listTo = (ListView) rootNode.lookup("#toListView");
        listTo.setItems(observableTo);
        toAddButton.setOnMouseClicked(e -> {
            observableTo.add(sipField.getText());
        });

        // Выбор мелодии
        ComboBox melodyChoice = (ComboBox) rootNode.lookup("#melodyChoice");
        melodyChoice.setItems(FXCollections.observableArrayList(melodies));
        melodyChoice.getSelectionModel().select(0);
        if (rule.getMelody() == null) {
            melodyChoice.setValue("none");
        } else {
            melodyChoice.setValue(rule.getMelody());
        }

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
        boolean[] days = rule.getDays();
        ((CheckBox) rootNode.lookup("#CB1")).setSelected(days[0]);
        ((CheckBox) rootNode.lookup("#CB2")).setSelected(days[1]);
        ((CheckBox) rootNode.lookup("#CB3")).setSelected(days[2]);
        ((CheckBox) rootNode.lookup("#CB4")).setSelected(days[3]);
        ((CheckBox) rootNode.lookup("#CB5")).setSelected(days[4]);
        ((CheckBox) rootNode.lookup("#CB6")).setSelected(days[5]);
        ((CheckBox) rootNode.lookup("#CB7")).setSelected(days[6]);

        ObservableList<String> hours = FXCollections.observableArrayList(Arrays.asList("0", "1", "2", "3", "4",
                "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24"));

        ComboBox fromHour = (ComboBox) rootNode.lookup("#CBfromHour");
        fromHour.setItems(hours);
        fromHour.getSelectionModel().select(rule.getStartHour());
        ComboBox toHour = (ComboBox) rootNode.lookup("#CBtoHour");
        toHour.setItems(hours);
        toHour.getSelectionModel().select(rule.getEndHour());

        //Удаление правила
        Button removeRuleButton = (Button) rootNode.lookup("#removeRuleButton");
        removeRuleButton.setOnAction(event -> {
            vBoxForRules.getChildren().remove(mainNode);
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
