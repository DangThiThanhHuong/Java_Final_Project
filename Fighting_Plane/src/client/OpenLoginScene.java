package client;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * This class helps to open an Login Scene for client to access.
 * 
 * @author Huong-Tuan
 *
 */
public class OpenLoginScene {
	ResourceLock lock;
	Stage stage;
	URL fileUrl;
	PrintWriter outPrinterMess;
	Label labelRegister;

	/**
	 * Constructor method.
	 * 
	 * @param lock           ResourceLock class to save flag for synchronized and
	 *                       root to working in threads.
	 * @param stage          Stage
	 * @param fileUrl        URL of LoginController file
	 * @param outPrinterMess PrintWriter to send message to Client
	 * @param labelRegister  Label of Register appear if the user is not in Data
	 */
	public OpenLoginScene(ResourceLock lock, Stage stage, URL fileUrl, PrintWriter outPrinterMess,
			Label labelRegister) {
		super();
		this.lock = lock;
		this.stage = stage;
		this.fileUrl = fileUrl;
		this.outPrinterMess = outPrinterMess;
		this.labelRegister = labelRegister;
	}

	/**
	 * Method Load Login Scene
	 */

	public void loadTheScene() {
		Platform.runLater(() -> {
			try {
				synchronized (lock) {
					while (lock.flag != 1) {
						lock.wait();
					}
					FXMLLoader fxmlLoader = new FXMLLoader(fileUrl);
					File controller = new File("src/application/LoginController.java");
					fxmlLoader.setController(controller);
					lock.root = fxmlLoader.load();
					Scene scene = new Scene(lock.root);
					scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
					stage.setScene(scene);
					stage.setTitle("CLIENT");
					stage.show();
					lock.flag = 2;
					lock.notifyAll();
					Platform.runLater(() -> {
						((Button) lock.root.getChildren().get(4)).setOnAction(e -> {
							if (((TextField) lock.root.getChildren().get(2)).getText().isEmpty()) {
								((Label) lock.root.getChildren().get(5)).setText("UserName can not be NULL");
								if (lock.root.getChildren().contains(labelRegister))
									Platform.runLater(() -> lock.root.getChildren().remove(labelRegister));
							} else if (((PasswordField) lock.root.getChildren().get(3)).getText().isEmpty()) {
								((Label) lock.root.getChildren().get(5)).setText("Password can not be NULL");
								if (lock.root.getChildren().contains(labelRegister))
									Platform.runLater(() -> lock.root.getChildren().remove(labelRegister));
							} else {
								String login = ((TextField) lock.root.getChildren().get(2)).getText() + "- "
										+ ((PasswordField) lock.root.getChildren().get(3)).getText();
								outPrinterMess.println(login);
								stage.setTitle(((TextField) lock.root.getChildren().get(2)).getText());

							}
						});
					});
				}
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		});
	}

}
