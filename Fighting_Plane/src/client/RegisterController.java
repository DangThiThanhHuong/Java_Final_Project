package client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javafx.event.ActionEvent;

import javafx.scene.control.Label;

import javafx.scene.control.PasswordField;

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

	public RegisterController(PrintWriter outPrinterMess, Socket s, Stage stage) {
		this.outPrinterMess = outPrinterMess;
		this.s = s;
		this.stage = stage;
	}

	// Event Listener on Button[#RegisterId].onAction
	@FXML
	public void RegisterAction(ActionEvent event) throws IOException {
		if (userId.getText().isEmpty() || passId.getText().isEmpty())
			messId.setText("UserName Or Passwod can not be NULL");
		else {
			outPrinterMess.println("Register, " + userId.getText() + ", " + passId.getText());

		}
	}

	@FXML
	public void CancelAction(ActionEvent event) throws IOException {
		MoveToLogin(event);
	}

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
