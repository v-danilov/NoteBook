package app;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {

    //java -cp NoteBook-1.0-SNAPSHOT.jar app.Main
    //java -jar NoteBook-1.0-jar-with-dependencies.jar

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        String fxmlFile = "/fxml/listForm.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent root = (Parent) loader.load(getClass().getResourceAsStream(fxmlFile));
        stage.setTitle("Personal notebook");
        Scene scene = new Scene(root);
        scene.getStylesheets().add("Interface.css");
        stage.setScene(scene);
        //stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() {
        System.exit(0);
    }
}