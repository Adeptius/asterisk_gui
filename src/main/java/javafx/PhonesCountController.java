package javafx;


import dao.Dao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import json.JsonInnerAndOuterPhones;
import json.JsonPhoneCount;
import model.User;

import java.net.URL;
import java.util.ResourceBundle;

public class PhonesCountController implements Initializable{

    private GuiController guiController;
    private Stage stage;
    private User user;


    public PhonesCountController(GuiController guiController, Stage stage, User user) {
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
    public void initialize(URL location, ResourceBundle resources){
        try {
            btnCancel.setOnAction(e -> cancel());
            JsonInnerAndOuterPhones innerAndOuterPhones = Dao.getInnerAndOuterPhones(user);
            innerText.setText(innerAndOuterPhones.getInnerPhones().size()+"");
            outerText.setText(innerAndOuterPhones.getOuterPhones().size()+"");
            btnSave.setOnAction(e -> save());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void cancel(){
        stage.hide();
    }

    private void save(){

        JsonPhoneCount jsonPhoneCount = new JsonPhoneCount();
        jsonPhoneCount.setInnerCount(Integer.parseInt(innerText.getText()));
        jsonPhoneCount.setOuterCount(Integer.parseInt(outerText.getText()));

        String result;
        try{
            result = Dao.setPhonesCount(user, jsonPhoneCount);
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
