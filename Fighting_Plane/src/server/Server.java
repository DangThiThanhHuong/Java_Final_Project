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
	String[] array2;
	@Override
	public void start(Stage primaryStage) throws IOException {
		this.primaryStage = primaryStage;
		FXMLLoader fxmlLoader = new FXMLLoader(Server.class.getResource("Login.fxml"));
		LoginController sbViewController = new LoginController();
		fxmlLoader.setController(sbViewController);
		root = fxmlLoader.load();
		// RootPane rp = new RootPane();
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
					client.sockets.add(incoming);
					Connection connCoppy = conn;
					Statement stmt = connCoppy.createStatement();
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
							boolean done = false;
							while (!done && scanner.hasNext()) {
								String inMes1 = scanner.nextLine().trim();
								String pass = "";
								Boolean status = false;
								try {
									if (!conn.isClosed()) {
										if (inMes1.contains("- ")) {
											System.out.println("Login : " + inMes1);
											array = inMes1.split("-");

											String stringStatement = "SELECT * FROM Users WHERE UserName = '"
													+ array[0].trim() + "'";
											ResultSet result = stmt.executeQuery(stringStatement);

											if (result.next()) {
												pass = result.getString(2).trim();
												status = result.getBoolean(3);
												System.out.println(status);
											}
											if (pass == "") {
												Platform.runLater(() -> ((Label) root.getChildren().get(5))
														.setText("Account is not exit!"));
												outPrinter.println("Account is not exit!");

											} else {
												if (array[1].trim().equals(pass) && status == false) {
													connection++;
													String stringStatement2 = "UPDATE Users SET Status = true WHERE UserName = '"
															+ array[0].trim() + "'";
													int resultUpdate = stmt.executeUpdate(stringStatement2);
													if (resultUpdate == 1)
														System.out.println("Update Status Successful");
													System.out.println();
													if (connection == 1)
														client.client1 = array[0].trim();
													else {
														client.client2 = array[0].trim();
													}
													gaming = new Gaming(primaryStage, lock, sc, incoming, outPrinter,
															client);
													gaming.startGaming();
													if (connection == 2)
														connection++;
													
												} else if (status == true) {
													Platform.runLater(() -> ((Label) root.getChildren().get(5))
															.setText("User have been used!"));
													outPrinter.println("User have been used!");
												} else {
													Platform.runLater(() -> ((Label) root.getChildren().get(5))
															.setText("Wrong Password!"));
													outPrinter.println("Wrong Password!");
												}
											}

										}
										if(inMes1.contains("Register, ")) {
											int resultRe = 0;
											String[] arrayRe = inMes1.split(",");
											String stringStatement = "INSERT INTO Users VALUES ('" + arrayRe[1].trim() + "','" + arrayRe[2].trim() + "',false)";
											String checkPrimaryStatement = "Select * FROM Users WHERE UserName= '" + arrayRe[1].trim() + "'";
											ResultSet resultCheck = stmt.executeQuery(checkPrimaryStatement);
											if(resultCheck.next()) {
												outPrinter.println("UserName is Existing, Please choose another one!");
											}
											else
												resultRe = stmt.executeUpdate(stringStatement);
											if(resultRe==1) {
												outPrinter.println("Register Successfull");
											}
										}
										if (inMes1.contains("BYE")) {
											String[] array = inMes1.split(",");
											System.out.println(array[1].trim());
											done = true;
											incoming.close();
											connection--;
											String stringStatement3 = "UPDATE Users SET Status = false WHERE UserName = '"
													+ array[1].trim() + "'";
											int resultUpdateFinal1 = stmt.executeUpdate(stringStatement3);
											System.out.println("Update database to close!");

											Platform.runLater(() -> {
												try {
													if (connection == 0)
														start(primaryStage);
													if (socket.isClosed())
														connCoppy.close();
												} catch (Exception e) {
													e.printStackTrace();
												}
											});
										}
										if (client.client1 != null && client.client2 != null && connection == 3) {
											System.out.println("inMes1" + inMes1);
											gaming.playGaming(inMes1);
										}
									}
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

			} catch (IOException | SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
