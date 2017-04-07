package javafx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import dao.Dao;
import java.net.URL;
import java.util.ResourceBundle;


public class ScriptController implements Initializable {

    private String site;
    private Stage stage;

    public ScriptController(String site, Stage stage) {
        this.site = site;
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
        if(site != null){
            textForScript.setText(Dao.getScriptForSite(site));
        }else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Сайт не выбран");
            alert.showAndWait();
        }
    }

    private void close(){
        stage.hide();
    }
}
