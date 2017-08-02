package javafx;

import dao.Dao;
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
import json.JsonScenario;
import model.*;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("Duplicates")
public class PhonesAndWorkersController implements Initializable {

    private Stage stage;
    private GuiController guiController;
    private User user;


    public PhonesAndWorkersController(GuiController guiController, Stage stage, User user) {
        this.user = user;
        this.stage = stage;
        this.guiController = guiController;
    }

    @FXML
    private Button saveButton;

    @FXML
    private VBox vBoxForRules;

    HashMap<Label, TextField> fields = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vBoxForRules.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        saveButton.setOnAction(event -> {
            HashMap<String, String> hashMapToSave = new HashMap<>();
            for (Map.Entry<Label, TextField> entry : fields.entrySet()) {
                String phone = entry.getValue().getText();
                String person = entry.getKey().getText();
                if (phone != null) {
                    hashMapToSave.put(person, phone);
                }
            }
            try {
                Dao.setAmoBindings(user, hashMapToSave);
            } catch (Exception e) {
                e.printStackTrace();
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
                        fields.clear();
                        HashMap<String, String> amoBindings = Dao.getAmoBindings(user);
                        for (Map.Entry<String, String> entry : amoBindings.entrySet()) {
                            addBindToScreen(entry.getKey(), entry.getValue());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                })
        ).start();
    }


    private void addBindToScreen(String name, String phone) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("item_bind.fxml"));
        Parent root = fxmlLoader.load();
        AnchorPane mainNode = (AnchorPane) root.lookup("#mainNode");
        VBox rootNode = (VBox) root.lookup("#ruleHbox");
        rootNode.setStyle("-fx-border-color: #0072ff");
        rootNode.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        Label worker = (Label) rootNode.lookup("#workerName");
        worker.setText(name);

        TextField phoneField = (TextField) rootNode.lookup("#phone");
        phoneField.setText(phone);

        fields.put(worker, phoneField);
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
