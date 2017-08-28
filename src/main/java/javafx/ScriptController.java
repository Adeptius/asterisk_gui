package javafx;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import dao.Dao;
import json.JsonMessage;
import model.User;

import java.net.URL;
import java.util.ResourceBundle;


public class ScriptController implements Initializable {

    private User user;
    private Stage stage;
    private String sitename;

    public ScriptController(User user, Stage stage, String sitename) {
        this.user = user;
        this.stage = stage;
        this.sitename = sitename;
    }

    @FXML
    private Button btnClose;

    @FXML
    private TextArea textForScript;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnClose.setOnAction(event -> close());
        textForScript.setWrapText(true);
        String result;
        try{
            result = Dao.getScriptForUser(user, sitename);

            JsonMessage jsonMessage = new Gson().fromJson(result, JsonMessage.class);
            System.out.println(jsonMessage.getMessage());
//            JSONObject object = new JSONObject(result);
            textForScript.setText(jsonMessage.getMessage());

//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Information");
//            alert.setHeaderText(null);
//            alert.setContentText(result);
//            alert.showAndWait();
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Ошибка " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void close(){
        stage.hide();
    }
}
