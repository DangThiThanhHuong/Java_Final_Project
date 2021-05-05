package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Class opens Game scene and runs game whenever client send message.
 * 
 * @author Huong-Tuan
 *
 */
public class Gaming {
	Stage primaryStage;
	ResourceLock lock;
	Socket incoming;
	PrintWriter outPrinter;
	SaveClient client;
	Scene scene;
	UpdateScene thread2;

	/**
	 * Constructor method.
	 * 
	 * @param primaryStage Stage
	 * @param lock         ResourceLock class to save flag for synchronized and root
	 *                     to working in threads.
	 * @param scene        Scene
	 * @param incoming     Socket
	 * @param outPrinter   PrintWriter to sent message to clients
<<<<<<< HEAD
	 * @param client       SaveClient helps saving clients' name and list Clients.
=======
	 * @param client       SaveClient helps saving clients' name and list's Socket of each client.
>>>>>>> branch 'huong_Register' of https://github.com/DangThiThanhHuong/Java_Final_Project.git
	 */
	public Gaming(Stage primaryStage, ResourceLock lock, Scene scene, Socket incoming, PrintWriter outPrinter,
			SaveClient client) {
		super();
		this.primaryStage = primaryStage;
		this.lock = lock;
		this.scene = scene;
		this.incoming = incoming;
		this.outPrinter = outPrinter;
		this.client = client;
	}

	/**
	 * Method to open the GameScene
	 * 
	 * @throws IOException
	 */
	public void startGaming() throws IOException {
		ThreadLoadSceneGame thread1 = new ThreadLoadSceneGame(lock, incoming, outPrinter);
		thread1.start();
		System.out.println("Start gamming");
		FXMLLoader fxmlLoader = new FXMLLoader(Server.class.getResource("PlayGame.fxml"));
		PlayGameController sbViewController = new PlayGameController();
		fxmlLoader.setController(sbViewController);
		lock.root = fxmlLoader.load();
		Platform.runLater(() -> {
			((Label) lock.root.getChildren().get(3)).setText("Player1: " + client.client1);
			((Label) lock.root.getChildren().get(5)).setText("Player2: " + client.client2);
			scene.setRoot(lock.root);
			scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Fighting Plane");
			primaryStage.show();
		});

	}

	/**
	 * Method calls UpdateScene class to update the scene of Server when client
	 * actions and send back to clients.
	 * 
<<<<<<< HEAD
	 * @param request
=======
	 * @param request (String) message from clients
>>>>>>> branch 'huong_Register' of https://github.com/DangThiThanhHuong/Java_Final_Project.git
	 */
	public void playGaming(String request) {
		new UpdateScene(lock, incoming, lock.root, client, outPrinter, request).start();
	}
}
