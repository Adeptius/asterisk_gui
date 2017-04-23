package javafx;


import dao.Dao;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Tracking;
import model.User;

import java.net.URL;
import java.util.ResourceBundle;

public class AddTrackingController implements Initializable{

    private GuiController guiController;
    private Stage stage;
    private User user;


    public AddTrackingController(GuiController guiController, Stage stage, User user) {
        this.guiController = guiController;
        this.stage = stage;
        this.user = user;
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
        if (user.getTracking() != null) {// если мы добавляем
            btnSave.setText("Изменить");

            textNumber.setText(user.getTracking().getStandartNumber());
            textBlock.setText(String.valueOf(user.getTracking().getTimeToBlock()));
            numberOfOuterPhones.setText(String.valueOf(user.getTracking().getSiteNumbersCount()));
        }
        btnSave.setOnAction(e -> save());
    }

    private void cancel(){
        stage.hide();
    }

    public void remove(){
        String result;
        try{
            result = Dao.removeTracking(user);
        }catch (Exception e){
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

    private void save(){
        Tracking tracking = new Tracking();
        tracking.setSiteNumbersCount(Integer.parseInt(numberOfOuterPhones.getText()));
        tracking.setStandartNumber(textNumber.getText());
        tracking.setTimeToBlock(Integer.parseInt(textBlock.getText()));

        String result;
        try{
            result = Dao.setTracking(user, tracking);

        }catch (Exception e){
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
}
