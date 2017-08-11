package javafx;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
import json.JsonAmoForController;
import json.JsonInnerAndOuterPhones;
import json.JsonRoistatForController;
import model.*;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class GuiController implements Initializable {

    public static User activeUser;

    public static boolean allRequersSeparated = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        updateStatus();

        Thread monitor = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(4000);
                    try {
//                        updateLogs();
                        String selectedUser = userList.getSelectionModel().getSelectedItem();
                        if (selectedUser != null) {
                            User user = Dao.getUser(selectedUser);
                            activeUser = user;
                            Gui.selectedSiteString = selectedUser;
                            updateTelephony(user);
                            updateTracking(user);
//                            updateAmo(user);
                            updateBlackList(user);
                        }
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

    public void updateAllUserInfo() throws Exception {
        String selectedUser = userList.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            choicedSitename = null;
            User user = Dao.getUser(selectedUser);
            activeUser = user;
            Gui.selectedSiteString = selectedUser;
            updateTelephony(user);
            updateTracking(user);
            updateAmo(user);
            updateBlackList(user);
        }
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

    private void initUserList() {
        userList.setOnMouseClicked(event -> {
            try {
                String selectedUser = userList.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    User user = Dao.getUser(selectedUser);
                    activeUser = user;
                    Gui.selectedSiteString = selectedUser;
                    updateTelephony(user);
                    updateTracking(user);
                    updateAmo(user);
                    updateRoistat(user);
                    updateBlackList(user);
                    choicedSitename = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
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
    private ListView<String> siteList;

    @FXML
    private TableView<OuterPhone> phoneTable;

    @FXML
    private TableColumn<Phone, String> phoneNumber;

    @FXML
    private TableColumn<Phone, String> phoneGoogleId;

    @FXML
    private TableColumn<Phone, String> phoneTime;

    @FXML
    private TableColumn<Phone, String> phoneIp;

    @FXML
    private TableColumn<Phone, String> phoneUtm;

    @FXML
    private ListView<String> blackList;

    @FXML
    private TextField blackIPText;

    public static String choicedSitename;

    private void initTrackingTab() {
        phoneTable.setOnMouseClicked(event -> {
            OuterPhone selectedPhone = phoneTable.getSelectionModel().getSelectedItem();
            if (selectedPhone != null) {
                String ip = selectedPhone.getIp();
                blackIPText.setText(ip);
            }
        });

        phoneNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        phoneGoogleId.setCellValueFactory(new PropertyValueFactory<>("googleId"));
        phoneIp.setCellValueFactory(new PropertyValueFactory<>("ip"));
        phoneTime.setCellValueFactory(new PropertyValueFactory<>("busyTimeText"));
        phoneUtm.setCellValueFactory(new PropertyValueFactory<>("utmRequest"));
    }

    public void updateBlackList(User user) {
        if (choicedSitename != null) {
            List<String> list = Dao.getBlackList(user, choicedSitename);

            Platform.runLater(() -> {
                blackList.setItems(FXCollections.observableList(list));
            });
        }else {
            Platform.runLater(() -> {
                blackList.setItems(FXCollections.emptyObservableList());
            });
        }
    }

    public void crudSite() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("crudSite.fxml"));
        Stage stage = new Stage();
        loader.setController(new SiteController(this, stage, activeUser, choicedSitename));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Управление сайтами");
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
        loader.setController(new ScriptController(activeUser, stage, choicedSitename));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Генератор скрипта");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
        stage.initOwner(userList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }


    public void addToBlackList() {
        Dao.addIpToBlackList(blackIPText.getText(), activeUser, choicedSitename);
        blackIPText.setText("");
    }

    public void removeFromBlackList() {
        Dao.removeIpFromBlackList(blackList.getSelectionModel().getSelectedItem().trim(), activeUser, choicedSitename);
    }

    public void updateTracking(User user) {
        try {
            List<Site> sites = Dao.getSites(user);
            if (sites != null) {
                Platform.runLater(() -> {
                    siteList.setItems(FXCollections.observableArrayList(
                            sites.stream()
                                    .map(site -> site.getName())
                                    .collect(Collectors.toList())
                    ));
                });
                MultipleSelectionModel<String> selectionModel = siteList.getSelectionModel();
                String selectedItem = selectionModel.getSelectedItem();
                if (selectedItem == null) {
                    Platform.runLater(() -> {
                        phoneTable.setItems(FXCollections.emptyObservableList());
                    });
                    return;// не выбран сайт
                }
                choicedSitename = selectedItem.toString();
                if (choicedSitename != null) {
                    List<OuterPhone> phones;
                    Site siteByName = sites.stream().filter(site -> site.getName().equals(choicedSitename)).findFirst().get();

                    phones = Dao.getOuterPhones(user, choicedSitename);

                    if (siteByName == null) {
                        return;
                    }
                    siteByName.setUser(user);
                    Platform.runLater(() -> {
                        phoneTable.setItems(FXCollections.observableArrayList(phones));
                    });
                }
            } else {
                Platform.runLater(() -> {
                    phoneTable.setItems(FXCollections.emptyObservableList());
                });
            }
        } catch (JsonSyntaxException ignored) {

            Platform.runLater(() -> {
                siteList.setItems(FXCollections.emptyObservableList());
                phoneTable.setItems(FXCollections.emptyObservableList());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * =================================== TELEPHONY TAB =======================================
     */

    @FXML
    private ListView<String> innerNumbers;

    @FXML
    private ListView<String> outerNumbers;

    @FXML
    private ListView<String> scenariosList;

    @FXML
    private Button scenarioAddButton;

    @FXML
    private Button scenarioEditButton;

    @FXML
    private Button scenarioDeleteButton;

    /**
     * Метод ShowHistory, showScenarios, updateTrackingAndTelephonyPhones, общий с трекингом
     */

    private void updateTelephony(User user) {
        try {
            JsonInnerAndOuterPhones innerAndOuterPhones = Dao.getInnerAndOuterPhones(user);
            List<Scenario> scenarios = Dao.getScenarios(user);


            Platform.runLater(() -> {
                outerNumbers.setItems(FXCollections.observableList(
                        innerAndOuterPhones.getOuterPhones().stream()
                        .map(OuterPhone::getNumber)
                        .sorted()
                        .collect(Collectors.toList())));

                innerNumbers.setItems(FXCollections.observableList(
                        innerAndOuterPhones.getInnerPhones().stream()
                        .map(InnerPhone::getNumber)
                        .sorted()
                        .collect(Collectors.toList())));

                scenariosList.setItems(FXCollections.observableList(
                        scenarios.stream()
                        .map(Scenario::getName)
                        .collect(Collectors.toList())));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void changeTelephonyCount() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("setPhonesCount.fxml"));
        Stage stage = new Stage();
        loader.setController(new PhonesCountController(this, stage, activeUser));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Изменение количества номеров");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
        stage.initOwner(userList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }

    public void showScenarios() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenarios.fxml"));
        Stage stage = new Stage();
        loader.setController(new ScenarioController(this, stage, activeUser));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Редактор сценариев");
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
    private TextField textUpdateRate;

    @FXML
    private TextField textCleanRate;

    @FXML
    private TextField textServerAdress;

    @FXML
    private TextField textAntiSpam;

    @FXML
    private Button btnSave;

    private void initSettingsTab() {
        try {
            Dao.loadSettings();
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    private void updateAmo(User user) {
        AmoAccount amoAccount;
            String response = "";
            try {
                response = Dao.getAmoAccount(user);
                amoAccount = new Gson().fromJson(response, AmoAccount.class);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

        if (amoAccount == null) {
            textAmoDomain.setText("");
            textAmoAccount.setText("");
            textAmoApiKey.setText("");
            return;
        }
        textAmoDomain.setText(amoAccount.getDomain());
        textAmoAccount.setText(amoAccount.getAmoLogin());
        textAmoApiKey.setText(amoAccount.getApiKey());
    }

    public void gotoAmoApiPage(ActionEvent actionEvent) {
        Gui.hostServices.showDocument("https://adeptiustest.amocrm.ru/settings/dev/");
    }

    public void onAmoSaveButton() throws Exception {
        JsonAmoForController jsonAmo = new JsonAmoForController();
        jsonAmo.setDomain(textAmoDomain.getText());
        jsonAmo.setAmoLogin(textAmoAccount.getText());
        jsonAmo.setApiKey(textAmoApiKey.getText());
        String result = Dao.setAmoAccount(activeUser, jsonAmo);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
    }


    public void onAmoTestButton() throws Exception {
        String result = Dao.testAmoAccount(activeUser);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
    }


    public void onAmoRemoveButton() throws Exception {
        String result = Dao.removeAmoAccount(activeUser);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
    }


    public void onAmoPhoneAndWorkersButton() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("phonesAndWorkers.fxml"));
        Stage stage = new Stage();
        loader.setController(new PhonesAndWorkersController(this, stage, activeUser));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Редактор привязок");
        stage.initOwner(userList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }


    /**
     * =================================== Roistat TAB =======================================
     */


    @FXML
    private TextField textRoistatProjectNumber;

    @FXML
    private TextField textRoistatApiKey;

    private void updateRoistat(User user) {
        RoistatAccount roistatAccount;
        try {
            roistatAccount = new Gson().fromJson(Dao.getRoistatAccount(user), RoistatAccount.class);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (roistatAccount == null) {
            textRoistatApiKey.setText("");
            textRoistatProjectNumber.setText("");
            return;
        }

        textRoistatProjectNumber.setText(roistatAccount.getProjectNumber());
        textRoistatApiKey.setText(roistatAccount.getApiKey());
    }

    public void gotoRoistatApiPage(ActionEvent actionEvent) {
        Gui.hostServices.showDocument("https://cloud.roistat.com/user/profile/api");
    }

    public void onRoistatSaveButton() throws Exception {
        JsonRoistatForController jsonRoistat = new JsonRoistatForController();
        jsonRoistat.setApiKey(textRoistatApiKey.getText());
        jsonRoistat.setProjectNumber(textRoistatProjectNumber.getText());
        String result = Dao.setRoistatAccount(activeUser, jsonRoistat);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
    }


    public void onRoistatTestButton() throws Exception {
        String result = Dao.testRoistatAccount(activeUser);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
    }


    public void onRoistatRemoveButton() throws Exception {
        String result = Dao.removeRoistatAccount(activeUser);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
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
