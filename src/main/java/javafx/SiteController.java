package javafx;


import dao.Dao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import json.JsonSite;
import json.JsonTracking;
import model.Site;
import model.User;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SiteController implements Initializable {

    private GuiController guiController;
    private Stage stage;
    private User user;
    private String siteName;


    public SiteController(GuiController guiController, Stage stage, User user, String siteName) {
        this.guiController = guiController;
        this.stage = stage;
        this.user = user;
        this.siteName = siteName;
    }

    @FXML
    private Button btnCancel;

    @FXML
    private TextField textNumber;

    @FXML
    private TextField textName;

    @FXML
    private Button btnSave;

    @FXML
    private TextField textBlock;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnCancel.setOnAction(e -> cancel());
        Site siteByName = null;
        try{
            List<Site> sites = Dao.getSites(user);
            siteByName = sites.stream().filter(site -> site.getName().equals(siteName)).findFirst().get();
        }catch (Exception e){
        }

        if (siteByName != null) {
            btnSave.setText("Изменить");
            textName.setText(siteName);
            textName.setEditable(false);
            textNumber.setText(siteByName.getStandardNumber());
            textBlock.setText(String.valueOf(siteByName.getTimeToBlock()));
            Site finalSiteByName = siteByName;
            btnSave.setOnAction(e -> edit(finalSiteByName));
        } else {
            btnSave.setText("Добавить");
            btnSave.setOnAction(e -> createNew());
            textBlock.setText("120");
        }
    }

    private void cancel() {
        stage.hide();
    }

    public void remove() {
        String result;
        try {
            result = Dao.removeSite(user, siteName);
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
        guiController.updateStatus();
        stage.hide();
    }

    private void edit(Site site) {
        try {
            JsonSite jsonSite = new JsonSite();
            jsonSite.setName(siteName);
            jsonSite.setStandartNumber(textNumber.getText());
            jsonSite.setTimeToBlock(Integer.parseInt(textBlock.getText()));
            String result = Dao.editSite(user, jsonSite);
            showInformationAlert(result);
        } catch (Exception e) {
            e.printStackTrace();
            showInformationAlert(e.toString());
        }
    }

    private void createNew() {
        try {
            JsonSite jsonSite = new JsonSite();
            jsonSite.setName(textName.getText());
            jsonSite.setStandartNumber(textNumber.getText());
            jsonSite.setTimeToBlock(Integer.parseInt(textBlock.getText()));
            String result = Dao.addSite(user, jsonSite);
            showInformationAlert(result);
        } catch (Exception e) {
            e.printStackTrace();
            showInformationAlert(e.toString());
        }
    }

    @SuppressWarnings("Duplicates")
    public static void showInformationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
