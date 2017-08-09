package javafx;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import dao.Dao;
import json.Message;
import model.User;
import org.json.JSONObject;

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

            Message message = new Gson().fromJson(result, Message.class);
            System.out.println(message.getMessage());
//            JSONObject object = new JSONObject(result);
            textForScript.setText(message.getMessage());

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
