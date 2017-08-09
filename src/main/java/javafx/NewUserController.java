package javafx;


import dao.Dao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import json.JsonUser;
import model.User;

import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("Duplicates")
public class NewUserController implements Initializable {

    private GuiController guiController;
    private Stage stage;
    private User user;


    public NewUserController(GuiController guiController, Stage stage, User user) {
        this.guiController = guiController;
        this.stage = stage;
        this.user = user;
    }

    @FXML
    private TextField textName;

    @FXML
    private TextField textEmail;

    @FXML
    private TextField textGoogleId;

    @FXML
    private TextField textPassword;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            if (user != null) {// если мы меняем сайт
                btnSave.setText("Изменить");
                textName.setEditable(false);

                textName.setText(user.getLogin());
                textName.setEditable(false);
                textEmail.setText(user.getEmail());
                textGoogleId.setText(user.getTrackingId());
                textPassword.setText(user.getPassword());
            }
            btnCancel.setOnAction(e -> cancel());
            btnSave.setOnAction(e -> save());
        } catch (Exception e) {
            e.printStackTrace();
            cancel();
        }
    }

    private void cancel() {
        stage.hide();
    }

    private void save() {
        String login = textName.getText().trim();
//        if (!Validator.isValidLogin(login)) {
//            Alert alert = new Alert(Alert.AlertType.INFORMATION);
//            alert.setTitle("Ошибка");
//            alert.setHeaderText(null);
//            alert.setContentText("Логин должен состоять только из английских букв");
//            alert.showAndWait();
//            return;
//        }

        String email = textEmail.getText();
        String googleId = textGoogleId.getText();
        String password = textPassword.getText();

        JsonUser newUser = new JsonUser();
        newUser.setLogin(login);
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setTrackingId(googleId);

        String result;
        try {
            if (user != null){
                result = Dao.editUser(newUser);
            }else {
                result = Dao.createNewUser(newUser);
            }
            stage.hide();
            Dao.updateHashes();
            guiController.updateUserList();
            guiController.updateAllUserInfo();

        } catch (Exception e) {
            e.printStackTrace();
            result = "Ошибка: " + e.getMessage();
        }
        System.out.println(result);

        Alert alert = null;
        alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
        guiController.updateStatus();
        try {
            Dao.updateHashes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
