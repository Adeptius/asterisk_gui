package javafx;


import dao.Dao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import json.JsonTracking;
import model.Telephony;
import model.User;

import java.net.URL;
import java.util.ResourceBundle;

public class TrackingNumberCountController implements Initializable{

    private GuiController guiController;
    private Stage stage;
    private User user;


    public TrackingNumberCountController(GuiController guiController, Stage stage, User user) {
        this.guiController = guiController;
        this.stage = stage;
        this.user = user;
    }

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private TextField numberOfOuterPhones;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnCancel.setOnAction(e -> cancel());
        if (user.getTracking() != null) {// если мы добавляем
            btnSave.setText("Изменить");
            numberOfOuterPhones.setText(String.valueOf(user.getTracking().getSiteNumbersCount()));
        }
        btnSave.setOnAction(e -> save());
    }

    private void cancel(){
        stage.hide();
    }

    private void save(){
        JsonTracking jsonTracking = new JsonTracking();
        jsonTracking.setSiteNumbersCount(Integer.parseInt(numberOfOuterPhones.getText()));
        String result;
        try{
            result = Dao.setTrackingNumberCount(user, jsonTracking);
        }catch (Exception e){
            e.printStackTrace();
            result = e.getMessage();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
        guiController.updateStatus();
        stage.hide();
    }
}
