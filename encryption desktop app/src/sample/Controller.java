package sample;

// JavaFX packs

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;

// utilities packs

public class Controller {
    @FXML
    Label waitShow;
    @FXML
    Label waitEncrypt;
    @FXML
    ChoiceBox<Integer> seedChoice;
    @FXML
    ArrayList<Integer> seedChoiceListArray;
    @FXML
    ObservableList<Integer> seedChoiceList;
    @FXML
    TextArea fileViewer;
    @FXML
    TextField filePath;

    @FXML
    void initialize() {
        // add values in choice box
        seedChoiceListArray = new ArrayList<>();
        for (int i = 1; i <= 1000; i++)
            seedChoiceListArray.add(i);
        seedChoiceList = FXCollections.observableArrayList(seedChoiceListArray);
        seedChoice.setValue(1);
        seedChoice.setItems(seedChoiceList);

        waitShow.setText("");    // loading message
        waitEncrypt.setText(""); // loading message
    }

    @FXML
    void clickBrowse(ActionEvent ev) {
        // implement file chooser button
        final FileChooser filec = new FileChooser();
        File file = filec.showOpenDialog(null);
        if (file != null)
            filePath.setText(file.getAbsolutePath());
    }

    @FXML
    void clickShow(ActionEvent ev) {
        waitShow.setText("Wait.."); // loading message

        // display file in file viewer
        try {
            if (!(filePath.getText().endsWith(".txt"))) {
                waitShow.setText("Incorrect file format."); // loading message
                return;
            }
            String content = new String(Files.readAllBytes(Paths.get(filePath.getText())));
            fileViewer.setText(content);
            waitShow.setText("Done."); // loading message
        } catch (IOException exc) {
            waitShow.setText("Can't open text file."); // loading message
        }
    }

    @FXML
    void clickEncrypt() {
        waitEncrypt.setText("Wait.."); // loading message

        // init random
        SecureRandom random = new SecureRandom();
        random.setSeed(seedChoice.getValue());

        try {
            if (!filePath.getText().endsWith(".txt")) { // test for format
                waitEncrypt.setText("Incorrect file format."); // loading message
                return;
            }

            // store init random config
            String filepathText = filePath.getText();
            try {
                // next line says that if input is filename.txt,
                // then output will be filename_keygen.txt
                // for storing key generator
                Files.write(Paths.get(filepathText.substring(0, filepathText.length() - 4) + "_keygen" + ".txt"), seedChoice.getValue().toString().getBytes());
            } catch(IOException exc) {
                waitEncrypt.setText("Can't create keygen file."); // loading message
                return;
            }

            // read text file
            byte[] in = Files.readAllBytes(Paths.get(filePath.getText()));

            // generate key
            byte[] key = new byte[in.length];
            random.nextBytes(key);

            // generate encrypted content
            byte[] out = new byte[in.length];
            for (int i = 0; i < in.length; i++)
                out[i] = (byte) (in[i] ^ key[i]);

            // write encrypted file
            Files.write(Paths.get(filePath.getText()), out);

            waitEncrypt.setText("Done."); // loading message
        } catch(IOException exc) {
            waitEncrypt.setText("Can't open text file."); // loading message
        }
    }
}
