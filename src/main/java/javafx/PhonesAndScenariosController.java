package javafx;

import dao.Dao;
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
import json.JsonScenarioBindings;
import model.User;

import java.net.URL;
import java.util.*;

@SuppressWarnings("Duplicates")
public class PhonesAndScenariosController implements Initializable {

    private Stage stage;
    private GuiController guiController;
    private User user;


    public PhonesAndScenariosController(GuiController guiController, Stage stage, User user) {
        this.user = user;
        this.stage = stage;
        this.guiController = guiController;
    }

    @FXML
    private Button saveButton;

    @FXML
    private VBox vBoxForRules;

    private ObservableList<String> availableScenarios;
    private HashMap<String, Integer> phones;
    private HashMap<Integer, String> scenarios;
    private HashMap<String, Integer> scenariosReverse;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vBoxForRules.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        saveButton.setOnAction(event -> {
            ObservableList<Node> children = vBoxForRules.getChildren();
            HashMap<String, Integer> phoneAndScenarioId = new HashMap<>();
            for (Node child : children) {
                Label phoneNumberLabel = (Label) child.lookup("#phoneNumber");
                ComboBox<String> scenarioListComboBox = (ComboBox<String>) child.lookup("#scenarioList");
                String phoneNumber = phoneNumberLabel.getText();
                String scenarioName = scenarioListComboBox.getValue();
                if (scenarioName != null && !"".equals(scenarioName)) {
                    int scenarioId = scenariosReverse.get(scenarioName);
                    phoneAndScenarioId.put(phoneNumber, scenarioId);
                }
            }
            try{
                String s = Dao.setScenariosBindings(user, phoneAndScenarioId);
                GuiController.showInformationAlert(s);
            }catch (Exception e){
                e.printStackTrace();
                GuiController.showErrorAlert(e.toString());
            }
        });
        refreshScreen();
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

        new Thread(() ->
                Platform.runLater(() -> {
                    try {
                        JsonScenarioBindings scenarioBindings = Dao.getScenarioBindings(user);
                        phones = scenarioBindings.getPhones();
                        scenarios = scenarioBindings.getScenarios();
                        availableScenarios = FXCollections.observableArrayList(scenarios.values());
                        availableScenarios.add("");
                        scenariosReverse = scenarioBindings.getScenariosReverse();

                        for (Map.Entry<String, Integer> entry : phones.entrySet()) {
                            String phone = entry.getKey();
                            String scenario = scenarios.get(entry.getValue());
                            addBindToScreen(phone, scenario);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
        ).start();
    }


    private void addBindToScreen(String phone, String scenario) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("item_scenario_bind.fxml"));
        Parent root = fxmlLoader.load();
        AnchorPane mainNode = (AnchorPane) root.lookup("#mainNode");
        VBox rootNode = (VBox) root.lookup("#ruleHbox");
        rootNode.setStyle("-fx-border-color: #0072ff");
        rootNode.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        Label phoneNumber = (Label) rootNode.lookup("#phoneNumber");
        phoneNumber.setText(phone);

        ComboBox<String> scenarioList = (ComboBox<String>) rootNode.lookup("#scenarioList");
        scenarioList.setItems(availableScenarios);
        scenarioList.setValue(scenario);

        vBoxForRules.getChildren().add(mainNode);
    }
}
