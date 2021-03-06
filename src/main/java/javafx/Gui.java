package javafx;

import dao.Dao;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Gui extends Application {

    public static boolean onlyActiveSite;
    public static String selectedSiteString;
    public static HostServices hostServices;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("main.fxml"));
        Parent root = fxmlLoader.load();
        if (Dao.IP.contains("cstat.nextel.com.ua")){
            primaryStage.setTitle("Астериск монитор. Удалённый сервер");

        } else if (Dao.IP.contains("localhost")){
            primaryStage.setTitle("Астериск монитор. Локальный");

        } else {
            primaryStage.setTitle("Неизвесный адрес сервера");

        }
        primaryStage.setScene(new Scene(root, 1270, 450));
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> Platform.exit());
        primaryStage.getIcons().add(new Image(Gui.class.getResourceAsStream( "adeptius64.png" )));
        primaryStage.show();
//        guiController = fxmlLoader.getController();
        hostServices = getHostServices();
    }


    public void startGui(){
        launch();
    }

    public static void main(String[] args) {
        Gui gui = new Gui();
        gui.startGui();
    }
}
