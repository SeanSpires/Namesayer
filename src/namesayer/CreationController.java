package namesayer;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.media.Media;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

public class CreationController implements Initializable {

	public static String _name;

	@FXML
	private Text creationText;

	@FXML
	private TextField creationName;

	@FXML
	private Button confirmCreate;

	@FXML
	private Button creationExit;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}


	@FXML
	public void creationExitClick() {
		creationName.clear();
		Main.changeToMain();
	}

	@FXML
	public void creationConfirmClick() {
		_name = creationName.getText();
		creationName.clear();

		//Check if file already exists and give user the choice to override
		if(new File("Creations/"+_name+".mp4").exists()) {
			Alert alert = new Alert(AlertType.NONE, "creation already exists, override?"
					+ "", ButtonType.YES,ButtonType.NO);
			alert.showAndWait();
			if(alert.getResult() == ButtonType.YES) {
				buildTempVideo();
			}
			else {
				Main.changeToCreation();
			}
		}
		//Check for valid input
		else if(_name.isEmpty() || _name == null || _name.length() == 1) {
			Alert alert = new Alert(AlertType.NONE, "Please enter a valid name", ButtonType.OK);
			alert.showAndWait();
			if(alert.getResult() == ButtonType.OK) {
				Main.changeToCreation();
			}
		}
		else {
			buildTempVideo();
		}

	}
	
	//Method to create blank video with text
	public void buildTempVideo() {
				
		Service<Void> backgroundThread = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						ProcessBuilder videoBuilder = new ProcessBuilder("ffmpeg", "-y", "-f", "lavfi",
								"-i", "color=c=black:s=320x240:d=5", "-vf","drawtext=fontsize=30:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text="+_name,
								System.getProperty("user.dir")+"/Creations/tempVideo.mp4");
						try {
							Process p = videoBuilder.start();
							p.waitFor();
						} catch (IOException e) {
							e.printStackTrace();
						}
						return null;
					}
				};
			}
		};

		backgroundThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			//Change scene when recording is finished
			@Override
			public void handle(WorkerStateEvent event) {
				Main.changeToRecording();

			}		
		});
		
		backgroundThread.start();
	}
}



