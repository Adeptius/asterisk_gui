package javafx;


import dao.Dao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.JsonSipAndPass;

import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("Duplicates")
public class PasswordsController implements Initializable{

    private String customer;
    private Stage stage;
    private GuiController guiController;


    public PasswordsController(GuiController guiController, Stage stage, String customer) {
        this.customer = customer;
        this.stage = stage;
        this.guiController = guiController;
    }

    @FXML
    private TableColumn<JsonSipAndPass, String> columnSip;

    @FXML
    private TableColumn<JsonSipAndPass, String> columnPass;

    @FXML
    private TableView<JsonSipAndPass> table;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnSip.setCellValueFactory(new PropertyValueFactory<>("sip"));
        columnPass.setCellValueFactory(new PropertyValueFactory<>("pass"));
        try {
            table.setItems(FXCollections.observableList(Dao.getPasswords(customer)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void cancel(){
        stage.hide();
    }
}
