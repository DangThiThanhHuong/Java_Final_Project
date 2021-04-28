package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

public class Main extends Application {
	Stage primaryStage;
	Pane root;
	final String DB_URL = "jdbc:derby:UserDB";
	ServerSocket socket;
	int connection = 0;

	@Override
	public void start(Stage primaryStage) throws IOException {
		this.primaryStage = primaryStage;
		FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("Login.fxml"));
		LoginController sbViewController = new LoginController();
		fxmlLoader.setController(sbViewController);
		root = fxmlLoader.load();

		Scene sc = new Scene(root);
		sc.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
		primaryStage.setScene(sc);
		primaryStage.setTitle("Server");
		primaryStage.setOnShowing(event -> {
			new Thread(new SocketConnection()).start();
		});
		primaryStage.show();

	}

///////////////////////
	public class SocketConnection implements Runnable {
		@Override
		public void run() {
			Connection conn = null;
			try {
				conn = DriverManager.getConnection(DB_URL);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				socket = new ServerSocket(8189);
				SaveClient client = new SaveClient();
				while (true) {
					Socket incoming = socket.accept();
					Connection connCoppy = conn;
					Runnable runCon = () -> {
						try {
							Thread.sleep(10);
							ResourceLock lock = new ResourceLock();
							InputStream in = incoming.getInputStream();
							OutputStream out = incoming.getOutputStream();
							Scanner scanner = new Scanner(in, "UTF-8");
							PrintWriter outPrinter = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
							ThreadLoadScene thread1 = new ThreadLoadScene(lock, incoming, outPrinter);
							thread1.start();
							while (scanner.hasNext()) {
								String inMes1 = scanner.nextLine();
								String inMes2 = scanner.nextLine();
								String pass = "";
								int highestScore = 0;
								try {
									Statement stmt = connCoppy.createStatement();
									String stringStatement = "SELECT * FROM Users WHERE UserName = '" + inMes1 + "'";
									ResultSet result = stmt.executeQuery(stringStatement);

									if (result.next()) {
										pass = result.getString(2).trim();
										highestScore = result.getInt(3);
									}
									if (pass == "") {
										Platform.runLater(() -> ((Label) root.getChildren().get(5))
												.setText("Account is not exit!"));

									} else {
										if (inMes2.equals(pass)) {
											connection++;
											if(connection==1) 
												client.client1 ="Player1: " + inMes1;
											else {
												client.client2 = "Player1: " + inMes1;
											}
											new Gaming(primaryStage, lock, incoming,
													outPrinter,client).startGaming();
											
										} else
											Platform.runLater(() -> ((Label) root.getChildren().get(5)).setText("Wrong Password!"));
									}
									if (connection == 2)
										connCoppy.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}

						} catch (InterruptedException | IOException e) {
							e.printStackTrace();
						}
					};
					new Thread(runCon).start();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

		}

	}

	public static void main(String[] args) {
		launch(args);
	}
}
