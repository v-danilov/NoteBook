package app.controllers;


import app.Entity.NoteEntity;
import app.hibernate.HibernateSessionFactory;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.hibernate.Session;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;


public class ListController {

    @FXML
    private AnchorPane mainPane;

    @FXML
    private TableView noteTable;

    @FXML
    private TableColumn<NoteEntity, Timestamp> dateColumn;

    @FXML
    private TableColumn<NoteEntity, String> headerColumn, textColumn;


    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML
    public void initialize() {

        addButton.setTooltip(new Tooltip("Добавить заметку"));
        deleteButton.setTooltip(new Tooltip("Удалить выбранную"));
        //table format
        dateColumn.setCellValueFactory(new PropertyValueFactory<NoteEntity, Timestamp>("date"));
        headerColumn.setCellValueFactory(new PropertyValueFactory<NoteEntity, String>("header"));
        textColumn.setCellValueFactory(new PropertyValueFactory<NoteEntity, String>("text"));
        noteTable.setPlaceholder(new Label("Data is loading. Wait..."));

        //table size
        dateColumn.prefWidthProperty().bind(noteTable.widthProperty().multiply(0.20));
        headerColumn.prefWidthProperty().bind(noteTable.widthProperty().multiply(0.15));
        textColumn.prefWidthProperty().bind(noteTable.widthProperty().multiply(0.65));

        //get all notes
        updateData();
    }


    //Add new note function
    public void addNewNote(ActionEvent actionEvent) {
        Parent root;
        try {

            //Create stage with interface for new note
            root = FXMLLoader.load(getClass().getClassLoader().getResource("fxml/noteForm.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(mainPane.getScene().getWindow());
            stage.setTitle("New note");
            Scene scene = new Scene(root);
            scene.getStylesheets().add("Interface.css");
            stage.setResizable(false);

            stage.setScene(scene);

            //When new note window will close -> update list
            stage.setOnHiding(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    //update function
                    updateData();
                }
            });

            //Show new window
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //SQL query with list result
    private List<NoteEntity> getAllNotes() {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        session.beginTransaction();
        List<NoteEntity> notesList = session.createQuery("from NoteEntity").list();
        session.close();
        return notesList;
    }


    private void updateData() {

        //Create new thread
        Thread update = new Thread() {
            public void run() {

                //get data from database
                List<NoteEntity> data = getAllNotes();

                //Update UI when data loaded
                Platform.runLater(new Runnable() {
                    public void run() {
                        noteTable.getItems().clear();
                        noteTable.getItems().addAll(data);
                    }
                });
            }
        };

        //Start thread
        update.start();
    }

    private void deleteRow(NoteEntity element){
        Thread update = new Thread() {
            public void run() {

                //get data from database
                Session session = HibernateSessionFactory.getSessionFactory().openSession();
                session.beginTransaction();
                session.delete(element);
                session.getTransaction().commit();
                List<NoteEntity> data = session.createQuery("from NoteEntity").list();
                session.close();

                //Update UI when data loaded
                Platform.runLater(new Runnable() {
                    public void run() {
                        noteTable.getItems().clear();
                        noteTable.getItems().addAll(data);
                    }
                });
            }
        };

        //Start thread
        update.start();
    }

    public void deleteNote(ActionEvent actionEvent) {
       NoteEntity selected_note = (NoteEntity) noteTable.getSelectionModel().getSelectedItem();
       if(selected_note != null) {
           deleteRow(selected_note);
       }
    }
}
