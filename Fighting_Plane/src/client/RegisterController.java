package client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

/**
 * Register Controller class
 * 
 * @author Huong-Tuan
 *
 */
public class RegisterController {
	@FXML
	private Pane rootRegis;
	@FXML
	private Button RegisterId;
	@FXML
	private TextField userId;
	@FXML
	private Label messId;
	@FXML
	private PasswordField passId;
	@FXML
	private Button cancelId;
	PrintWriter outPrinterMess;
	Socket s;
	Stage stage;

	/**
	 * Constructor method.
	 * 
	 * @param outPrinterMess PrintWriter to sent message to Server
	 * @param s              Socket
	 * @param stage          Stage
	 */
	public RegisterController(PrintWriter outPrinterMess, Socket s, Stage stage) {
		this.outPrinterMess = outPrinterMess;
		this.s = s;
		this.stage = stage;
	}

	/**
	 * Event Listener on Button[#RegisterId].onAction
	 * 
	 * @param event ActionEvent
	 * @throws IOException
	 */
	@FXML
	public void RegisterAction(ActionEvent event) throws IOException {
		if (userId.getText().isEmpty() || passId.getText().isEmpty())
			messId.setText("UserName Or Passwod can not be NULL");
		else {
			outPrinterMess.println("Register, " + userId.getText() + ", " + passId.getText());

		}
	}

	/**
	 * Event Listener on Button[#cancelId].onAction
	 * 
	 * @param event ActionEvent
	 * @throws IOException
	 */
	@FXML
	public void CancelAction(ActionEvent event) throws IOException {
		MoveToLogin(event);
	}

	/**
	 * Method helps moving to Login Scene
	 * 
	 * @param event ActionEvent
	 * @throws IOException
	 */
	private void MoveToLogin(ActionEvent event) throws IOException {
		var thread = new Thread(new Runnable() {
			@Override
			public void run() {
				s = new Socket();
				try {
					s.connect(new InetSocketAddress("localhost", 8189), 3000);
					ThreadSceneWorking thread1 = new ThreadSceneWorking(s, stage);
					thread1.start();

				} catch (IOException e1) {
					System.out.println("Main_e1: " + e1.getMessage());
				}
			}
		});
		thread.start();
	}
}
