package javafx;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import dao.Dao;
import model.User;

import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("Duplicates")
public class DeleteController implements Initializable{

    private Stage stage;
    private GuiController guiController;
    private User user;


    public DeleteController(GuiController guiController, Stage stage, User user) {
        this.guiController = guiController;
        this.user = user;
        this.stage = stage;
    }

    @FXML
    private Label label;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnDelete;


    private void delete() {
        String result = "";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        try{
            result = Dao.removeUser(user);
            stage.hide();
            guiController.updateUserList();
            guiController.updateTrackingAndTelephonyPhones();
            guiController.updateLogs();
//            guiController.hideSiteTableAndButtons();
        }catch (Exception e){
            e.printStackTrace();
            result = "Ошибка: " + e.getMessage();
            alert.setAlertType(Alert.AlertType.ERROR);
        }
        alert.setTitle("Результат");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
        guiController.updateStatus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.setText("Внимание!\nПользователь " + user.getLogin() + "\nбудет удалён!");
        btnDelete.setOnAction(event ->  delete());
        btnCancel.setOnAction(event ->  cancel());
        btnCancel.setFocusTraversable(true);
    }

    private void cancel(){
        stage.hide();
    }
}
