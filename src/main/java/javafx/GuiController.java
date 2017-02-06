package javafx;


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
import model.Phone;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class GuiController implements Initializable {

    ObservableList<Phone> phones;
    ObservableList<String> sites;
    ObservableList<String> logs = FXCollections.observableArrayList();

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

    public static String selectedSiteString;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logList.setItems(logs);
//        List<String> sitesNames = MainController.sites.stream().map(Site::getName).collect(Collectors.toList());
//        sites = FXCollections.observableArrayList(sitesNames);
        siteList.setItems(sites);
        siteList.setOnMouseClicked(event -> {
            String siteSelected = siteList.getSelectionModel().getSelectedItem();
            setPhones(siteSelected);
        });
        setPhones("e404");
        siteList.getSelectionModel().select(1);

        phoneNumber.setCellValueFactory(new PropertyValueFactory<>("number"));
        phoneGoogleId.setCellValueFactory(new PropertyValueFactory<>("googleId"));
        phoneTime.setCellValueFactory(new PropertyValueFactory<>("busyTime"));
        phoneIp.setCellValueFactory(new PropertyValueFactory<>("ip"));
    }

    public void setPhones(String sitename) {
//        Site site = MainController.getSiteByName(sitename);
//        phones = FXCollections.observableArrayList(site.getPhones());
//        phoneTable.setItems(phones);
//        selectedSiteString = sitename;
    }


    public void removeAndUpdateList(String siteToRemove) {
        sites.remove(siteToRemove);
        siteList.setItems(sites);
        siteList.getSelectionModel().select(0);
        setPhones(siteList.getSelectionModel().getSelectedItem());
    }

    public void addAndUpdateList(String siteToRemove) {
        sites.add(siteToRemove);
        siteList.setItems(sites);
        siteList.getSelectionModel().select(0);
        setPhones(siteList.getSelectionModel().getSelectedItem());
    }

    private static int logCounter = 0;

    private static volatile boolean allowToCut = true;

    public void appendLog(String message) {
        javafx.application.Platform.runLater(() -> logs.add(0,message));

        if (logs.size() > 100 && allowToCut){
            allowToCut= false;
            javafx.application.Platform.runLater(() -> {
                logs.remove(90,logs.size()-1);
                allowToCut = true;
            });
        }

//        try{
//        logCounter++;
//        if (logCounter > 100) {
////            logArea.setText("");
//            javafx.application.Platform.runLater(() -> logArea.setText(""));
//            logCounter = 0;
//        }
//        javafx.application.Platform.runLater(() -> logArea.appendText(message + "\n"));
////        logArea.appendText(message + "\n");
//        logArea.setScrollTop(Double.MAX_VALUE);
//
//
//        }catch (IndexOutOfBoundsException e){
//            MyLogger.log(ELSE, "СЛОВЛЕН IndexOutOfBoundsException В ЛОГЕ!!!!!!!!!!!!!!!");
//        }
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
        }
    }

    private void showSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../settings.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../dbdelete.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../newsite.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../newsite.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../filteredit.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../script.fxml"));
            Stage stage = new Stage();
//            loader.setController(new ScriptController(MainController.getSiteByName(selectedSiteString), stage));
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
