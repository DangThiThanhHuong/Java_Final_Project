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
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class Server extends Application {
	Stage primaryStage;
	Pane root;
	final String DB_URL = "jdbc:derby:UserDB";
	ServerSocket socket;
	int connection = 0;
	Connection conn = null;
	Scene sc;
	Gaming gaming;
	String[] array;

	@Override
	public void start(Stage primaryStage) throws IOException {
		this.primaryStage = primaryStage;
		FXMLLoader fxmlLoader = new FXMLLoader(Server.class.getResource("Login.fxml"));
		LoginController sbViewController = new LoginController();
		fxmlLoader.setController(sbViewController);
		root = fxmlLoader.load();

		//RootPane rp = new RootPane();

		sc = new Scene(root);
		sc.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
		primaryStage.setScene(sc);
		primaryStage.setTitle("Server");
		primaryStage.setOnShowing(event -> {
			new Thread(new SocketConnection()).start();
		});
		primaryStage.show();

	}

	public class SocketConnection implements Runnable {
		@Override
		public void run() {
			try {
				conn = DriverManager.getConnection(DB_URL);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

			try {
				socket = new ServerSocket(8189);
				SaveClient client = new SaveClient();

				while (true) {
					Socket incoming = socket.accept();
					Connection connCoppy = conn;
					ResourceLock lock = new ResourceLock();
					InputStream in = incoming.getInputStream();
					OutputStream out = incoming.getOutputStream();
					Scanner scanner = new Scanner(in, "UTF-8");
					PrintWriter outPrinter = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
					Runnable runCon = () -> {
						try {
							Thread.sleep(10);
							ThreadLoadScene thread1 = new ThreadLoadScene(lock, incoming, outPrinter);
							thread1.start();
							while (scanner.hasNext()) {
								String inMes1 = scanner.nextLine().trim();
								// String inMes2;

								// if (inMes1.equals("tuan") || inMes1.equals("huong")) {

								// inMes2 = scanner.nextLine();
								String pass = "";
								// int highestScore = 0;
								try {
									if (!conn.isClosed()) {
											if (inMes1.contains("- ")) {
												System.out.println("Login : " + inMes1);
												array = inMes1.split("-");
												Statement stmt = connCoppy.createStatement();
												String stringStatement = "SELECT * FROM Users WHERE UserName = '"
														+ array[0].trim() + "'";
												ResultSet result = stmt.executeQuery(stringStatement);

												if (result.next()) {
													pass = result.getString(2).trim();
													// highestScore = result.getInt(3);
												}
												if (pass == "") {
													Platform.runLater(() -> ((Label) root.getChildren().get(5))
															.setText("Account is not exit!"));
													outPrinter.println("Account is not exit!");

												} else {
													if (array[1].trim().equals(pass)) {
														connection++;
														if (connection == 1)
															client.client1 =array[0].trim();
														else{
															client.client2 =array[0].trim();
														}
														gaming = new Gaming(primaryStage, lock, sc, incoming,
																outPrinter, client);
														gaming.startGaming();
													} else {
														Platform.runLater(() -> ((Label) root.getChildren().get(5)).setText("Wrong Password!"));
														outPrinter.println("Wrong Password!");
													}
												}
										
									} 
											
									if (connection == 2)
										connCoppy.close();
									}
								} catch (SQLException e) {
									e.printStackTrace();
								}
								if(client.client1!=null && client.client2!=null)
									outPrinter.write("3, Player1: "+client.client1);
									outPrinter.println();
									outPrinter.write("5, Player2: "+client.client2);
									outPrinter.println();
									gaming.playGaming(inMes1);
							}

						} catch (InterruptedException | IOException e) {
							e.printStackTrace();
						}
					};
					new Thread(runCon).start();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
