package javafx;


import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dao.Dao;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import json.*;
import model.*;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;

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
        initTelephonyTab();
        initSettingsTab();

//        update
        updateUserList();
        updateStatus();

        Thread monitor = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(4000);
                    try {
                        String selectedUser = userList.getSelectionModel().getSelectedItem();
                        if (selectedUser != null) {
                            User user = Dao.getUser(selectedUser);
                            activeUser = user;
                            Gui.selectedSiteString = selectedUser;
                            updateTelephony(user);
                            updateTracking(user);
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

    public void recoverPassword() {
        try {
            showInformationAlert(Dao.recoverPassword(activeUser.getLogin()));
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert(e);
        }
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

//    @FXML
//    private TextField blackIPText;

    @FXML
    private Button phonesBindButton;

    public static String choicedSitename;

    private void initTrackingTab() {

        phoneNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        phoneGoogleId.setCellValueFactory(new PropertyValueFactory<>("googleId"));
        phoneIp.setCellValueFactory(new PropertyValueFactory<>("ip"));
        phoneTime.setCellValueFactory(new PropertyValueFactory<>("busyTimeText"));
        phoneUtm.setCellValueFactory(new PropertyValueFactory<>("utmRequest"));
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
        stage.setResizable(true);
        stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
        stage.initOwner(userList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }


    public void updateTracking(User user) {
        try {
            List<JsonSite> sites = Dao.getSites(user);
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
                    JsonSite siteByName = sites.stream().filter(site -> site.getName().equals(choicedSitename)).findFirst().get();

                    phones = Dao.getOuterPhones(user, choicedSitename);

                    if (siteByName == null) {
                        return;
                    }
//                    siteByName.setUser(user);
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
    private ListView<String> melodiesList;

    @FXML
    private Button scenarioAddButton;

    @FXML
    private Button scenarioEditButton;

    @FXML
    private Button scenarioDeleteButton;

    @FXML
    private Button scenarioBindButton;

    @FXML
    private Button melodyAddButton;

    @FXML
    private Button melodyListenButton;

    @FXML
    private Button melodyDeleteButton;


    private void initTelephonyTab() {
        scenarioEditButton.setOnAction(event -> {
            String selectedSite = scenariosList.getSelectionModel().getSelectedItem();
            if (selectedSite != null) {
                try {
                    showScenario(selectedSite);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        scenarioAddButton.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Добавление сценария");
            dialog.setHeaderText(null);
            dialog.setContentText("Название сценария");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                try {
                    showScenario(name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        scenarioDeleteButton.setOnAction(event -> {
            try {
                String selectedScenario = scenariosList.getSelectionModel().getSelectedItem();
                List<Scenario> scenarios = Dao.getScenarios(activeUser);
                Scenario scenario = scenarios.stream().filter(sc -> sc.getName().equals(selectedScenario)).findFirst().orElse(null);
                if (scenario != null) {
                    int id = scenario.getId();
                    String s = Dao.removeScenario(activeUser, id);
                    showInformationAlert(s);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showErrorAlert(e.toString());
            }
        });

        scenarioBindButton.setOnAction(event -> {
            try {
                showScenarioBindings();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        melodyAddButton.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Добавление мелодии");
            dialog.setHeaderText(null);
            dialog.setContentText("Название мелодии или сообщения");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                try {
                    Stage stage = new Stage();
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Open Resource File");
                    String filePath = fileChooser.showOpenDialog(stage).getAbsolutePath();
                    String response = Dao.sendMelodyFile(filePath, name, activeUser);
                    showInformationAlert(response);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });

        melodyListenButton.setOnAction(event -> {
            try {
                String selectedItem = melodiesList.getSelectionModel().getSelectedItem();
                List<JsonUserAudio> userMelodies = Dao.getUserAudio(activeUser);
                if (selectedItem == null) {
                    return;
                }
                Optional<JsonUserAudio> first = userMelodies.stream().filter(melody -> melody.getName().equals(selectedItem)).findFirst();
                if (first.isPresent()) {
                    int melodyId = first.get().getId();
                    System.out.println(melodyId);
                    String url = Dao.IP + "/audio/getFile/" + activeUser.getLogin() + "/" + melodyId;
                    Desktop.getDesktop().browse(new URI(url));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        melodyDeleteButton.setOnAction(event -> {
            try {
                String selectedItem = melodiesList.getSelectionModel().getSelectedItem();
                List<JsonUserAudio> userMelodies = Dao.getUserAudio(activeUser);
                if (selectedItem == null) {
                    return;
                }
                Optional<JsonUserAudio> first = userMelodies.stream().filter(melody -> melody.getName().equals(selectedItem)).findFirst();
                if (first.isPresent()) {
                    int melodyId = first.get().getId();
                    String result = Dao.removeAudio(melodyId, activeUser);
                    showInformationAlert(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }


    private void updateTelephony(User user) {
        try {
            JsonInnerAndOuterPhones innerAndOuterPhones = Dao.getInnerAndOuterPhones(user);
            List<Scenario> scenarios = Dao.getScenarios(user);
            List<JsonUserAudio> userMelodies = Dao.getUserAudio(user);

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

                melodiesList.setItems(FXCollections.observableList(
                        userMelodies.stream()
                                .map(JsonUserAudio::getName)
                                .collect(Collectors.toList())
                ));
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

    public void showScenario(String scenario) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("scenarios.fxml"));
        Stage stage = new Stage();
        loader.setController(new ScenarioController(this, stage, activeUser, scenario));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Редактирование сценария: " + scenario);
        stage.initOwner(userList.getScene().getWindow());
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

    public void showScenarioBindings() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("phonesAndScenarios.fxml"));
        Stage stage = new Stage();
        loader.setController(new PhonesAndScenariosController(this, stage, activeUser));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Редактирование привязок сценариев");
        stage.initOwner(userList.getScene().getWindow());
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

    @FXML
    private ToggleButton clingToggleButton;

    @FXML
    private VBox vBoxWithResponsibles;

    @FXML
    private ChoiceBox<String> cbPipeline;

    @FXML
    private ChoiceBox<String> cbStage;

    @FXML
    private VBox amoWorkersVBox;

    private void updateAmo(User user) {
        try {

            JsonAmoForController jsonAmo;
            String response = "";
            jsonAmo = Dao.getAmoAccount(user);


            if (jsonAmo == null) {
                textAmoDomain.setText("");
                textAmoAccount.setText("");
                textAmoApiKey.setText("");
                clingToggleButton.setSelected(false);
                return;
            }
            textAmoDomain.setText(jsonAmo.getDomain());
            textAmoAccount.setText(jsonAmo.getAmoLogin());
            textAmoApiKey.setText(jsonAmo.getApiKey());
            clingToggleButton.setSelected(jsonAmo.isCling());
            if (jsonAmo.isCling()) {
                clingToggleButton.setText("Включено");
            } else {
                clingToggleButton.setText("Отключено");
            }
            clingToggleButton.setOnAction(event -> {
                if (clingToggleButton.isSelected()) {
                    clingToggleButton.setText("Включено");
                } else {
                    clingToggleButton.setText("Отключено");
                }
            });


            HashMap<String, String> amoUsers = jsonAmo.getUsersIdAndName();
            ObservableList<String> userNames = FXCollections.observableList(amoUsers.values().stream().collect(Collectors.toList()));
            userNames.add("");

            for (int i = 0; i < 7; i++) {
                ComboBox<String> choiceBox = (ComboBox<String>) vBoxWithResponsibles.lookup("#responsibleCB" + i);
                choiceBox.setItems(userNames);
                String[] responsibleUserSchedule = jsonAmo.getResponsibleUserSchedule();
                String userId = responsibleUserSchedule[i];
                if (!StringUtils.isBlank(userId)) {
                    choiceBox.setValue(amoUsers.get(userId));
                }
            }

            try {
                List<JsonPipeline> amoPipelines = jsonAmo.getPipelines();

                int pipelineId = jsonAmo.getPipelineId();
                int stageId = jsonAmo.getStageId();

                List<String> pipelineNames = amoPipelines.stream().map(JsonPipeline::getName).collect(Collectors.toList());
                ObservableList<String> fxPipelineNames = FXCollections.observableList(pipelineNames);
                cbPipeline.setItems(fxPipelineNames);
                String selectedPipeline = amoPipelines.stream()
                        .filter(jsonPipeline -> jsonPipeline.getId() == pipelineId)
                        .map(JsonPipeline::getName)
                        .findFirst().orElse("");
                cbPipeline.getSelectionModel().select(selectedPipeline);


                HashMap<Integer, String> statusesIdAndName = amoPipelines.stream()
                        .filter(jsonPipeline -> jsonPipeline.getName().equals(selectedPipeline))
                        .findFirst()
                        .get()
                        .getStatusesIdAndName();

                List<String> stageNames = statusesIdAndName.values().stream().collect(Collectors.toList());
                cbStage.setItems(FXCollections.observableList(stageNames));
                cbStage.getSelectionModel().select(statusesIdAndName.get(stageId));

                cbPipeline.setOnAction(event -> fillStageChoise(amoPipelines));

            } catch (Exception e) {
                e.printStackTrace();
            }

            HashMap<String, String> usersIdAndName = jsonAmo.getUsersIdAndName();
            HashMap<String, String> operatorLocation = jsonAmo.getOperatorLocation();
            HashMap<String, String> usersIdAndName1 = jsonAmo.getUsersIdAndName();

            updateOperatorLocations(jsonAmo);
        } catch (Exception e) {
            if (e.getMessage().contains("No amo acc")) {
                textAmoDomain.setText("");
                textAmoAccount.setText("");
                textAmoApiKey.setText("");
                clingToggleButton.setText("");

//                vBoxWithResponsibles.getChildren().forEach(node -> {
//                    vBoxWithResponsibles.getChildren().remove(node);
//                });
                cbPipeline.setItems(FXCollections.emptyObservableList());
                cbStage.setItems(FXCollections.emptyObservableList());
//                ObservableList<Node> children = amoWorkersVBox.getChildren();
//                for (Node child : children) {
//                    amoWorkersVBox.getChildren().remove(child);
//                }
            } else {
                e.printStackTrace();

            }
        }
    }

    private void updateOperatorLocations(JsonAmoForController amoJsonObj) throws Exception {
        if (amoWorkersVBox.getChildren().size() > 0) {
            amoWorkersVBox.getChildren().clear();
        }
        amoWorkersVBox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        HashMap<String, String> operatorLocation = amoJsonObj.getOperatorLocation();
        HashMap<String, String> amoUserIdAndNames = amoJsonObj.getUsersIdAndName();
        for (Map.Entry<String, String> entry : amoUserIdAndNames.entrySet()) {
            addWorkerLocationToScreenToScreen(entry.getValue(), operatorLocation.get(entry.getKey()));
        }
    }

    private void addWorkerLocationToScreenToScreen(String name, String phone) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("item_bind.fxml"));
        Parent root = fxmlLoader.load();
        AnchorPane mainNode = (AnchorPane) root.lookup("#mainNode");
        VBox rootNode = (VBox) root.lookup("#ruleHbox");
        rootNode.setStyle("-fx-border-color: #0072ff");
        rootNode.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        Label worker = (Label) rootNode.lookup("#workerName");
        worker.setText(name);

        TextField phoneField = (TextField) rootNode.lookup("#phone");
        phoneField.setText(phone);

        amoWorkersVBox.getChildren().add(mainNode);
    }

    private void fillStageChoise(List<JsonPipeline> amoPipelines) {
        String selectedPipeline = cbPipeline.getSelectionModel().getSelectedItem();
        if (StringUtils.isBlank(selectedPipeline)) {
            cbStage.setItems(FXCollections.emptyObservableList());
        } else {
            HashMap<Integer, String> statusesIdAndName = amoPipelines.stream()
                    .filter(jsonPipeline -> jsonPipeline.getName().equals(selectedPipeline))
                    .findFirst()
                    .get()
                    .getStatusesIdAndName();

            List<String> collect = statusesIdAndName.values().stream().collect(Collectors.toList());
            cbStage.setItems(FXCollections.observableList(collect));
        }
    }

    public void gotoAmoApiPage(ActionEvent actionEvent) {
        Gui.hostServices.showDocument("https://adeptiustest.amocrm.ru/settings/dev/");
    }

    public void onAmoSaveButton() throws Exception {
        JsonAmoForController jsonAmo = new JsonAmoForController();
        jsonAmo.setDomain(textAmoDomain.getText());
        jsonAmo.setAmoLogin(textAmoAccount.getText());
        jsonAmo.setApiKey(textAmoApiKey.getText());
        jsonAmo.setCling(clingToggleButton.isSelected());

        String[] responsibles = new String[7];

        JsonAmoForController currentAmoAcc = Dao.getAmoAccount(activeUser);
        HashMap<String, String> amoUsersIdAndNames = currentAmoAcc.getUsersIdAndName();


        for (int i = 0; i < 7; i++) {
            ComboBox<String> choiceBox = (ComboBox<String>) vBoxWithResponsibles.lookup("#responsibleCB" + i);
            String choised = choiceBox.getSelectionModel().getSelectedItem();
            if (!StringUtils.isBlank(choised)) {
                String userId = "";

                for (Map.Entry<String, String> entry : amoUsersIdAndNames.entrySet()) {
                    if (entry.getValue().equals(choised)) {
                        userId = entry.getKey();
                    }
                }
                responsibles[i] = userId;
            }
        }
        jsonAmo.setResponsibleUserSchedule(responsibles);

        // воронка и этап
        List<JsonPipeline> amoPipelines = currentAmoAcc.getPipelines();

        String selectedPipeline = cbPipeline.getSelectionModel().getSelectedItem();
        String selectedStage = cbStage.getSelectionModel().getSelectedItem();

        int pipelineId = 0;
        int stageId = 0;
        JsonPipeline jsonPipeline1 = amoPipelines.stream()
                .filter(jsonPipeline -> jsonPipeline.getName().equals(selectedPipeline))
                .findFirst()
                .orElse(null);
        if (jsonPipeline1 != null) {
            pipelineId = jsonPipeline1.getId();
            HashMap<Integer, String> statusesIdAndName = jsonPipeline1.getStatusesIdAndName();
            for (Map.Entry<Integer, String> entry : statusesIdAndName.entrySet()) {
                if (entry.getValue().equals(selectedStage)) {
                    stageId = entry.getKey();
                }
            }
        }
        jsonAmo.setPipelineId(pipelineId);
        jsonAmo.setStageId(stageId);

        ObservableList<Node> children = amoWorkersVBox.getChildren();

        HashMap<String, String> hashMapToSave = new HashMap<>();

        for (Node child : children) {
            Label workerField = (Label) child.lookup("#workerName");
            TextField phoneField = (TextField) child.lookup("#phone");

            String workerName = workerField.getText();
            String phone = phoneField.getText();
            if (StringUtils.isEmpty(phone)) {
                continue;
            }

            String userId = "";
            for (Map.Entry<String, String> idAndName : amoUsersIdAndNames.entrySet()) {
                if (idAndName.getValue().equals(workerName)) {
                    userId = idAndName.getKey();
                }
            }
            hashMapToSave.put(userId, phone);
        }

        jsonAmo.setOperatorLocation(hashMapToSave);

        String result = Dao.setAmoAccount(activeUser, jsonAmo);
        Alert alert = new Alert(INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
    }


    public void onAmoTestButton() throws Exception {
        String result = Dao.testAmoAccount(activeUser);
        Alert alert = new Alert(INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
    }


    public void onAmoRemoveButton() throws Exception {
        String result = Dao.removeAmoAccount(activeUser);
        Alert alert = new Alert(INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
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
        Alert alert = new Alert(INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
    }


    public void onRoistatTestButton() throws Exception {
        String result = Dao.testRoistatAccount(activeUser);
        Alert alert = new Alert(INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
    }


    public void onRoistatRemoveButton() throws Exception {
        String result = Dao.removeRoistatAccount(activeUser);
        Alert alert = new Alert(INFORMATION);
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

    public static void showInformationAlert(String message) {
        Alert alert = new Alert(INFORMATION);
        alert.setTitle("Результат");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showErrorAlert(String message) {
        Alert alert = new Alert(ERROR);
        alert.setTitle("Результат");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public static void showErrorAlert(Throwable e) {
        while (e.getCause() != null) {
            e = e.getCause();
        }

        StackTraceElement[] stackTrace = e.getStackTrace();
        String s = "";
        for (StackTraceElement stackTraceElement : stackTrace) {
            s += stackTraceElement + "\n";
        }

        Alert alert = new Alert(ERROR);
        alert.setResizable(true);
        alert.setTitle("Результат");
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }
}
