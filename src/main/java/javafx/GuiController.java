package javafx;


import dao.Dao;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;

import java.net.URL;
import java.util.*;

@SuppressWarnings("ALL")
public class GuiController implements Initializable {

    @FXML
    private ListView<String> userList;

    @FXML
    private TableView<Phone> phoneTable;

    @FXML
    private ListView<String> logList;

    @FXML
    private TableColumn<Phone, String> phoneNumber;

    @FXML
    private TableColumn<Phone, String> phoneGoogleId;

    @FXML
    private TableColumn<Phone, String> phoneTime;

    @FXML
    private TableColumn<Phone, String> phoneIp;

    @FXML
    private VBox buttonBox;

    @FXML
    private ListView<String> innerNumbers;

    @FXML
    private ListView<String> outerNumbers;

    @FXML
    private VBox buttonBoxTelephony;

    @FXML
    private HBox phonesBoxTelephony;

    @FXML
    private Label statusLabel;

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
    private CheckBox ONLY_ACTIVE_SITE;

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
    private TextField textUpdateRate;

    @FXML
    private TextField textCleanRate;

    @FXML
    private TextField textServerAdress;

    @FXML
    private TextField textAntiSpam;

    @FXML
    private Button btnSave;

    @FXML
    private Label statusTracking;

    @FXML
    private Label statusTelephony;

    public static User activeUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Dao.updateHashes();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        textServerAdress.setText(Dao.getSetting("SERVER_ADDRESS_FOR_SCRIPT"));
        textUpdateRate.setText(Dao.getSetting("SECONDS_TO_UPDATE_PHONE_ON_WEB_PAGE"));
        textCleanRate.setText(Dao.getSetting("SECONDS_TO_REMOVE_OLD_PHONES"));
        textAntiSpam.setText(Dao.getSetting("ELSE"));

        userList.setOnMouseClicked(event -> {
            updateSitePhones();
            String sitename = userList.getSelectionModel().getSelectedItem();
            if (sitename != null) {
                Gui.selectedSiteString = sitename;
            }
        });

        updateCustomers();
        updateLogs();
        updateSitePhones();
        updateStatus();

        phoneNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        phoneGoogleId.setCellValueFactory(new PropertyValueFactory<>("googleId"));
        phoneTime.setCellValueFactory(new PropertyValueFactory<>("busyTimeText"));
        phoneIp.setCellValueFactory(new PropertyValueFactory<>("ip"));

        Thread monitor = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(4000);
                    try {
                        updateLogs();
                        updateSitePhones();
                    } catch (Exception e) {
                        System.out.println("Нет связи скорее всего");
                    }
                } catch (InterruptedException ignored) {
                }
            }
        });
        monitor.setDaemon(true);
        monitor.start();
    }

    public void updateLogs() {
        ArrayList<String> list = Dao.getLogs();
        Platform.runLater(() -> logList.setItems(FXCollections.observableList(list)));
    }

    public void updateCustomers() {
        userList.setItems(FXCollections.observableArrayList(Dao.getListOfCustomers()));
    }

    public void updateSitePhones() {
        try {
            String selectedUser = userList.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                User user = Dao.getUserByName(selectedUser);
                activeUser = user;
                if (user.getTracking() != null) {
                    Tracking tracking = user.getTracking();
                    phoneTable.setItems(FXCollections.observableArrayList(tracking.getPhones()));
                    Platform.runLater(() -> {
                        statusTracking.setText("Подключено");
                        statusTracking.setStyle("-fx-background-color: chartreuse");
                    });
                } else {
                    Platform.runLater(() -> {
                        statusTracking.setText("Отключено");
                        statusTracking.setStyle("-fx-background-color: darkgray");
                        phoneTable.setItems(FXCollections.emptyObservableList());
                    });
                }
                if (user.getTelephony() != null) {
                    Telephony telephony = user.getTelephony();
                    Platform.runLater(() -> {
                        outerNumbers.setItems(FXCollections.observableList(telephony.getOuterPhonesList()));
                        innerNumbers.setItems(FXCollections.observableList(telephony.getInnerPhonesList()));
                        statusTelephony.setText("Подключено");
                        statusTelephony.setStyle("-fx-background-color: chartreuse");
                    });
                } else {
                    Platform.runLater(() -> {
                        statusTelephony.setText("Отключено");
                        statusTelephony.setStyle("-fx-background-color: darkgray");
                        outerNumbers.setItems(FXCollections.emptyObservableList());
                        innerNumbers.setItems(FXCollections.emptyObservableList());
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showAddUser() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("newuser.fxml"));
        Stage stage = new Stage();
        loader.setController(new NewUserController(this, stage, userList.getSelectionModel().getSelectedItem()));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Добавление пользователя");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
        stage.initOwner(userList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }


    public void connectTracking() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addTracking.fxml"));
        Stage stage = new Stage();
        loader.setController(new AddTrackingController(this, stage, activeUser));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Трекинг");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
        stage.initOwner(userList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }

    public void connectTelephony() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addTelephony.fxml"));
        Stage stage = new Stage();
        loader.setController(new AddTelephonyController(this, stage, activeUser));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Телефония");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
        stage.initOwner(userList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }


    public void showDeleteUser() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dbdelete.fxml"));
        Stage stage = new Stage();
        loader.setController(new DeleteController(this, stage, activeUser));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Телефония");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
        stage.initOwner(userList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }


    public void showHistory() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("history.fxml"));
        Stage stage = new Stage();
        loader.setController(new HistoryController(this, stage, activeUser));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Телефония");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
        stage.initOwner(userList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }


    public void showRules() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("rules2.fxml"));
        Stage stage = new Stage();
        loader.setController(new RulesController(this, stage, activeUser));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Редактор правил");
        stage.initOwner(userList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }

    public void showScript() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("script.fxml"));
        Stage stage = new Stage();
        loader.setController(new ScriptController(activeUser, stage));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Генератор скрипта");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
        stage.initOwner(userList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }

    public void showSipPass() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("passwords.fxml"));
        Stage stage = new Stage();
        loader.setController(new PasswordsController(this, stage, activeUser));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Пароли");
        stage.setResizable(false);
        stage.initOwner(userList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }

    public void checkBoxPressed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (!(source instanceof CheckBox)) {
            return;
        }

        CheckBox clickedCheckBox = (CheckBox) source;

        String id = clickedCheckBox.getId();
        boolean state = clickedCheckBox.isSelected();

        Dao.setSetting(id, "" + state);
    }

    public void save() {
        String serverAdress = textServerAdress.getText();
        String updateRate = textUpdateRate.getText();
        String cleanRate = textCleanRate.getText();
        String antispam = textAntiSpam.getText();

        Dao.setSetting("SERVER_ADDRESS_FOR_SCRIPT", serverAdress);
        Dao.setSetting("SECONDS_TO_UPDATE_PHONE_ON_WEB_PAGE", updateRate);
        Dao.setSetting("SECONDS_TO_REMOVE_OLD_PHONES", cleanRate);
        Dao.setSetting("MAIL_ANTISPAM", antispam);
    }

    public void updateStatus() {
        try{
            JsonNumbersCount free = Dao.getNumbersCount();
            List<String> customers = Dao.getListOfCustomers();
            int users = customers.size();
            int freeOuter = free.freeOuter;
            int busyOuter = free.busyOuter;
            int busyInner = free.busyInner;
            String status = String.format("Пользователей: %d | Свободных внешних номеров : %d | " +
                    "занято внешних: %d, внутренних: %d",
                    users, freeOuter, busyOuter, busyInner);
            statusLabel.setText(status);
        }catch (Exception e){
            statusLabel.setText("Ошибка. Возможно нет соединения");
        }
    }

    public void exit() {
        Platform.exit();
    }
}
