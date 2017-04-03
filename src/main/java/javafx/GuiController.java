package javafx;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        telephonyList.setOnMouseClicked(event -> {
            String telephon = telephonyList.getSelectionModel().getSelectedItem();
            TelephonyCustomer customer = Dao.getTelephonyCustomerByName(telephon);
            innerNumbers.setItems(FXCollections.observableArrayList(customer.getInnerPhones()));
            outerNumbers.setItems(FXCollections.observableArrayList(customer.getOuterPhones()));
        });

        siteList.setOnMouseClicked(event -> {
            updatePhones();
            String sitename = siteList.getSelectionModel().getSelectedItem();
            if (sitename != null) {
                Gui.selectedSiteString = sitename;
                showTableAndButtons();
            }
        });
        updateCustomers();
        updateLogs();
        updatePhones();
        hideTableAndButtons();

        phoneNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        phoneGoogleId.setCellValueFactory(new PropertyValueFactory<>("googleId"));
        phoneTime.setCellValueFactory(new PropertyValueFactory<>("busyTimeText"));
        phoneIp.setCellValueFactory(new PropertyValueFactory<>("ip"));

        Thread monitor = new Thread(() -> {
            while (true){
             try{
                 Thread.sleep(4000);
                 try{
                     updateLogs();
                     updatePhones();
                 }catch (Exception e){
                     System.out.println("Нет связи скорее всего");
                 }
             }catch (InterruptedException ignored){}
            }
        });
        monitor.setDaemon(true);
        monitor.start();
    }

    public void hideTableAndButtons(){
        buttonBox.setVisible(false);
        phoneTable.setVisible(false);
    }

    public void showTableAndButtons(){
        buttonBox.setVisible(true);
        phoneTable.setVisible(true);
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
        if (sitename != null){
            Site site = Dao.getSiteByName(sitename);
            phoneTable.setItems(FXCollections.observableArrayList(site.getPhones()));
//            Gui.selectedSiteString = sitename;
        }
    }

    public void showHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("history.fxml"));
            Stage stage = new Stage();
            loader.setController(new HistoryController(siteList.getSelectionModel().getSelectedItem(), stage));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("История звонков");
//            stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
            stage.initOwner(siteList.getScene().getWindow()); // Указание кого оно перекрывает
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showRules() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("rules2.fxml"));
            Stage stage = new Stage();
            loader.setController(new RulesController(this, stage, siteList.getSelectionModel().getSelectedItem()));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Редактор правил");
//            stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
            stage.initOwner(siteList.getScene().getWindow()); // Указание кого оно перекрывает
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSettings() {
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showDelete() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dbdelete.fxml"));
            Stage stage = new Stage();
            loader.setController(new DeleteController(this, stage, siteList.getSelectionModel().getSelectedItem()));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Удаление сайта");
            stage.setResizable(false);
            stage.initModality(Modality.WINDOW_MODAL); // Перекрывающее окно
            stage.initOwner(siteList.getScene().getWindow()); // Указание кого оно перекрывает
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAdd() {
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showEdit() {
        try {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showFilters() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showScript() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void exit(){
        Platform.exit();
    }
}
