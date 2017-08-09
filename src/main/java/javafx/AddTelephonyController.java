package javafx;


import dao.Dao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import model.User;

import java.net.URL;
import java.util.ResourceBundle;

public class AddTelephonyController implements Initializable {

    private GuiController guiController;
    private Stage stage;
    private User user;


    public AddTelephonyController(GuiController guiController, Stage stage, User user) {
        this.guiController = guiController;
        this.stage = stage;
        this.user = user;
    }

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnCancel.setOnAction(e -> cancel());
//        if (user.getTelephony() != null) {// если мы добавляем
//            btnSave.setVisible(false);
//        }
        btnSave.setOnAction(e -> save());
    }

    private void cancel() {
        stage.hide();
    }

    public void remove() {
        String result;
        try {
            result = Dao.removeTelephony(user);
        } catch (Exception e) {
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

    private void save() {


        String result;
        try {
            result = Dao.addTelephony(user);
        } catch (Exception e) {
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
