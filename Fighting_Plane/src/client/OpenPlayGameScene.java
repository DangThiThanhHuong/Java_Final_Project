package client;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class OpenPlayGameScene {
	ResourceLock lock = new ResourceLock();
	Stage stage;
	URL fileUrl;

	public OpenPlayGameScene(ResourceLock lock, Stage stage, URL fileUrl) {
		super();
		this.lock = lock;
		this.stage = stage;
		this.fileUrl = fileUrl;
	}

	public void loadTheScene() {
		Platform.runLater(() -> {
			try {
				synchronized (lock) {
					while (lock.flag != 2) {
						lock.wait();
					}
					FXMLLoader fxmlLoader = new FXMLLoader(fileUrl);
					File controller = new File("src/application/PlayGameController.java");
					fxmlLoader.setController(controller);
					lock.root = fxmlLoader.load();
					lock.scene = new Scene(lock.root);
					
					lock.scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
					stage.setScene(lock.scene);
					stage.setTitle("CLIENT_1");
					stage.show();
					lock.flag = 3;
					lock.notifyAll();
				}
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		});
	}
}
