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
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;

import java.net.URL;
import java.util.*;

@SuppressWarnings("ALL")
public class GuiController implements Initializable {

    public static User activeUser;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        try {
            Dao.updateHashes();
        } catch (Exception e) {
            e.printStackTrace();
            exit();
        }

//        init
        initUserList();
        initTrackingTab();
        initSettingsTab();

//        update
        updateUserList();
        updateLogs();
        updateTrackingAndTelephonyPhones();
        updateStatus();
        updateAmo();

        Thread monitor = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(4000);
                    try {
                        updateLogs();
                        updateTrackingAndTelephonyPhones();
                        updateBlackList();
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


    /**
     * =================================== MENU BAR =======================================
     */
    public void showEditUser() throws Exception {
        showAddOrEditUser(activeUser);
    }

    public void showAddUser() throws Exception {
        showAddOrEditUser(null);
    }

    public void showAddOrEditUser(User user) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("newuser.fxml"));
        Stage stage = new Stage();
        loader.setController(new NewUserController(this, stage, user));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        if (user != null) {
            stage.setTitle("Редактирование пользователя");
        } else {
            stage.setTitle("Добавление пользователя");
        }
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

    /**
     * =================================== USER LIST =======================================
     */
    @FXML
    private ListView<String> userList;

    private void initUserList(){
        userList.setOnMouseClicked(event -> {
            updateTrackingAndTelephonyPhones();
            String sitename = userList.getSelectionModel().getSelectedItem();
            if (sitename != null) {
                Gui.selectedSiteString = sitename;
            }
        });
    }

    public void updateUserList() {
        userList.setItems(FXCollections.observableArrayList(Dao.getListOfCustomers()));
    }


    /**
     * =================================== TRACKING TAB =======================================
     */

    @FXML
    private TableView<Phone> phoneTable;

    @FXML
    private TableColumn<Phone, String> phoneNumber;

    @FXML
    private TableColumn<Phone, String> phoneGoogleId;

    @FXML
    private TableColumn<Phone, String> phoneTime;

    @FXML
    private TableColumn<Phone, String> phoneIp;

    @FXML
    private Label statusTracking;

    @FXML
    private ListView<String> blackList;

    @FXML
    private TextField blackIPText;

    private void initTrackingTab(){
        phoneTable.setOnMouseClicked(event -> {
            Phone selectedPhone = phoneTable.getSelectionModel().getSelectedItem();
            if (selectedPhone != null){
                String ip = selectedPhone.getIp();
                blackIPText.setText(ip);
            }
        });
        phoneNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        phoneGoogleId.setCellValueFactory(new PropertyValueFactory<>("googleId"));
        phoneTime.setCellValueFactory(new PropertyValueFactory<>("busyTimeText"));
        phoneIp.setCellValueFactory(new PropertyValueFactory<>("ip"));
    }

    public void updateBlackList(){
        ArrayList<String> list = Dao.getBlacklist(activeUser);
        Platform.runLater(() -> {
            blackList.setItems(FXCollections.observableList(list));
        });
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

    public void changeTrackingCount() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("setTrackingNumberCount.fxml"));
        Stage stage = new Stage();
        loader.setController(new TrackingNumberCountController(this, stage, activeUser));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Изменение количества номеров");
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


    public void addToBlackList(){
        Dao.addIpToBlackList(blackIPText.getText(),activeUser);
        blackIPText.setText("");
    }

    public void removeFromBlackList(){
        Dao.removeIpFromBlackList(blackList.getSelectionModel().getSelectedItem().trim(), activeUser);
    }

    public void updateTrackingAndTelephonyPhones() {
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

    /**
     * =================================== TELEPHONY TAB =======================================
     */

    @FXML
    private Label statusTelephony;

    @FXML
    private ListView<String> innerNumbers;

    @FXML
    private ListView<String> outerNumbers;


    /**
     * Метод ShowHistory, showRules, updateTrackingAndTelephonyPhones, общий с трекингом
     */

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

    public void changeTelephonyCount() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("setTelephonyNumberCount.fxml"));
        Stage stage = new Stage();
        loader.setController(new TelephonyNumberCountController(this, stage, activeUser));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Изменение количества номеров");
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



    /**
     * =================================== SETTINGS TAB =======================================
     */

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
    private CheckBox SENDING_ANALYTICS;

    @FXML
    private CheckBox MAIL_SENDING_LOG;

    @FXML
    private CheckBox MAIL_SENDING_ERRORS;

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

    private void initSettingsTab(){
        try {
            Dao.loadSettings();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ONLY_ACTIVE_SITE.setSelected(Gui.onlyActiveSite);
        REQUEST_NUMBER.setSelected(Boolean.parseBoolean(Dao.settings.get("REQUEST_NUMBER"))); // запрос номера
        BLOCKED_BY_IP.setSelected(Boolean.parseBoolean(Dao.settings.get("BLOCKED_BY_IP"))); // выдача по ip
        REPEATED_REQUEST.setSelected(Boolean.parseBoolean(Dao.settings.get("REPEATED_REQUEST"))); // выдача по google id
        SENDING_NUMBER.setSelected(Boolean.parseBoolean(Dao.settings.get("SENDING_NUMBER"))); // выдача номера
        NUMBER_FREE.setSelected(Boolean.parseBoolean(Dao.settings.get("NUMBER_FREE"))); // освобождение номера
        NO_NUMBERS_LEFT.setSelected(Boolean.parseBoolean(Dao.settings.get("NO_NUMBERS_LEFT"))); // нет свободных номеров
        ENDED_CALL.setSelected(Boolean.parseBoolean(Dao.settings.get("ENDED_CALL"))); // Закончен разговор
        SENDING_ANALYTICS.setSelected(Boolean.parseBoolean(Dao.settings.get("SENDING_ANALYTICS"))); // отправка аналитики
        MAIL_SENDING_LOG.setSelected(Boolean.parseBoolean(Dao.settings.get("MAIL_SENDING_LOG"))); // отправка писем
        MAIL_SENDING_ERRORS.setSelected(Boolean.parseBoolean(Dao.settings.get("MAIL_SENDING_ERRORS"))); // ошибки отправки писем

        textUpdateRate.setText(Dao.settings.get("SECONDS_TO_UPDATE_PHONE_ON_WEB_PAGE"));
        textCleanRate.setText(Dao.settings.get("SECONDS_TO_REMOVE_OLD_PHONES"));
        textAntiSpam.setText(Dao.settings.get("MAIL_ANTISPAM"));

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
        String updateRate = textUpdateRate.getText();
        String cleanRate = textCleanRate.getText();
        String antispam = textAntiSpam.getText();

        Dao.setSetting("SECONDS_TO_UPDATE_PHONE_ON_WEB_PAGE", updateRate);
        Dao.setSetting("SECONDS_TO_REMOVE_OLD_PHONES", cleanRate);
        Dao.setSetting("MAIL_ANTISPAM", antispam);
    }



    /**
     * =================================== AMOCRM TAB =======================================
     */


    @FXML
    private TextField textAmoDomain;

    @FXML
    private TextField textAmoAccount;

    @FXML
    private TextField textAmoApiKey;

    private void updateAmo(){

    }

    public void gotoAmoApiPage(ActionEvent actionEvent) {
        Gui.hostServices.showDocument("https://adeptiustest.amocrm.ru/settings/dev/");
    }

    public void onAmoSaveButton() throws Exception {

    }


    public void onAmoTestButton() throws Exception {

    }







   /**
     * =================================== LOGS =======================================
     */

   @FXML
   private ListView<String> logList;

   public void updateLogs() {
       ArrayList<String> list = Dao.getLogs();
       Platform.runLater(() -> logList.setItems(FXCollections.observableList(list)));
   }



    /**
     * =================================== STATUS BAR =======================================
     */
    @FXML
    private Label statusLabel;

    public void updateStatus() {
        try {
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
        } catch (Exception e) {
            statusLabel.setText("Ошибка. Возможно нет соединения");
        }
    }








    public void exit() {
        Platform.exit();
    }
}
