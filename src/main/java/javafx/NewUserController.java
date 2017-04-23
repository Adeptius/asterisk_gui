package javafx;


import dao.Dao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Site;
import model.User;
import utils.Validator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings("Duplicates")
public class NewUserController implements Initializable {

    private GuiController guiController;
    private Stage stage;
    private String userLogin;


    public NewUserController(GuiController guiController, Stage stage, String userLogin) {
        this.guiController = guiController;
        this.stage = stage;
        this.userLogin = userLogin;
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
//            if (userLogin != null) {// если мы меняем сайт
//                btnSave.setText("Изменить");
//                textName.setEditable(false);
//
//                User user = Dao.getUserByName(userLogin);
//                textName.setText(user.getLogin());
//                textEmail.setText(user.getEmail());
//                textGoogleId.setText(user.getTrackingId());
//                textPassword.setText(user.getPassword());
//            }
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
        if (!Validator.isValidLogin(login)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Логин должен состоять только из английских букв");
            alert.showAndWait();
            return;
        }

        String email = textEmail.getText();
        String googleId = textGoogleId.getText();
        String password = textPassword.getText();

        User user = new User();
        user.setLogin(login);
        user.setEmail(email);
        user.setPassword(password);
        user.setTrackingId(googleId);

        String result;
        try {
            result = Dao.createNewUser(user);
            stage.hide();
            guiController.updateCustomers();
            guiController.updateLogs();
            guiController.updateSitePhones();

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
