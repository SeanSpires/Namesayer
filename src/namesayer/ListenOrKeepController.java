package namesayer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Timer;

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
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class ListenOrKeepController implements Initializable {

	@FXML
	private Button listen;


	@FXML
	private Button keep;


	@FXML
	private Button redo;

	@FXML
	private ProgressBar listenProgress;
	
	private static Timer disableTime = new Timer();
	
	private static Timer progressTime = new Timer();



	@FXML
	public void ifRedoClick() {
		//Remove tempAudio file as we are going reuse the same file name
		try {
			Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/Creations/tempAudio.wav"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Main.changeToRecording();
	}

	@FXML
	public void ifListenClick() {
		disableButtons(); 
		disableTime.cancel();
		progressTime.cancel();
		listenProgress.setProgress(0.0);
		
		//Create timers to enable and disable buttons at a certain interval
		disableTime = new Timer();
		TimerTask disableTask = new TimerTask() {
			@Override
			public void run() {
				enableButtons();	
			}
		};
		
		progressTime = new Timer();
		//Create a timer for the progress bar
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				double progress = listenProgress.getProgress();
				if (progress == 1) {
					listenProgress.setProgress(0);
					progressTime.cancel();
					return;
				}
				listenProgress.setProgress(progress + 0.01);
			}	
		};

		
	
		//Play the audio file
		Media audio = new Media(new File("Creations/tempAudio.wav").toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(audio);
		mediaPlayer.setAutoPlay(true);  
		disableTime.schedule(disableTask, 5200);	
		progressTime.scheduleAtFixedRate(timerTask, 0, 50);

	}

	@FXML
	public void ifKeepClick() {

		Service<Void> backgroundThread = new Service<Void>() {
			@Override
			protected Task<Void> createTask() {
				return new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						//Merge the files
						ProcessBuilder merge = new ProcessBuilder("ffmpeg", "-y", "-i",
								"Creations/tempVideo.mp4", "-i",
								"Creations/tempAudio.wav", "-c:v", "copy", "-c:a", "aac", "-strict", "experimental",
								"Creations/"+CreationController._name+".mp4");
						try {
							Process mergeProcess = merge.start();
							mergeProcess.waitFor();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
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
				Alert alert = new Alert(AlertType.NONE,"Creation Created!", ButtonType.OK);
				alert.showAndWait();
				if(alert.getResult() == ButtonType.OK) {
					ListenOrKeepController.deleteFiles(); //remove the temp files
					Main.changeToMain();
				}
			}		
		});


		backgroundThread.start();

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	public static void deleteFiles() {
		try {
			Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/Creations/tempAudio.wav"));
			Files.deleteIfExists(Paths.get(System.getProperty("user.dir") + "/Creations/tempVideo.mp4"));
		} catch (IOException e) {

		}
	}
	
	public void disableButtons() {
		listen.setDisable(true);
		keep.setDisable(true);
		redo.setDisable(true);
	}
	
	public void enableButtons() {
		listen.setDisable(false);
		keep.setDisable(false);
		redo.setDisable(false);
	}

}
