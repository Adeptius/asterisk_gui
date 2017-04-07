package javafx;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import dao.Dao;

import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("Duplicates")
public class DeleteController implements Initializable{

    private String customer;
    private Stage stage;
    private GuiController guiController;


    public DeleteController(GuiController guiController, Stage stage, String customer) {
        this.customer = customer;
        this.stage = stage;
        this.guiController = guiController;
    }

    @FXML
    private Label label;

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnDelete;


    private void delete() {
        String result = "";
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        try{
            result = Dao.removeCustomer(customer);
            stage.hide();
            guiController.updateCustomers();
            guiController.updatePhones();
            guiController.updateLogs();
            guiController.hideSiteTableAndButtons();
        }catch (Exception e){
            e.printStackTrace();
            result = "Ошибка: " + e.getMessage();
            alert.setAlertType(Alert.AlertType.ERROR);
        }
        alert.setTitle("Результат");
        alert.setHeaderText(null);
        alert.setContentText(result);
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.setText("Внимание!\nПользователь " + customer + "\nбудет удалён!");
        btnDelete.setOnAction(event ->  delete());
        btnCancel.setOnAction(event ->  cancel());
        btnCancel.setFocusTraversable(true);
    }

    private void cancel(){
        stage.hide();
    }
}
