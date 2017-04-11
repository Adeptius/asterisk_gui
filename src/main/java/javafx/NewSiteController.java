package javafx;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import dao.Dao;
import model.Site;
import utils.Validator;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class NewSiteController implements Initializable{

    private GuiController guiController;
    private Stage stage;
    private String selectedSiteString;


    public NewSiteController(GuiController guiController, Stage stage, String selectedSiteString) {
        this.guiController = guiController;
        this.stage = stage;
        this.selectedSiteString = selectedSiteString;
    }

    @FXML
    private Button btnCancel;

    @FXML
    private TextField textName;

    @FXML
    private TextField textNumber;

    @FXML
    private Button btnSave;

    @FXML
    private TextField textGoogleId;

    @FXML
    private TextArea textBlackList;

    @FXML
    private TextField textEmail;

    @FXML
    private TextArea textPhones;

    @FXML
    private TextField textPassword;

    @FXML
    private TextField textBlock;

    @FXML
    private TextField numberOfOuterPhones;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnCancel.setOnAction(e -> cancel());
        if (selectedSiteString != null){// если мы меняем сайт
            btnSave.setText("Изменить");
            textName.setEditable(false);

            Site site = Dao.getSiteByName(selectedSiteString);
            textName.setText(site.getName());
            textEmail.setText(site.getMail());
            textNumber.setText(site.getStandartNumber());
            textGoogleId.setText(site.getGoogleAnalyticsTrackingId());
            textPassword.setText(site.getPassword());
            textBlock.setText(String.valueOf(site.getTimeToBlock()));
            numberOfOuterPhones.setText(String.valueOf(site.getOuterNumbersCount()));

//            String phones = "";
//            for (Phone phone : site.getPhones()) {
//                phones += phone.getNumber() + "\n";
//            }
//            if (phones.length() >0){
//                phones = phones.substring(0, phones.length() -1);
//            }

            String ips = "";
            for (String s : site.getBlackIps()) {
                ips += s +"\n";
            }
            if (ips.length() >0){
                ips = ips.substring(0, ips.length() -1);
            }

//            textPhones.setText(phones);
            textBlackList.setText(ips);
        }

        btnSave.setOnAction(e -> save());
    }

    private void cancel(){
        stage.hide();
    }

    private void save(){
        String name = textName.getText().trim();
        if (!Validator.isValidLogin(name)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Имя должно состоять только из английских букв");
            alert.showAndWait();
            return;
        }

        String email = textEmail.getText().trim();
        String standartNumber = textNumber.getText().trim();
        String googleId = textGoogleId.getText().trim();
//        String phones = textPhones.getText().trim();
        String blackList = textBlackList.getText().trim();
        String password = textPassword.getText().trim();
        int timeToBlock = Integer.parseInt(textBlock.getText().trim());
        int outerNumbersCount = Integer.parseInt(numberOfOuterPhones.getText().trim());

//        phones = phones.replaceAll(" ", "").replaceAll("\t","");
        blackList = blackList.replaceAll(" ", "").replaceAll("\t","");

//        List<Phone> phoneList = new ArrayList<>();
//        String[] phonesArr = phones.split("\n");
//        for (String s : phonesArr) {
//            phoneList.add(new Phone(s));
//        }

        List<String> blackIps = new ArrayList<>();
        String[] blackIpsArr = blackList.split("\n");
        for (String s : blackIpsArr) {
            blackIps.add(s);
        }

        String result = "";
        try {
            Site site = new Site(name,standartNumber, googleId, email, blackIps, password, timeToBlock, outerNumbersCount);
            result = Dao.addOrUpdate(site);
            stage.hide();
            guiController.updateCustomers();
            guiController.updateLogs();
            guiController.updateSitePhones();

        }catch (Exception e){
            e.printStackTrace();
            result = "Ошибка: " + e.getMessage();
        }
        System.out.println(result);

        Alert alert = null;
        if (result.equals("Updated") || result.equals("Added")){
            result = "Выполнено!";
            alert = new Alert(Alert.AlertType.INFORMATION);
        }else {
            alert = new Alert(Alert.AlertType.ERROR);
        }
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
        guiController.updateStatus();

    }
}
