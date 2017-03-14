package javafx;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Dao;
import model.History;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.List;


public class HistoryController implements Initializable {

    private Stage stage;
    private String site;

    public HistoryController(String site, Stage stage) {
        this.site = site;
        this.stage = stage;
    }

    @FXML
    private TableColumn<History, String> toColumn;

    @FXML
    private TableColumn<History, String> fromColumn;

    @FXML
    private TableColumn<History, String> dateColumn;

    @FXML
    private TableColumn<History, String> talkingTimeColumn;

    @FXML
    private TableColumn<History, String> timeToAnswerColumn;

    @FXML
    private TableColumn<History, String> googleIDColumn;

    @FXML
    private TableColumn<History, String> callIDColumn;

    @FXML
    private TableColumn<History, String> utmColumn;

    @FXML
    private TableView<History> table;

    @FXML
    private Button buttonShowOut;

    @FXML
    private Button buttonShowIn;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        toColumn.setCellValueFactory(new PropertyValueFactory<>("to"));
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("from"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        talkingTimeColumn.setCellValueFactory(new PropertyValueFactory<>("talkingTime"));
        timeToAnswerColumn.setCellValueFactory(new PropertyValueFactory<>("timeToAnswerForWebInSeconds"));
        googleIDColumn.setCellValueFactory(new PropertyValueFactory<>("googleId"));
        callIDColumn.setCellValueFactory(new PropertyValueFactory<>("callUniqueId"));
        utmColumn.setCellValueFactory(new PropertyValueFactory<>("request"));
        buttonShowIn.setOnAction(event -> showHistory(textFrom.getText(), textTo.getText(), "IN"));
        buttonShowOut.setOnAction(event -> showHistory(textFrom.getText(), textTo.getText(), "OUT"));
        btnDownload.setOnAction(event -> openInBrowser());

        Calendar calendar = new GregorianCalendar();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        String currentDate = year + "-" + doTwoSymb(month) + "-" + doTwoSymb(day);
        String tomorrowDate = year + "-" + doTwoSymb(month) + "-" + doTwoSymb(day + 1);

        textFrom.setText(currentDate + " 00:00:00");
        textTo.setText(tomorrowDate + " 00:00:00");
    }


    private void showHistory(String from, String to, String direction) {
        List<History> histories = Dao.getHistory(site, from, to, direction);
        Collections.reverse(histories);
        table.setItems(FXCollections.observableArrayList(histories));
    }

    public static String doTwoSymb(int i) {
        String s = String.valueOf(i);
        if (s.length() == 1) s = "0" + s;
        return s;
    }


    private void openInBrowser() {
        History history = table.getSelectionModel().getSelectedItem();
        String date = history.getDate();
        date = date.substring(0, date.indexOf(" "));

        String url = Dao.IP+"/status/record/"
                + history.getCallUniqueId()
                + "/"
                + date;
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        }
    }
}
