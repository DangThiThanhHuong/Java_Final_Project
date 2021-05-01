package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.application.Platform;
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
		this.incoming = incoming;
		this.outPrinter = outPrinter;
		this.client = client;
		this.scene = scene;
		
	}

	public void startGaming() throws IOException {
		Pane root = RootPane.root;
		
		Platform.runLater(()->{
			((Label)root.getChildren().get(3)).setText(client.client1);
			((Label)root.getChildren().get(5)).setText(client.client2);
		});
		
		scene.setRoot(root);
		scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
		Platform.runLater(()->{
			primaryStage.setScene(scene);
			primaryStage.setTitle("Fighting Plane");
			primaryStage.show();
		});
		ThreadLoadSceneGame thread1 = new ThreadLoadSceneGame(lock, incoming, outPrinter);
		thread1.start();
		
		thread2 = new UpdateScene(incoming, root);
		thread2.start();
	}

}
