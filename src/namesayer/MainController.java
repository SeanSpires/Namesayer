package namesayer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

	private String _creationSelect;

	@FXML
	private Button quitButton;

	@FXML
	private Button refresh;

	@FXML
	private MediaView mediaView;

	@FXML
	private ListView<String> creationList;

	@FXML
	private Button playButton;

	@FXML
	private Button createButton;

	@FXML
	private Button deleteButton;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		listCreations();
		try {
			//Delete temp files leftover
			Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/Creations/tempAudio.wav"));
			Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/Creations/tempVideo.mp4"));
		} catch (IOException e) {

		}
	}   


	@FXML
	private void refreshClicked() {
		listCreations();
	}


	@FXML
	private void ifQuitClicked() {
		Stage stage = (Stage) quitButton.getScene().getWindow();
		stage.close();
	}
	
	@FXML
	private void creationListClicked() {
		if(creationList.getSelectionModel().isEmpty()) {
			return;
		}
		
		String path = "Creations/"+creationList.getSelectionModel().getSelectedItem();
		String absPath = new File(path).getAbsolutePath();
		Media media = new Media(new File(absPath).toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setAutoPlay(false);
		mediaView.setMediaPlayer(mediaPlayer);
		
	}

	@FXML
	private void playClicked() {

		//Check if a list item is actually selected
		if(creationList.getSelectionModel().isEmpty()) {
			return; 
		}

		this.creationList.setDisable(true);
		this.playButton.setDisable(true);
		this.createButton.setDisable(true);
		this.deleteButton.setDisable(true);
		this.refresh.setDisable(true);

		//Set up video to be played on the mediaView
		String path = "Creations/"+creationList.getSelectionModel().getSelectedItem();
		String absPath = new File(path).getAbsolutePath();
		Media media = new Media(new File(absPath).toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setAutoPlay(true);
		//Disable all buttons for the duration of the played videos
		mediaPlayer.setStopTime(Duration.millis(5000.0));
		mediaPlayer.setOnEndOfMedia(new Runnable() {
			@Override
			public void run() {
				creationList.setDisable(false);
				playButton.setDisable(false);
				createButton.setDisable(false);
				deleteButton.setDisable(false);
				refresh.setDisable(false);
				mediaView.setMediaPlayer(null);
			}
		});
		mediaView.setMediaPlayer(mediaPlayer);
		

	}

	@FXML
	public void deleteClicked() {
		_creationSelect = creationList.getSelectionModel().getSelectedItem();
		if(_creationSelect == null) {
			return;
		}
		//Allow the user to double check if they want to delete
		Alert alert = new Alert(AlertType.CONFIRMATION, "Delete " + _creationSelect + " ?",
		 ButtonType.NO,ButtonType.YES);
		alert.showAndWait();
		if (alert.getResult() == ButtonType.YES) {
			File file = new File("Creations/"+_creationSelect);
			file.delete();
			listCreations();
			mediaView.setMediaPlayer(null);

		}
		else {
			Main.changeToMain();
		}
	}

	@FXML
	public void createClicked() {
		//Move to next stage
		Main.changeToCreation();
	}

	public void listCreations() {
		File dir = new File("Creations/");
		List<String> fileList = new ArrayList<>();
		File[] directoryListing = dir.listFiles();
		FilenameFilter fileFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".mp4");
			}	 
		};
		//Loop through files and store .mp4 files in a list
		if (directoryListing != null) {
			for (File child : directoryListing) {
				if(fileFilter.accept(dir, child.getName())) {
					fileList.add(child.getName());
				}
			}
		} 
		ObservableList<String> items = FXCollections.observableArrayList(fileList);
		creationList.setItems(items);
	}

}
