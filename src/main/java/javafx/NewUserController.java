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
    private TextField textPassword;

    @FXML
    private TextField textPhone;

    @FXML
    private TextField textFirstName;

    @FXML
    private TextField textLastName;

    @FXML
    private TextField textMiddleName;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnChangePassword;

    @FXML
    private Button btnRegister;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            if (user != null) {// если мы меняем пользователя
                btnSave.setText("Изменить");
                textName.setEditable(false);

                textName.setText(user.getLogin());
                textName.setEditable(false);
                textEmail.setText(user.getEmail());
                btnSave.setOnAction(e -> updateUser());

                textPhone.setText(user.getUserPhoneNumber());
                textFirstName.setText(user.getFirstName());
                textLastName.setText(user.getLastName());
                textMiddleName.setText(user.getMiddleName());

            } else {
                btnChangePassword.setDisable(true);
                btnSave.setOnAction(e -> saveNewUser());
                btnRegister.setOnAction(event -> registerNewUser());
            }
            btnCancel.setOnAction(e -> cancel());

            btnChangePassword.setOnAction(event -> changePassword());
        } catch (Exception e) {
            e.printStackTrace();
            cancel();
        }
    }

    private void cancel() {
        stage.hide();
    }

    private void changePassword() {
        String login = textName.getText().trim();
        String password = textPassword.getText();

        String result;
        try {
            result = Dao.changePassword(login, password);
            Dao.updateHashes();
            guiController.updateUserList();
            guiController.updateAllUserInfo();

        } catch (Exception e) {
            e.printStackTrace();
            result = "Ошибка: " + e.getMessage();
        }

        GuiController.showInformationAlert(result);
        guiController.updateStatus();
        try {
            Dao.updateHashes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUser() {
        String login = textName.getText().trim();
        String email = textEmail.getText();
        String phone = textPhone.getText();
        String firstName = textFirstName.getText();
        String lastName = textLastName.getText();
        String middleName = textMiddleName.getText();

        JsonUser newUser = new JsonUser();
        newUser.setLogin(login);
        newUser.setEmail(email);
        newUser.setFirstName(firstName);
        newUser.setUserPhoneNumber(phone);
        newUser.setLastName(lastName);
        newUser.setMiddleName(middleName);

        String result;
        try {
            result = Dao.editUser(newUser);
            Dao.updateHashes();
            guiController.updateUserList();
            guiController.updateAllUserInfo();

        } catch (Exception e) {
            e.printStackTrace();
            result = "Ошибка: " + e.getMessage();
        }

        GuiController.showInformationAlert(result);
        guiController.updateStatus();
        try {
            Dao.updateHashes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveNewUser() {
        String login = textName.getText().trim();
        String email = textEmail.getText();
        String password = textPassword.getText();

        JsonUser newUser = new JsonUser();
        newUser.setLogin(login);
        newUser.setEmail(email);
        newUser.setPassword(password);

        String result;
        try {
            result = Dao.createNewUser(newUser);
            Dao.updateHashes();
            guiController.updateUserList();
            guiController.updateAllUserInfo();

        } catch (Exception e) {
            e.printStackTrace();
            result = "Ошибка: " + e.getMessage();
        }
        GuiController.showInformationAlert(result);
        guiController.updateStatus();
        try {
            Dao.updateHashes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerNewUser() {
        String login = textName.getText().trim();
        String email = textEmail.getText();
        String password = textPassword.getText();
        String phone = textPhone.getText();

        try {
            String result = Dao.registerNewUser(login, password, email, phone);
            GuiController.showInformationAlert(result);

        } catch (Exception e) {
            e.printStackTrace();
            GuiController.showErrorAlert(e);
        }
    }
}
