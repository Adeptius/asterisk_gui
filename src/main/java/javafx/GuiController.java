package javafx;


import dao.Dao;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GuiController implements Initializable {

    @FXML
    private ListView<String> siteList;

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
    private ListView<String> telephonyList;

    @FXML
    private ListView<String> innerNumbers;

    @FXML
    private ListView<String> outerNumbers;

    @FXML
    private VBox buttonBoxTelephony;

    @FXML
    private HBox phonesBoxTelephony;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        telephonyList.setOnMouseClicked(event -> {
            String telephon = telephonyList.getSelectionModel().getSelectedItem();
            if (telephon != null) {
                TelephonyCustomer customer = Dao.getTelephonyCustomerByName(telephon);
                innerNumbers.setItems(FXCollections.observableArrayList(customer.getInnerPhones()));
                outerNumbers.setItems(FXCollections.observableArrayList(customer.getOuterPhones()));
                showTelephonyTableAndButtons();
            }
        });

        siteList.setOnMouseClicked(event -> {
            updatePhones();
            String sitename = siteList.getSelectionModel().getSelectedItem();
            if (sitename != null) {
                Gui.selectedSiteString = sitename;
                showSiteTableAndButtons();
            }
        });

        updateCustomers();
        updateLogs();
        updatePhones();
        hideSiteTableAndButtons();
        hideTelephonyTableAndButtons();

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
                        updatePhones();
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

    public void hideSiteTableAndButtons() {
        buttonBox.setVisible(false);
        phoneTable.setVisible(false);
    }

    public void showSiteTableAndButtons() {
        buttonBox.setVisible(true);
        phoneTable.setVisible(true);
    }

    public void hideTelephonyTableAndButtons() {
        buttonBoxTelephony.setVisible(false);
        phonesBoxTelephony.setVisible(false);
    }

    public void showTelephonyTableAndButtons() {
        buttonBoxTelephony.setVisible(true);
        phonesBoxTelephony.setVisible(true);
    }


    public void showAddTelephony() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("newtelephony.fxml"));
        Stage stage = new Stage();
        loader.setController(new NewTelephonyController(this, stage, null));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Добавление телефонии");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
        stage.initOwner(siteList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }

    public void showEditTelephony() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("newtelephony.fxml"));
        Stage stage = new Stage();
        loader.setController(new NewTelephonyController(this, stage, telephonyList.getSelectionModel().getSelectedItem()));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Изменение телефонии");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
        stage.initOwner(siteList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }



    public void updateCustomers() {
        ArrayList<CustomerGroup> groups = Dao.getListOfCustomers();
        List<String> sitesNames = groups.stream()
                .filter(cg -> cg.type == CustomerType.TRACKING)
                .map(cg -> cg.name).collect(Collectors.toList());
        siteList.setItems(FXCollections.observableArrayList(sitesNames));

        List<String> trackingNames = groups.stream()
                .filter(cg -> cg.type == CustomerType.TELEPHONY)
                .map(cg -> cg.name).collect(Collectors.toList());
        telephonyList.setItems(FXCollections.observableArrayList(trackingNames));
    }

    public void updateLogs() {
        ArrayList<String> list = Dao.getLogs();
        Platform.runLater(() -> logList.setItems(FXCollections.observableList(list)));
    }

    public void updatePhones() {
        String sitename = siteList.getSelectionModel().getSelectedItem();
        if (sitename != null) {
            Site site = Dao.getSiteByName(sitename);
            phoneTable.setItems(FXCollections.observableArrayList(site.getPhones()));
        }
    }

    public void showHistorySite() throws Exception {
        showHistory(siteList.getSelectionModel().getSelectedItem());
    }

    public void showHistoryTelephony() throws Exception {
        showHistory(telephonyList.getSelectionModel().getSelectedItem());
    }

    public void showHistory(String customer) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("history.fxml"));
        Stage stage = new Stage();
        loader.setController(new HistoryController(customer, stage));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("История звонков");
        stage.initOwner(siteList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }

    public void showRulesTelephony() throws Exception {
        TelephonyCustomer customer = Dao.getTelephonyCustomerByName(telephonyList.getSelectionModel().getSelectedItem());
        showRules(customer);
    }

    public void showRulesSite() throws Exception {
        Site site = Dao.getSiteByName(siteList.getSelectionModel().getSelectedItem());
        showRules(site);
    }

    public void showRules(Customer customer) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("rules2.fxml"));
        Stage stage = new Stage();
        loader.setController(new RulesController(this, stage, customer));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Редактор правил");
        stage.initOwner(siteList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }

    public void showSettings() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("settings.fxml"));
        Stage stage = new Stage();
        loader.setController(new SettingsController(stage));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Настройки");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
        stage.initOwner(siteList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }

    public void showDeleteTelephony() throws Exception {
        showDelete(telephonyList.getSelectionModel().getSelectedItem());
    }

    public void showDeleteSite() throws Exception{
        showDelete(siteList.getSelectionModel().getSelectedItem());
    }

    public void showDelete(String customer) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("dbdelete.fxml"));
        Stage stage = new Stage();
        loader.setController(new DeleteController(this, stage, customer));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Удаление пользователя");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
        stage.initOwner(siteList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }

    public void showAddSite() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("newsite.fxml"));
        Stage stage = new Stage();
        loader.setController(new NewSiteController(this, stage, null));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Добавление сайта");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
        stage.initOwner(siteList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }


    public void showEdit() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("newsite.fxml"));
        Stage stage = new Stage();
        loader.setController(new NewSiteController(this, stage, siteList.getSelectionModel().getSelectedItem()));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Изменение сайта");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
        stage.initOwner(siteList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }


    public void showFilters() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("filteredit.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Настройка фильтров");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
        stage.initOwner(siteList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }

    public void showScript() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("script.fxml"));
        Stage stage = new Stage();
        loader.setController(new ScriptController(siteList.getSelectionModel().getSelectedItem(), stage));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Генератор скрипта");
        stage.setResizable(false);
        stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
        stage.initOwner(siteList.getScene().getWindow()); // Указание кого оно перекрывает
        stage.setScene(scene);
        stage.show();
    }

    public void exit() {
        Platform.exit();
    }
}
