package javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import dao.Dao;

import java.net.URL;
import java.util.ResourceBundle;


public class FilterEditDialogController implements Initializable {

    @FXML
    private CheckBox MAIL_ANTISPAM;

    @FXML
    private CheckBox BLOCKED_BY_IP;

    @FXML
    private CheckBox INCOMING_CALL;

    @FXML
    private CheckBox ANSWER_CALL;

    @FXML
    private CheckBox REQUEST_NUMBER;

    @FXML
    private CheckBox ENDED_CALL;

    @FXML
    private CheckBox NUMBER_FREE;

    @FXML
    private CheckBox INCOMING_CALL_NOT_REGISTER;

    @FXML
    private CheckBox SENDING_NUMBER;

    @FXML
    private CheckBox NO_NUMBERS_LEFT;

    @FXML
    private CheckBox REPEATED_REQUEST;

    @FXML
    private CheckBox ONLY_ACTIVE_SITE;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
                     MAIL_ANTISPAM.setSelected(Dao.getSettingBoolean("MAIL_ANTISPAM"));
                     BLOCKED_BY_IP.setSelected(Dao.getSettingBoolean("BLOCKED_BY_IP"));
                     INCOMING_CALL.setSelected(Dao.getSettingBoolean("INCOMING_CALL"));
                       ANSWER_CALL.setSelected(Dao.getSettingBoolean("ANSWER_CALL"));
                    REQUEST_NUMBER.setSelected(Dao.getSettingBoolean("REQUEST_NUMBER"));
                        ENDED_CALL.setSelected(Dao.getSettingBoolean("ENDED_CALL"));
                       NUMBER_FREE.setSelected(Dao.getSettingBoolean("NUMBER_FREE"));
        INCOMING_CALL_NOT_REGISTER.setSelected(Dao.getSettingBoolean("INCOMING_CALL_NOT_REGISTER"));
                    SENDING_NUMBER.setSelected(Dao.getSettingBoolean("SENDING_NUMBER"));
                   NO_NUMBERS_LEFT.setSelected(Dao.getSettingBoolean("NO_NUMBERS_LEFT"));
                  REPEATED_REQUEST.setSelected(Dao.getSettingBoolean("REPEATED_REQUEST"));
                  ONLY_ACTIVE_SITE.setSelected(Gui.onlyActiveSite);
    }

    public void actionClose(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
    }


    public void checkBoxPressed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (!(source instanceof CheckBox)) {
            return;
        }

        CheckBox clickedCheckBox = (CheckBox) source;

        String id = clickedCheckBox.getId();
        boolean state = clickedCheckBox.isSelected();

        Dao.setSetting(id, ""+state);
    }
}
