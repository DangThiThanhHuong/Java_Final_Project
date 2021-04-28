package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import server.Main.SocketConnection;

public class Gaming {
	Stage primaryStage;
	ResourceLock lock;
	Socket incoming;
	PrintWriter outPrinter;
	SaveClient client;
	

	public Gaming(Stage primaryStage, ResourceLock lock, Socket incoming,
			PrintWriter outPrinter,SaveClient client) {
		super();
		this.primaryStage = primaryStage;
		this.lock = lock;
		this.incoming = incoming;
		this.outPrinter = outPrinter;
		this.client = client;
	}

	public void startGaming() throws IOException {
		this.primaryStage = primaryStage;
		FXMLLoader fxmlLoader = new FXMLLoader(
				Main.class.getResource("PlayGame.fxml"));
		PlayGameController sbViewController = new PlayGameController();
		fxmlLoader.setController(sbViewController);
		lock.root = fxmlLoader.load();
		Platform.runLater(()->{
			((Label)lock.root.getChildren().get(3)).setText(client.client1);
			((Label)lock.root.getChildren().get(5)).setText(client.client2);
		});
		Scene sc = new Scene(lock.root);
		sc.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
		Platform.runLater(()->{
			primaryStage.setScene(sc);
			primaryStage.setTitle("Fighting Plane");
			primaryStage.show();
		});
		ThreadLoadSceneGame thread1 = new ThreadLoadSceneGame(lock, incoming, outPrinter);
		thread1.start();
		
		
	}

}
