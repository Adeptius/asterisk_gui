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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import json.JsonInnerAndOuterPhones;
import json.JsonScenarioBindings;
import model.OuterPhone;
import model.Scenario;
import model.Site;
import model.User;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@SuppressWarnings("Duplicates")
public class PhonesAndSitesController implements Initializable {

    private GuiController guiController;
    private User user;


    public PhonesAndSitesController(GuiController guiController, Stage stage, User user) {
        this.user = user;
        this.guiController = guiController;
    }

    @FXML
    private Button saveButton;

    @FXML
    private VBox vBoxForRules;

    private ObservableList<String> availableSites;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vBoxForRules.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        saveButton.setOnAction(event -> {
            ObservableList<Node> children = vBoxForRules.getChildren();
            HashMap<String, String> phoneAndSites = new HashMap<>();
            for (Node child : children) {
                Label phoneNumberLabel = (Label) child.lookup("#phoneNumber");
                ComboBox<String> siteListComboBox = (ComboBox<String>) child.lookup("#scenarioList");
                String phoneNumber = phoneNumberLabel.getText();
                String siteName = siteListComboBox.getValue();
                if (siteName != null && !"".equals(siteName)) {
                    phoneAndSites.put(phoneNumber, siteName);
                }
            }
            try{
                String s = Dao.setSitesBindings(user, phoneAndSites);
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

                        List<Site> sites = Dao.getSites(user);
                        List<String> sitesNames = sites.stream().map(Site::getName).collect(Collectors.toList());
                        sitesNames.add("");
                        availableSites = FXCollections.observableArrayList(sitesNames);

                        JsonInnerAndOuterPhones innerAndOuterPhones = Dao.getInnerAndOuterPhones(user);
                        List<OuterPhone> outerPhones = innerAndOuterPhones.getOuterPhones();

                        for (OuterPhone outerPhone : outerPhones) {
                            addBindToScreen(outerPhone.getNumber(), outerPhone.getSitename());
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
        scenarioList.setItems(availableSites);
        scenarioList.setValue(scenario);

        vBoxForRules.getChildren().add(mainNode);
    }
}
