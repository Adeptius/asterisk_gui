package javafx;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Dao;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DeleteController implements Initializable{

    private String sitename;
    private Stage stage;
    private GuiController guiController;


    public DeleteController(GuiController guiController, Stage stage, String sitename) {
        this.sitename = sitename;
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
        try{
            result = Dao.removeSite(sitename);
            stage.hide();
            guiController.updateSites();
            guiController.updatePhones();
            guiController.updateLogs();
        }catch (Exception e){
            e.printStackTrace();
            result = "Ошибка: " + e.getMessage();
        }
        System.out.println(result);
        //TODO сообщение

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.setText("Внимание!\nСайт " + sitename + "\nбудет удалён!");
        btnDelete.setOnAction(event ->  delete());
        btnCancel.setOnAction(event ->  cancel());
        btnCancel.setFocusTraversable(true);
    }



    private void cancel(){
        stage.hide();
    }
}
