package javafx;


import dao.Dao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import json.JsonSipAndPass;
import model.User;

import java.net.URL;
import java.util.ResourceBundle;

@SuppressWarnings("Duplicates")
public class PasswordsController implements Initializable{

    private Stage stage;
    private GuiController guiController;
    private User user;

    public PasswordsController(GuiController guiController, Stage stage, User user) {
        this.stage = stage;
        this.guiController = guiController;
        this.user = user;
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
            table.setItems(FXCollections.observableList(Dao.getPasswords(user)));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void cancel(){
        stage.hide();
    }
}
