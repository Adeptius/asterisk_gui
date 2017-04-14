package javafx;

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
import model.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RulesController implements Initializable {

    private Customer customer;
    private Stage stage;
    private GuiController guiController;


    public RulesController(GuiController guiController, Stage stage, Customer customer) {
        this.customer = customer;
        this.stage = stage;
        this.guiController = guiController;
    }


    @FXML
    private Button newRuleButton;

    @FXML
    private Button saveButton;

    @FXML
    private VBox vBoxForRules;

    //Костыль что бы список доступных номеров менялся на всех правилах
    private List<ChoiceBox<String>> choiseBoxes = new ArrayList<>();
    private List<String> availableNumbers;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vBoxForRules.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        List<Rule> rules = customer.getRules();

        for (Rule rule : rules) {
            System.out.println(rule);
        }
        try {
            for (Rule rule : rules) {
                addRuleEditorToScreen(rule);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        newRuleButton.setOnAction(event -> {
           try{
               addRuleEditorToScreen(new Rule(customer.getName()));
           }catch (Exception e){
               e.printStackTrace();
           }
        });

        saveButton.setOnAction(event -> saveRules());
    }

    private void saveRules(){
        List<Rule> newRules = new ArrayList<>();
        List<HBox> hboxes = new ArrayList<>();
        int hBoxCount = vBoxForRules.getChildren().size();
        for (int i = 0; i < hBoxCount; i++) {
            hboxes.add((HBox) vBoxForRules.getChildren().get(i));
        }
        List<RuleHbox> hBoxes = new ArrayList<>();

        for (HBox hBox : hboxes) {
            hBoxes.add(new RuleHbox(
                    ((ListView) hBox.lookup("#fromListView")),
                    ((ListView) hBox.lookup("#toListView")),
                    ((ChoiceBox) hBox.lookup("#forwardTypeChoice")),
                    ((ChoiceBox) hBox.lookup("#destinationTypeChoice")),
                    ((ChoiceBox) hBox.lookup("#melodyChoice")),
                    ((TextField) hBox.lookup("#timeChoice"))));
        }

        for (RuleHbox hBox : hBoxes) {
            Rule rule = new Rule(customer.getName());
            rule.setTo(hBox.getListTo());
            rule.setFrom(hBox.getListFrom());
            rule.setTime(hBox.getTimeChoice());
            rule.setDestinationType(hBox.getDestinationTypeChoice());
            rule.setForwardType(hBox.getForwardTypeChoice());
            rule.setMelody(hBox.getMelodyChoice());
            newRules.add(rule);
        }
        String result = Dao.saveRules(customer.getName(), newRules);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Результат");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
    }

    private void addRuleEditorToScreen(Rule rule) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("item_rule.fxml"));
        Parent root = fxmlLoader.load();
        HBox hBox = (HBox) root.lookup("#ruleHbox");
        HBox innerhBox = (HBox) hBox.lookup("#innerRuleHbox");
        innerhBox.setStyle("-fx-border-color: #0072ff");
        innerhBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        // Список номеров с
        ListView listFrom = (ListView) hBox.lookup("#fromListView");
        ObservableList<String> observableFrom = FXCollections.observableList(rule.getFrom());
        listFrom.setItems(observableFrom);

        // Кнопка добавления номера с
        Button fromAddButton = (Button) hBox.lookup("#fromAddButton");
        fromAddButton.setVisible(false);

         // Кнопка добавления номера на
        Button toAddButton = (Button) hBox.lookup("#toAddButton");
        toAddButton.setVisible(false);

        // Добавление номера на
        TextField fieldTo = (TextField) hBox.lookup("#fieldTo");
        fieldTo.setOnKeyReleased(event -> {
            String text = fieldTo.getText();
            Matcher regexMatcher = Pattern.compile("^\\d{9}$").matcher(text);
            if (regexMatcher.find()){
                toAddButton.setVisible(true);
            }else {
                toAddButton.setVisible(false);
            }
        });



        // Список сип номеров
        HBox sipHbox = (HBox) hBox.lookup("#sipHbox");
        ChoiceBox<String> sipList = (ChoiceBox<String>) hBox.lookup("#sipList");
        TextField sipField = (TextField) hBox.lookup("#sipField");
        sipList.setOnAction(event -> {
            String s = sipList.getSelectionModel().getSelectedItem();
            if (s != null){
                sipField.setText(s);
                toAddButton.setVisible(true);
            }
        });
        if (customer instanceof TelephonyCustomer){
            sipList.setItems(FXCollections.observableList(((TelephonyCustomer) customer).getInnerPhonesList()));
        }else {
//            sipList.setScaleY(0);
//            sipVbox.setScaleY(0.5);
        }

        // Выбор номеров с
        ChoiceBox<String> listFromChoiseBox = (ChoiceBox<String>) hBox.lookup("#listFromChoiseBox");
        choiseBoxes.add(listFromChoiseBox);
        availableNumbers = customer.availableNumbers;
        listFromChoiseBox.setItems(FXCollections.observableList(availableNumbers));
        listFromChoiseBox.setOnAction(e -> {
            if (listFromChoiseBox.getSelectionModel().getSelectedItem() != null){
                fromAddButton.setVisible(true);
            }
        });
        fromAddButton.setOnMouseClicked(e -> {
            String choised = listFromChoiseBox.getSelectionModel().getSelectedItem();
            if (choised != null){
                observableFrom.add(choised);
                availableNumbers.remove(choised);
                for (ChoiceBox<String> choiseBox : choiseBoxes) {
                    choiseBox.setItems(FXCollections.observableList(new ArrayList<>()));
                    choiseBox.setItems(FXCollections.observableList(availableNumbers));
                }
            }
        });



        // Выбор времени
        TextField timeChoice = (TextField) hBox.lookup("#timeChoice");
        timeChoice.setText(rule.getTime()+"");

        // Выбор типа переадресации
        ChoiceBox forwardTypeChoice = (ChoiceBox) hBox.lookup("#forwardTypeChoice");
        forwardTypeChoice.setItems(FXCollections.observableArrayList("Всем сразу", "По очереди"));
        forwardTypeChoice.setValue(rule.getForwardType().name);

        if ("Всем сразу".equals(forwardTypeChoice.getSelectionModel().getSelectedItem().toString())){
            timeChoice.setDisable(true);
            timeChoice.setText(600+"");
        }
        forwardTypeChoice.getSelectionModel().selectedItemProperty().addListener(observable -> {
            if ("Всем сразу".equals(forwardTypeChoice.getSelectionModel().getSelectedItem().toString())){
                timeChoice.setDisable(true);
                timeChoice.setText(600+"");
            }else {
                timeChoice.setDisable(false);
                timeChoice.setText(10+"");
            }
        });

        // Список номеров на
        ObservableList<String> observableTo = FXCollections.observableList(rule.getTo());

        // Выбор типа связи
        ChoiceBox destinationTypeChoice = (ChoiceBox) hBox.lookup("#destinationTypeChoice");
        destinationTypeChoice.setItems(FXCollections.observableArrayList("GSM", "SIP"));
        destinationTypeChoice.setValue(rule.getDestinationType().toString());
        HBox gsmVbox = (HBox) hBox.lookup("#gsmVbox");
        destinationTypeChoice.setOnAction(event -> {
            if (destinationTypeChoice.getSelectionModel().getSelectedIndex() == 0){ // если GSM
                toAddButton.setVisible(false);
                gsmVbox.setVisible(true);
                sipHbox.setVisible(false);
                observableTo.clear();
            }else {
                toAddButton.setVisible(false);
                gsmVbox.setVisible(false);
                sipHbox.setVisible(true);
                observableTo.clear();
            }
        });
        if (DestinationType.valueOf(destinationTypeChoice.getSelectionModel().getSelectedItem().toString())==DestinationType.GSM){
            gsmVbox.setVisible(true);
            sipHbox.setVisible(false);
        }else {
            gsmVbox.setVisible(false);
            sipHbox.setVisible(true);
        }

        // Список номеров на
        ListView listTo = (ListView) hBox.lookup("#toListView");
        listTo.setItems(observableTo);
        toAddButton.setOnMouseClicked(e -> {
            if (DestinationType.valueOf(destinationTypeChoice.getSelectionModel().getSelectedItem().toString())==DestinationType.GSM) {
                String text = "0" + fieldTo.getText();
                observableTo.add(text);
                fieldTo.setText("");
                toAddButton.setVisible(false);
            }else {
                if (sipField.getText() != null && !sipField.getText().equals("")){
                    observableTo.add(sipField.getText());
                    sipField.setText("");
                    toAddButton.setVisible(false);
                }
            }
        });

        sipField.setOnKeyTyped(event -> {
            if (sipField.getText() != null && !sipField.getText().equals("")){
                toAddButton.setVisible(true);
            }else {
                toAddButton.setVisible(false);
            }
        });

        // Выбор мелодии
        ChoiceBox melodyChoice = (ChoiceBox) hBox.lookup("#melodyChoice");
        List<String> melodies = Dao.getMelodies();
        melodyChoice.setItems(FXCollections.observableArrayList(melodies));
        melodyChoice.getSelectionModel().select(0);
        melodyChoice.setValue(rule.getMelody());

        // Удаление номера с
        Button fromDeleteButton = (Button) hBox.lookup("#fromDeleteButton");
        fromDeleteButton.setOnAction(event -> observableFrom.remove(listFrom.getSelectionModel().getSelectedItem().toString()));
        fromDeleteButton.setVisible(false);

        // Скрытие кнопки "удалить"
        listFrom.getSelectionModel().selectedItemProperty().addListener(observable -> {
            if (listFrom.getSelectionModel().getSelectedItem() == null){
                fromDeleteButton.setVisible(false);
            }else{
                fromDeleteButton.setVisible(true);
            }
        });

        // Удаление номера на
        Button toDeleteButton = (Button) hBox.lookup("#toDeleteButton");
        toDeleteButton.setOnAction(event -> observableTo.remove(listTo.getSelectionModel().getSelectedItem().toString()));
        toDeleteButton.setVisible(false);

        // Скрытие кнопки "удалить"
        listTo.getSelectionModel().selectedItemProperty().addListener(observable -> {
            if (listTo.getSelectionModel().getSelectedItem() == null){
                toDeleteButton.setVisible(false);
            }else{
                toDeleteButton.setVisible(true);
            }
        });

        //Удаление правила
        Button removeRuleButton = (Button) hBox.lookup("#removeRuleButton");
        removeRuleButton.setOnAction(event -> {
//            hBoxes.remove(current);
            vBoxForRules.getChildren().remove(hBox);
        });

        vBoxForRules.getChildren().add(hBox);
    }
}
