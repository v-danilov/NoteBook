package app.controllers;

import app.Entity.NoteEntity;
import app.hibernate.HibernateSessionFactory;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.hibernate.Session;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NoteController {

    @FXML
    private AnchorPane pane;

    @FXML
    private TextField dateBox;

    @FXML
    private TextField timeBox;

    @FXML
    private TextField headerBox;

    @FXML
    private Label symbolLable;

    @FXML
    private TextArea textField;

    @FXML
    private Button saveButton;

    private LocalDateTime currentTime;


    @FXML
    public void initialize() {

        textField.setStyle("-fx-font-size: 16");
        currentTime = LocalDateTime.now();
        dateBox.setText(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(currentTime));
        timeBox.setText(DateTimeFormatter.ofPattern("HH:mm:ss").format(currentTime));

        //Header length limit (20)
        headerBox.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.length() > 20) {
                    headerBox.setText(oldValue);
                }
            }
        });

        //Text length limit (100)
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                int symbol_counter = newValue.length();
                symbolLable.setText(symbol_counter + "/" + 100);
                if (symbol_counter > 100) {
                    textField.setText(oldValue);
                }
            }
        });
    }


    public void saveNote() {

        //Create new note object
        NoteEntity new_note = new NoteEntity();
        new_note.setDate(Timestamp.valueOf(currentTime));
        new_note.setHeader(headerBox.getText());
        new_note.setText(textField.getText());

        //Thread with insert function
        Thread update = new Thread() {
            public void run() {
                insert(new_note);
            }
        };

        //Start thread
        update.start();

        //Join thread before close the stage

        Stage stage = (Stage) saveButton.getScene().getWindow();

        try {
            update.join();
        } catch (InterruptedException ie) {
            System.err.println(ie.getStackTrace());
        } finally {
            stage.close();
        }
    }

    private void insert(NoteEntity noteEntity) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        session.beginTransaction();
        session.save(noteEntity);
        session.getTransaction().commit();
        session.close();
    }
}
