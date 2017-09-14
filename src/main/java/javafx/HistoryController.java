package javafx;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import dao.Dao;
import json.JsonHistoryResponse;
import model.Call;
import model.User;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;


public class HistoryController implements Initializable {

    private GuiController guiController;
    private Stage stage;
    private User user;

    public HistoryController(GuiController guiController, Stage stage, User user) {
        this.guiController = guiController;
        this.stage = stage;
        this.user = user;
    }

    @FXML
    private TableColumn<Call, String> toColumn;

    @FXML
    private TableColumn<Call, String> fromColumn;

    @FXML
    private TableColumn<Call, String> dateColumn;

    @FXML
    private TableColumn<Call, String> statusColumn;

    @FXML
    private TableColumn<Call, String> talkingTimeColumn;

    @FXML
    private TableColumn<Call, String> timeToAnswerColumn;

    @FXML
    private TableColumn<Call, String> googleIDColumn;

    @FXML
    private TableColumn<Call, String> callIDColumn;

    @FXML
    private TableColumn<Call, String> utmColumn;

    @FXML
    private TableView<Call> table;

    @FXML
    private Button buttonShowOut;

    @FXML
    private Button buttonShowIn;

    @FXML
    private Button buttonShowBoth;

    @FXML
    private TextField dateFrom;

    @FXML
    private TextField dateTo;

    @FXML
    private TextField textFrom;

    @FXML
    private TextField textTo;

    @FXML
    private Button btnDownload;

    @FXML
    private Label countLabel;

    @FXML
    private TextField limitText;

    @FXML
    private TextField offsetText;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        toColumn.setCellValueFactory(new PropertyValueFactory<>("calledToOnePhone"));
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("calledFrom"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("callState"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("calledDate"));
        timeToAnswerColumn.setCellValueFactory(new PropertyValueFactory<>("secondsFullTime"));
        talkingTimeColumn.setCellValueFactory(new PropertyValueFactory<>("secondsTalk"));
        googleIDColumn.setCellValueFactory(new PropertyValueFactory<>("googleId"));
        callIDColumn.setCellValueFactory(new PropertyValueFactory<>("asteriskId"));
        utmColumn.setCellValueFactory(new PropertyValueFactory<>("utm"));
        buttonShowIn.setOnAction(e -> showHistory(textFrom.getText(), textTo.getText(), "IN"));
        buttonShowOut.setOnAction(e -> showHistory(textFrom.getText(), textTo.getText(), "OUT"));
        buttonShowBoth.setOnAction(e -> showHistory(textFrom.getText(), textTo.getText(), "BOTH"));
        btnDownload.setOnAction(e -> openInBrowser());

        Calendar calendar = new GregorianCalendar();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String currentDate = year + "-" + doTwoSymb(month) + "-" + doTwoSymb(day);
        String tomorrowDate = year + "-" + doTwoSymb(month) + "-" + doTwoSymb(day + 1);

        textFrom.setText(currentDate + " 00:00:00");
        textTo.setText(tomorrowDate + " 00:00:00");
        btnDownload.setVisible(false);
        table.setOnMouseClicked(e -> {
            if (table.getSelectionModel().getSelectedItem() != null){
                btnDownload.setVisible(true);
            }
        });
        limitText.setText("100");
        offsetText.setText("0");
    }

    private void showHistory(String from, String to, String direction) {
        try {
            int limit = Integer.parseInt(limitText.getText());
            int offset = Integer.parseInt(offsetText.getText());
            String response = Dao.getHistory(user, from, to, direction, limit, offset);
            if (response.substring(0,30).contains("\"status\":\"Error\"")){
                GuiController.showErrorAlert(response);
                return;
            }
            JsonHistoryResponse history = new Gson().fromJson(response, JsonHistoryResponse.class);

            int count = history.getCount();
            countLabel.setText("Всего записей: " + count);

            List<Call> histories = history.getCalls();
            Collections.reverse(histories);
            table.setItems(FXCollections.observableArrayList(histories));
            btnDownload.setVisible(false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String doTwoSymb(int i) {
        String s = String.valueOf(i);
        if (s.length() == 1) s = "0" + s;
        return s;
    }


    private void openInBrowser() {
        Call history = table.getSelectionModel().getSelectedItem();
        String date = history.getCalledDate();
        date = date.substring(0, date.indexOf(" "));

        String url = Dao.IP + "/history/record/" + history.getAsteriskId() + "/" + date;

        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e1) {
            e1.printStackTrace();
        }
    }
}