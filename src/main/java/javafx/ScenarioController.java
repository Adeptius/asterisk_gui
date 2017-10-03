package javafx;

import enums.DestinationType;
import enums.ForwardType;
import enums.RuleType;
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
import json.JsonChainElement;
import json.JsonRule;
import json.JsonUserAudio;
import model.*;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static enums.DestinationType.GSM;
import static enums.RuleType.DEFAULT;
import static javafx.GuiController.showErrorAlert;

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

    private List<InnerPhone> innerPhones;
    private List<String> melodies;
    private int currentScenarioId;
    private List<JsonUserAudio> userMelodies;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vBoxForRules.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        try {
            innerPhones = Dao.getInnerAndOuterPhones(user).getInnerPhones();
            melodies = Dao.getMelodies();
            userMelodies = Dao.getUserAudio(user);
        } catch (Exception e) {
            e.printStackTrace();
            showInformationAlert(e.toString());
            return;
        }

        newRuleButton.setOnAction(e -> addNewRuleToScreen());

        saveScenarioButton.setOnAction(event -> {
            ObservableList<Node> childrens = vBoxForRules.getChildren();
            List<JsonRule> rules = new ArrayList<>();
            for (Node childNode : childrens) {
                JsonRule rule = new JsonRule();
                rule.setName(((TextField) childNode.lookup("#nameTextField")).getText());

                ComboBox ruleTypeChoice = (ComboBox) childNode.lookup("#ruleTypeChoice");
                rule.setType(RuleType.valueOf(ruleTypeChoice.getSelectionModel().getSelectedItem().toString()));

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

                rule.setMelody(((ComboBox) childNode.lookup("#melodyChoice")).getSelectionModel().getSelectedItem().toString());

                String greeting = ((ComboBox<String>) childNode.lookup("#greetingComboBox")).getSelectionModel().getSelectedItem();

                String message = ((ComboBox<String>) childNode.lookup("#messageComboBox")).getSelectionModel().getSelectedItem();

//                String responsibleUserName = ((ComboBox<String>) childNode.lookup("#amoResponsibleComboBox")).getSelectionModel().getSelectedItem();
//                if (amoUsers != null){
//                    String responsibleId = amoUsers.get(responsibleUserName);
//                    if (responsibleUserName.equals("")){
//                        rule.setAmoResponsibleId(null);
//                    }else {
//                        rule.setAmoResponsibleId(responsibleId);
//                    }
//                }

                if (!org.apache.commons.lang3.StringUtils.isBlank(greeting)) {
                    int id = userMelodies.stream()
                            .filter(userMel -> userMel.getName().equals(greeting))
                            .findFirst().get().getId();
                    rule.setGreeting(id);
                }

                if (!org.apache.commons.lang3.StringUtils.isBlank(message)) {
                    int id = userMelodies.stream()
                            .filter(userMel -> userMel.getName().equals(message))
                            .findFirst().get().getId();
                    rule.setMessage(id);
                }

                VBox vBoxForChain = (VBox) childNode.lookup("#vBoxForChain");
                ObservableList<Node> chainChildrens = vBoxForChain.getChildren();

                HashMap<Integer, JsonChainElement> jsonChainElements = new HashMap<>();
                for (int i = 0; i < chainChildrens.size(); i++) {
                    Node chainChildren = chainChildrens.get(i);
                    JsonChainElement element = new JsonChainElement();

//                    Label labelChainNumber = (Label) chainChildren.lookup("#labelChainNumber");
//                    System.out.println(labelChainNumber);
//                    int number = Integer.parseInt(labelChainNumber.getText().substring(13));
                    element.setPosition(i);

                    // создать элемент
                    ListView listTo = (ListView) chainChildren.lookup("#toListView");
                    element.setToList(new ArrayList<>(listTo.getItems()));

                    ComboBox forwardTypeChoice = (ComboBox) chainChildren.lookup("#forwardTypeChoice");
                    String name1 = forwardTypeChoice.getSelectionModel().getSelectedItem().toString();
                    ForwardType forwardType = ForwardType.valueOf(name1);
                    element.setForwardType(forwardType);

                    ComboBox destinationTypeChoice = (ComboBox) chainChildren.lookup("#destinationTypeChoice");
                    String name = destinationTypeChoice.getSelectionModel().getSelectedItem().toString();
                    DestinationType destinationType = DestinationType.valueOf(name);
                    element.setDestinationType(destinationType);

                    element.setAwaitingTime(Integer.parseInt(((TextField) chainChildren.lookup("#timeChoice")).getText()));

                    jsonChainElements.put(i, element);
                }

                rule.setChain(jsonChainElements);
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
            JsonRule rule = new JsonRule();
            rule.setDays(new boolean[]{false, false, false, false, false, false, false});
            rule.setType(DEFAULT);
            rule.setChain(new HashMap<>());
            rule.setMelody("slow");
            addRuleEditorToScreen(rule);
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
                    List<JsonRule> rules = scenario.getRules();
                    for (JsonRule rule : rules) {
                        addRuleEditorToScreen(rule);
                    }
                } else {
                    addNewRuleToScreen();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        })).start();
    }

    private void addRuleEditorToScreen(JsonRule rule) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("item_rule.fxml"));
        Parent root = fxmlLoader.load();
        AnchorPane mainNode = (AnchorPane) root.lookup("#mainNode");
        VBox rootNode = (VBox) root.lookup("#ruleHbox");
        rootNode.setStyle("-fx-border-color: #0072ff");
        rootNode.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        // Имя правила
        TextField nameTextField = (TextField) rootNode.lookup("#nameTextField");
        nameTextField.setText(rule.getName());

        if (rule.getType() == DEFAULT) {
            rootNode.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        }

        // Тип правила
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


        // Дни работы
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

        // Часы работы
        ComboBox fromHour = (ComboBox) rootNode.lookup("#CBfromHour");
        fromHour.setItems(hours);
        fromHour.getSelectionModel().select(rule.getStartHour());
        ComboBox toHour = (ComboBox) rootNode.lookup("#CBtoHour");
        toHour.setItems(hours);
        toHour.getSelectionModel().select(rule.getEndHour());

        //Удаление правила
        Button removeRuleButton = (Button) rootNode.lookup("#removeRuleButton");
        removeRuleButton.setOnAction(e -> vBoxForRules.getChildren().remove(mainNode));

        VBox vBoxForChain = (VBox) rootNode.lookup("#vBoxForChain"); // сюда вставлять цепочку



        //Добавить шаг
        Button addStepButton = (Button) rootNode.lookup("#addStepButton");
        addStepButton.setOnAction(e -> {
            JsonChainElement element = new JsonChainElement();
            element.setForwardType(ForwardType.TO_ALL);
            element.setToList(new ArrayList<>());
            element.setAwaitingTime(600);
            element.setDestinationType(GSM);
            addStepToChain(vBoxForChain, element);
        });

        //Удалить шаг
        Button removeStepButton = (Button) rootNode.lookup("#removeStepButton");
        removeStepButton.setOnAction(e -> {
            ObservableList<Node> childrenChainElements = vBoxForChain.getChildren();
            int size = childrenChainElements.size();
            if (size > 0) {
                childrenChainElements.remove(size - 1);
            }
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

        List<String> userMelodiesNames = userMelodies.stream().map(JsonUserAudio::getName).collect(Collectors.toList());
        userMelodiesNames.add("");

        HashMap<Integer, String> userMelodyIdAndName = new HashMap<>();
        userMelodies.forEach(melody -> userMelodyIdAndName.put(melody.getId(), melody.getName()));


        ObservableList<String> fxMelodies = FXCollections.observableList(userMelodiesNames);

        ComboBox<String> greetingComboBox = (ComboBox<String>) rootNode.lookup("#greetingComboBox");
        greetingComboBox.setItems(fxMelodies);
        greetingComboBox.setValue(userMelodyIdAndName.get(rule.getGreeting()));

        ComboBox<String> messageComboBox = (ComboBox<String>) rootNode.lookup("#messageComboBox");
        messageComboBox.setItems(fxMelodies);
        messageComboBox.setValue(userMelodyIdAndName.get(rule.getMessage()));

//        if (amoUsers != null){
//            String amoResponsibleId = rule.getAmoResponsibleId();
//            String currentAmoResponsibleName = "";
//            for (Map.Entry<String, String> entry : amoUsers.entrySet()) {
//                if (entry.getValue().equals(amoResponsibleId)){
//                    currentAmoResponsibleName = entry.getKey();
//                }
//            }
//
//            ComboBox<String> amoResponsibleComboBox = (ComboBox<String>) rootNode.lookup("#amoResponsibleComboBox");
//            ObservableList<String> observableList = FXCollections.observableArrayList(new ArrayList<>(amoUsers.keySet()));
//            observableList.add("");
//            amoResponsibleComboBox.setItems(observableList);
//            amoResponsibleComboBox.setValue(currentAmoResponsibleName);
//        }

        HashMap<Integer, JsonChainElement> chain = rule.getChain();
        for (int i = 0; i < chain.size(); i++) {
            addStepToChain(vBoxForChain, chain.get(i));
        }
        vBoxForRules.getChildren().add(mainNode);
    }

    private void addStepToChain(VBox vBoxForChain, JsonChainElement element) {
        Parent chainRoot = null;
        try {
            chainRoot = new FXMLLoader(getClass().getResource("item_chain_element.fxml")).load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

//        Label labelChainNumber = (Label) chainRoot.lookup("#labelChainNumber");
//        labelChainNumber.setText("Цепочка. Шаг " + (i));

//      Кнопка добавления номера на
        Button toAddButton = (Button) chainRoot.lookup("#toAddButton");

//      Список сип номеров
        ComboBox<String> sipList = (ComboBox<String>) chainRoot.lookup("#sipList");
        TextField sipField = (TextField) chainRoot.lookup("#sipField");
        sipList.setOnAction(event -> {
            String s = sipList.getSelectionModel().getSelectedItem();
            if (s != null) {
                sipField.setText(s);
            }
        });

        sipList.setItems(FXCollections.observableList(innerPhones.stream().map(InnerPhone::getNumber).collect(Collectors.toList())));

//      Выбор времени
        TextField timeChoice = (TextField) chainRoot.lookup("#timeChoice");
        timeChoice.setText(element.getAwaitingTime() + "");

//      Выбор типа переадресации
        ComboBox<String> forwardTypeChoice = (ComboBox<String>) chainRoot.lookup("#forwardTypeChoice");
        forwardTypeChoice.setItems(FXCollections.observableArrayList("TO_ALL", "QUEUE", "RANDOM"));
        forwardTypeChoice.setValue(element.getForwardType().toString());

//      Список номеров на
        ObservableList<String> observableTo = FXCollections.observableList(element.getToList());

//             Выбор типа связи
        ComboBox destinationTypeChoice = (ComboBox) chainRoot.lookup("#destinationTypeChoice");
        destinationTypeChoice.setItems(FXCollections.observableArrayList("GSM", "SIP"));
        destinationTypeChoice.setValue(element.getDestinationType().toString());

//             Список номеров на
        ListView listTo = (ListView) chainRoot.lookup("#toListView");
        listTo.setItems(observableTo);
        toAddButton.setOnMouseClicked(e -> {
            observableTo.add(sipField.getText());
        });

//             Удаление номера на
        Button toDeleteButton = (Button) chainRoot.lookup("#toDeleteButton");
        toDeleteButton.setOnAction(event -> observableTo.remove(listTo.getSelectionModel().getSelectedItem().toString()));
        toDeleteButton.setVisible(false);

//             Скрытие кнопки "удалить"
        listTo.getSelectionModel().selectedItemProperty().addListener(observable -> {
            if (listTo.getSelectionModel().getSelectedItem() == null) {
                toDeleteButton.setVisible(false);
            } else {
                toDeleteButton.setVisible(true);
            }
        });

        vBoxForChain.getChildren().add(chainRoot);
    }


    public static void showInformationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
