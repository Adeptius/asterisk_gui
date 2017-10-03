package javafx;


import dao.Dao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import json.JsonSite;
import model.User;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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

    @FXML
    private TextField textScript;

    @FXML
    private TextField textGoogleTrackingId;

    @FXML
    private TextArea textBlackIp;

    @FXML
    private TextArea textOuterPhones;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnCancel.setOnAction(e -> cancel());
        JsonSite siteByName = null;
        try {
            List<JsonSite> sites = Dao.getSites(user);
            siteByName = sites.stream().filter(site -> site.getName().equals(siteName)).findFirst().get();
        } catch (Exception e) {
        }

        if (siteByName != null) {
            textName.setText(siteName);
            textNumber.setText(siteByName.getStandardNumber());
            textBlock.setText(String.valueOf(siteByName.getTimeToBlock()));
            textScript.setText(siteByName.getScript());
            textGoogleTrackingId.setText(siteByName.getGoogleTrackingId());

            List<String> blackList = siteByName.getBlackList();
            for (int i = 0; i < blackList.size(); i++) {
                String s = blackList.get(i);
                if (i==0){
                    textBlackIp.appendText(s);
                }else {
                    textBlackIp.appendText("\n" + s);
                }
            }

            List<String> numbers = siteByName.getOuterPhones().stream()
                    .map(outerPhone -> outerPhone.getNumber())
                    .sorted()
                    .collect(Collectors.toList());
            for (int i = 0; i < numbers.size(); i++) {
                String s = numbers.get(i);
                if (i==0){
                    textOuterPhones.appendText(s);
                }else {
                    textOuterPhones.appendText("\n" + s);
                }
            }

        } else {
            textBlock.setText("120");
        }
        btnSave.setOnAction(e -> setSite());
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


    private void setSite() {
        try {
            JsonSite jsonSite = new JsonSite();
            jsonSite.setName(textName.getText());
            jsonSite.setStandardNumber(textNumber.getText());
            jsonSite.setTimeToBlock(Integer.parseInt(textBlock.getText()));
            jsonSite.setGoogleTrackingId(textGoogleTrackingId.getText());

            String text = textBlackIp.getText();
            List<String> ips = new ArrayList<>();
            String[] split = text.split("\n");
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                if (s != null && !s.equals("") && !s.equals("\n") && !s.equals(" ")){
                    ips.add(s);
                }
            }
            jsonSite.setBlackList(ips);

            String phoneText = textOuterPhones.getText();
            List<String> phones = new ArrayList<>();
            String[] splited = phoneText.split("\n");
            for (int i = 0; i < splited.length; i++) {
                String s = splited[i];
                if (s != null && !s.equals("") && !s.equals("\n") && !s.equals(" ")){
                    phones.add(s);
                }
            }
            jsonSite.setConnectedPhones(phones);

            String result = Dao.setSite(user, jsonSite);
            showInformationAlert(result);
        } catch (Exception e) {
            e.printStackTrace();
            showInformationAlert(e.toString());
        }
    }


//    private void edit(Site site) {
//        try {
//            JsonSite jsonSite = new JsonSite();
//            jsonSite.setName(siteName);
//            jsonSite.setStandartNumber(textNumber.getText());
//            jsonSite.setTimeToBlock(Integer.parseInt(textBlock.getText()));
//            String result = Dao.editSite(user, jsonSite);
//            showInformationAlert(result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            showInformationAlert(e.toString());
//        }
//    }
//
//    private void createNew() {
//        try {
//            JsonSite jsonSite = new JsonSite();
//            jsonSite.setName(textName.getText());
//            jsonSite.setStandartNumber(textNumber.getText());
//            jsonSite.setTimeToBlock(Integer.parseInt(textBlock.getText()));
//            String result = Dao.addSite(user, jsonSite);
//            showInformationAlert(result);
//        } catch (Exception e) {
//            e.printStackTrace();
//            showInformationAlert(e.toString());
//        }
//    }

    @SuppressWarnings("Duplicates")
    public static void showInformationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
