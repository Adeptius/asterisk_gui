package javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import dao.Dao;
import model.TelephonyCustomer;
import utils.StringUtils;
import utils.Validator;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


@SuppressWarnings("Duplicates")
public class NewTelephonyController implements Initializable {


    private final GuiController guiController;
    private final Stage stage;
    private final String selectedTelephony;

    public NewTelephonyController(GuiController guiController, Stage stage, String selectedTelephony) {
        this.guiController = guiController;
        this.stage = stage;
        this.selectedTelephony = selectedTelephony;
    }

    @FXML
    private Button btnSave;

    @FXML
    private TextField textName;

    @FXML
    private TextField textPassword;

    @FXML
    private TextField textGoogleId;

    @FXML
    private TextArea textOuterPhones;

    @FXML
    private TextField textEmail;

    @FXML
    private TextArea textInnerPhones;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (selectedTelephony != null) {
            try {
                TelephonyCustomer customer = Dao.getTelephonyCustomerByName(selectedTelephony);
                textName.setText(customer.getName());
                textPassword.setText(customer.getPassword());
                textGoogleId.setText(customer.getGoogleAnalyticsTrackingId());
                textEmail.setText(customer.getMail());
                textInnerPhones.setText(StringUtils.convertToStringWithN(customer.getInnerPhones()));
                textOuterPhones.setText(StringUtils.convertToStringWithN(customer.getOuterPhones()));
                btnSave.setText("Изменить");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void save(ActionEvent event) {
        String name = textName.getText().trim();
        if (!Validator.isValidLogin(name)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Имя должно состоять только из английских букв");
            alert.showAndWait();
            return;
        }

        String email = textEmail.getText().trim();
        String googleId = textGoogleId.getText().trim();
        String password = textPassword.getText().trim();

        ArrayList<String> innerList = StringUtils.convertToList(textInnerPhones.getText());
        ArrayList<String> outerList = StringUtils.convertToList(textOuterPhones.getText());

        TelephonyCustomer customer = new TelephonyCustomer(name, email, googleId, password, innerList, outerList);

        String result = "";
        try {
            result = Dao.addOrUpdate(customer);
            stage.hide();
            guiController.updateCustomers();
//            guiController.updateLogs();
//            guiController.updatePhones();

        } catch (Exception e) {
            e.printStackTrace();
            result = "Ошибка: " + e.getMessage();
        }
        System.out.println(result);

        Alert alert = null;
        if (result.equals("Updated") || result.equals("Added")) {
            result = "Выполнено!";
            alert = new Alert(Alert.AlertType.INFORMATION);
        } else {
            alert = new Alert(Alert.AlertType.ERROR);
        }
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
    }

    @FXML
    public void cancel() {
        stage.close();
    }
}
