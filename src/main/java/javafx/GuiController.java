package javafx;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.Dao;
import model.Phone;
import model.Site;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class GuiController implements Initializable {
//
//    ObservableList<Phone> phones;
//    ObservableList<String> sites;
//    ObservableList<String> logs = FXCollections.observableArrayList();

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
    private Button btnSettings;

    @FXML
    private Button btnHistory;

    public static String selectedSiteString;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        siteList.setOnMouseClicked(event -> {
            updatePhones();
            String sitename = siteList.getSelectionModel().getSelectedItem();
            if (sitename != null) {
                Dao.setSetting("ACTIVE_SITE", sitename);
            }
        });
        updateSites();
        updateLogs();
        updatePhones();

        phoneNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        phoneGoogleId.setCellValueFactory(new PropertyValueFactory<>("googleId"));
        phoneTime.setCellValueFactory(new PropertyValueFactory<>("busyTimeText"));
        phoneIp.setCellValueFactory(new PropertyValueFactory<>("ip"));

        new Thread(() -> {
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
        }).start();
    }


    public void updateSites() {
        List<String> sitesNames = Dao.getListOfSites();
        siteList.setItems(FXCollections.observableArrayList(sitesNames));
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
            selectedSiteString = sitename;
        }
    }

    public void actionButtonPressed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();
        if (!(source instanceof Button)) {
            return;
        }
        Button clickedButton = (Button) source;
        Window parentWindow = ((Node) actionEvent.getSource()).getScene().getWindow();
        if (clickedButton.getId().equals("btnFilter")) {
            showFilters();
        } else if (clickedButton.getId().equals("btnDelete")) {
            showDelete();
        } else if (clickedButton.getId().equals("btnAdd")) {
            showAdd();
        } else if (clickedButton.getId().equals("btnEdit")) {
            showEdit();
        }else if (clickedButton.getId().equals("btnScript")) {
            showScript();
        }else if (clickedButton.getId().equals("btnSettings")) {
            showSettings();
        }else if (clickedButton.getId().equals("btnHistory")) {
            showHistory();
        }
    }

    private void showHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("history.fxml"));
            Stage stage = new Stage();
            loader.setController(new HistoryController(selectedSiteString, stage));
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

    private void showSettings() {
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

    private void showDelete() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dbdelete.fxml"));
            Stage stage = new Stage();
            loader.setController(new DeleteController(this, stage, selectedSiteString));
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

    private void showAdd() {
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


    private void showEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("newsite.fxml"));
            Stage stage = new Stage();
            loader.setController(new NewSiteController(this, stage, selectedSiteString));
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


    private void showFilters() {
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

    private void showScript() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("script.fxml"));
            Stage stage = new Stage();
            loader.setController(new ScriptController(selectedSiteString, stage));
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



}
