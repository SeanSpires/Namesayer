package namesayer;

import javafx.animation.PauseTransition;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimerTask;
import java.util.Timer;

import javax.swing.*;
import java.awt.event.*;

public class RecordingController implements Initializable {

	private Service<Void> _backgroundThread;

	@FXML
	private Button recordQuit;

	@FXML
	private Button confirmRecord;
	
	@FXML
	private ProgressBar progressBar;
	
	private static Timer progressTimer = new Timer();
	
	@FXML
	public void clickConfirmRecord() {
		progressTimer.cancel();
		progressBar.setProgress(0.0);
		confirmRecord.setDisable(true);
		
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				double progress = progressBar.getProgress();
				if (progress == 1) {
					progressBar.setProgress(0);
					progressTimer.cancel();
					return;
				}
				
				progressBar.setProgress(progress + 0.01);
			}	
		};
		
		progressTimer = new Timer();
		progressTimer.scheduleAtFixedRate(timerTask, 0, 50);
		
		
		//Use a background thread for recording 
		_backgroundThread = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						ProcessBuilder recordBuilder = new ProcessBuilder("ffmpeg","-y","-f","alsa","-ac","1"
								,"-ar","44100","-i","default","-t", "5","Creations/tempAudio.wav");
						try {
							Process p = recordBuilder.start();
							p.waitFor();
							
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} 
						return null;
					}
				};
			}
		};

		_backgroundThread.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			//Change scene when recording is finished
			@Override
			public void handle(WorkerStateEvent event) {
				confirmRecord.setDisable(false);
				Main.changeToListen();

			}		
		});
		_backgroundThread.start();
	}
	

	@FXML
	public void clickRecordQuit() {
		
		if(_backgroundThread == null) {
			Main.changeToMain();
			return;
		}
		//Check if a background thread is running
		if(_backgroundThread.isRunning()) {
			confirmRecord.setDisable(false);
			progressTimer.cancel();
			progressBar.setProgress(0.0);
			_backgroundThread.cancel();
		}
		ListenOrKeepController.deleteFiles();
		Main.changeToMain();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		progressBar.setProgress(0.0);
	}


}
