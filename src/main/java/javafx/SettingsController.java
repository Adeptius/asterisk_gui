package javafx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Dao;

import java.net.URL;
import java.util.ResourceBundle;


public class SettingsController implements Initializable {

    private Stage stage;

    public SettingsController(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private Button btnCancel;

    @FXML
    private TextField textUpdateRate;

    @FXML
    private Button btnSave;

    @FXML
    private TextField textCleanRate;

    @FXML
    private TextField textServerAdress;

    @FXML
    private TextField textAntiSpam;

    @FXML
    private Pane mainPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnCancel.setOnAction(event -> close());
        btnSave.setOnAction(event -> save());
        textServerAdress.setText(Dao.getSetting("SERVER_ADDRESS_FOR_SCRIPT"));
        textUpdateRate.setText(Dao.getSetting("SECONDS_TO_UPDATE_PHONE_ON_WEB_PAGE"));
        textCleanRate.setText(Dao.getSetting("SECONDS_TO_REMOVE_OLD_PHONES"));
        textAntiSpam.setText(Dao.getSetting("MAIL_ANTISPAM"));
    }

    private void save(){
        String serverAdress = textServerAdress.getText();
        String updateRate = textUpdateRate.getText();
        String cleanRate = textCleanRate.getText();
        String antispam = textAntiSpam.getText();
        stage.hide();

        Dao.setSetting("SERVER_ADDRESS_FOR_SCRIPT", serverAdress);
        Dao.setSetting("SECONDS_TO_UPDATE_PHONE_ON_WEB_PAGE", updateRate);
        Dao.setSetting("SECONDS_TO_REMOVE_OLD_PHONES", cleanRate);
        Dao.setSetting("MAIL_ANTISPAM", antispam);
    }

    private void close(){
        stage.hide();
    }
}
