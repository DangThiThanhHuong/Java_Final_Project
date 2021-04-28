package client;
	
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {
	Socket s;
	File layoutLogin = new File("src/application/LayoutController.java");
	@Override
	public void start(Stage primaryStage) {
		var thread = new Thread(new Runnable() {
			@Override
			public void run() {
					s = new Socket();
					try {
						s.connect(new InetSocketAddress("localhost", 8189), 3000);
						ThreadSceneWorking thread1 = new ThreadSceneWorking(s, primaryStage, layoutLogin);
						thread1.start();
					} catch (IOException e1) {
						System.out.println("Main_e1: "+ e1.getMessage());
					}
			}
		});
		thread.start();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
