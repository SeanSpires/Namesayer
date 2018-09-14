package namesayer;

import java.io.File;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

	private static Stage _primaryStage;
	private static Scene _mainMenu,_creationScene,_recordingScene,_listenScene;

	@Override
	public void start(Stage primaryStage) throws Exception{
		_primaryStage = primaryStage;

		//Create the directory to put creations in
		File creationDir = new File(System.getProperty("user.dir")+"/Creations");
		if (!creationDir.exists()){
			creationDir.mkdir();
		} 

		//Load the roots of the multiple scences
		Parent root = FXMLLoader.load(getClass().getResource("MainMenu.fxml"));
		Parent creationRoot = FXMLLoader.load(getClass().getResource("CreationScene.fxml"));
		Parent recordingRoot = FXMLLoader.load(getClass().getResource("RecordingScene.fxml"));
		Parent listenRoot = FXMLLoader.load(getClass().getResource("ListenOrKeep.fxml"));

		_mainMenu = new Scene(root,600,400);
		_creationScene = new Scene(creationRoot,600,400);
		_recordingScene = new Scene(recordingRoot,600,400);
		_listenScene = new Scene(listenRoot,600,400);

		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.centerOnScreen();
		primaryStage.setTitle("Name Sayer");
		primaryStage.setScene(_mainMenu);
		primaryStage.show();

	}




	/**
	 * Methods to change scenes
	 */
	public static void changeToMain() {
		_primaryStage.setScene(_mainMenu);
	}


	public static void changeToCreation() {
		_primaryStage.setScene(_creationScene);
	}

	public static void changeToRecording() {
		_primaryStage.setScene(_recordingScene);
	}

	public static void changeToListen() {
		_primaryStage.setScene(_listenScene);
	}

	public static void main(String[] args) {
		launch(args);
	}
}
