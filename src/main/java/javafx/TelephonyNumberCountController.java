package javafx;


import dao.Dao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import json.JsonTelephony;
import json.JsonTracking;
import model.User;

import java.net.URL;
import java.util.ResourceBundle;

public class TelephonyNumberCountController implements Initializable{

    private GuiController guiController;
    private Stage stage;
    private User user;


    public TelephonyNumberCountController(GuiController guiController, Stage stage, User user) {
        this.guiController = guiController;
        this.stage = stage;
        this.user = user;
    }

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private TextField innerText;

    @FXML
    private TextField outerText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnCancel.setOnAction(e -> cancel());
        innerText.setText(user.getTelephony().getInnerCount()+"");
                outerText.setText(user.getTelephony().getOuterCount()+"");
        btnSave.setOnAction(e -> save());
    }

    private void cancel(){
        stage.hide();
    }

    private void save(){

        JsonTelephony jsonTelephony = new JsonTelephony();
        jsonTelephony.setInnerCount(Integer.parseInt(innerText.getText()));
        jsonTelephony.setOuterCount(Integer.parseInt(outerText.getText()));

        String result;
        try{
            result = Dao.setTelephonyNumberCount(user,jsonTelephony);
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
