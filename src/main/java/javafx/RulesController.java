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
import model.Dao;
import model.Rule;
import model.RuleHbox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class RulesController implements Initializable {

    private String sitename;
    private Stage stage;
    private GuiController guiController;
    private List<RuleHbox> hBoxes = new ArrayList<>();

    public RulesController(GuiController guiController, Stage stage, String sitename) {
        this.sitename = sitename;
        this.stage = stage;
        this.guiController = guiController;
    }


    @FXML
    private Button newRuleButton;

    @FXML
    private Button saveButton;

    @FXML
    private VBox vBoxForRules;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vBoxForRules.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        ArrayList<Rule> rules = Dao.getRules(sitename);

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
               addRuleEditorToScreen(new Rule(sitename));
           }catch (Exception e){
               e.printStackTrace();
           }
        });

        saveButton.setOnAction(event -> saveRules());
    }

    private void saveRules(){
        List<Rule> newRules = new ArrayList<>();
        for (RuleHbox hBox : hBoxes) {
            Rule rule = new Rule(sitename);
            rule.setTo(hBox.getListTo());
            rule.setFrom(hBox.getListFrom());
            rule.setTime(hBox.getTimeChoice());
            rule.setDestinationType(hBox.getDestinationTypeChoice());
            rule.setForwardType(hBox.getForwardTypeChoice());
            rule.setMelody(hBox.getMelodyChoice());
            newRules.add(rule);
            System.out.println(rule);
        }
        String result = Dao.saveRules(sitename, newRules);
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

        // Список номеров на
        ListView listTo = (ListView) hBox.lookup("#toListView");
        ObservableList<String> observableTo = FXCollections.observableList(rule.getTo());
        listTo.setItems(observableTo);

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

        // Выбор типа связи
        ChoiceBox destinationTypeChoice = (ChoiceBox) hBox.lookup("#destinationTypeChoice");
        destinationTypeChoice.setItems(FXCollections.observableArrayList("GSM", "SIP"));
        destinationTypeChoice.setValue(rule.getDestinationType().toString());

        // Выбор мелодии
        ChoiceBox melodyChoice = (ChoiceBox) hBox.lookup("#melodyChoice");
        melodyChoice.setItems(FXCollections.observableArrayList("m(simple)"));
        melodyChoice.setValue(rule.getMelody());


        Button fromAddButton = (Button) hBox.lookup("#fromAddButton");

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

//        Button toAddButton = (Button) hBox.lookup("#toAddButton");
//        toAddButton.setOnAction(event -> {
//            Dialog dialog = new TextInputDialog("");
//            dialog.setTitle("Введите номер");
//            dialog.
//            Optional<String> result = dialog.showAndWait();
//
//            String entered = "";
//            if (result.isPresent()) {
//                entered = result.get();
//                System.out.println(entered);
//            }
//
//
//
//        });

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
            vBoxForRules.getChildren().remove(hBox);
            hBoxes.remove(hBox);
        });

        hBoxes.add(new RuleHbox(listFrom, listTo, forwardTypeChoice, destinationTypeChoice, melodyChoice, timeChoice));
        vBoxForRules.getChildren().add(hBox);
    }
}
