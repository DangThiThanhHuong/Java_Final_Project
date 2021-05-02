package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Gaming {
	Stage primaryStage;
	ResourceLock lock;
	Socket incoming;
	PrintWriter outPrinter;
	SaveClient client;
	Scene scene;
	UpdateScene thread2;
	

	public Gaming(Stage primaryStage, ResourceLock lock, Scene scene, Socket incoming,
			PrintWriter outPrinter,SaveClient client) {
		super();
		this.primaryStage = primaryStage;
		this.lock = lock;
		this.scene = scene;
		this.incoming = incoming;
		this.outPrinter = outPrinter;
		this.client = client;
	}
	public void startGaming() throws IOException {
		ThreadLoadSceneGame thread1 = new ThreadLoadSceneGame(lock, incoming, outPrinter);
		thread1.start();
		System.out.println("Start gamming");
		FXMLLoader fxmlLoader = new FXMLLoader(
				Server.class.getResource("PlayGame.fxml"));
		PlayGameController sbViewController = new PlayGameController();
		fxmlLoader.setController(sbViewController);
		lock.root = fxmlLoader.load();
		Platform.runLater(()->{
			((Label)lock.root.getChildren().get(3)).setText("Player1: "+client.client1);
			((Label)lock.root.getChildren().get(5)).setText("Player2: "+client.client2);
			scene.setRoot(lock.root);
			scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Fighting Plane");
			primaryStage.show();
		});
		
	}
	public void playGaming(String request) {
		new UpdateScene(incoming, lock.root,client,outPrinter,request).start();
	}
}
