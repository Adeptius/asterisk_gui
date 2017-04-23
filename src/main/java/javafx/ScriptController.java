package javafx;

import com.google.gson.JsonObject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import dao.Dao;
import model.User;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;


public class ScriptController implements Initializable {

    private User user;
    private Stage stage;

    public ScriptController(User user, Stage stage) {
        this.user = user;
        this.stage = stage;
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
            result = Dao.getScriptForUser(user);
            JSONObject object = new JSONObject(result);
            textForScript.setText(object.getString("Message"));

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
