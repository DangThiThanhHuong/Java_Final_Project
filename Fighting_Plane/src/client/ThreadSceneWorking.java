package client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Thread's class for SceneWorking helps to load scenes from serve.
 * 
 * @author Huong-Tuan
 *
 */
public class ThreadSceneWorking extends Thread {
	File file, file2, file3, file4, file5, file6;
	Socket s;
	Stage stage;
	Label labelRegister = new Label();;
	ResourceLock lock = new ResourceLock();

	/**
	 * Constructor method
	 * 
	 * @param s     Socket
	 * @param stage Stage
	 */
	public ThreadSceneWorking(Socket s, Stage stage) {
		this.s = s;
		this.stage = stage;
	}

	/***
	 * Method run() of the thread helps get message from client to load the Login
	 * scene and Game scene.
	 */
	@Override
	public void run() {
		try {
			Thread.sleep(10);
			try (Scanner in = new Scanner(s.getInputStream(), "UTF-8")) {
				OutputStream outStream = null;
				OutputStream outStream2 = null;
				OutputStream outStream3 = null;
				OutputStream outStream4 = null;
				OutputStream out = s.getOutputStream();
				PrintWriter outPrinterMess = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
				file = new File("src/client/Login.fxml");
				file2 = new File("src/client/LoginController.java");
				file3 = new File("src/client/PlayGame.fxml");
				file4 = new File("src/client/PlayGameController.java");
				try {
					file.createNewFile();
					outStream = new FileOutputStream(file);
					PrintWriter outPrinter = new PrintWriter(new OutputStreamWriter(outStream, "UTF-8"), true);
					////////////////////////////// Print Login &
					////////////////////////////// LoginController////////////////////////
					while (in.hasNext()) {
						String string = in.nextLine();
						if (!string.contains("DONE")) {
							outPrinter.println(string);
						} else {
							outPrinter.close();
							file2.createNewFile();
							outStream2 = new FileOutputStream(file2);
							PrintWriter outPrinter2 = new PrintWriter(new OutputStreamWriter(outStream2, "UTF-8"),
									true);
							while (in.hasNext()) {
								String string2 = in.nextLine();
								if (!string2.contains("FINISHED")) {
									outPrinter2.println(string2);
								} else {
									outPrinter2.close();
									URL fileUrl = file.toURL();
									new OpenLoginScene(lock, stage, fileUrl, outPrinterMess, labelRegister)
											.loadTheScene();

									////////////////////////////// Print PlayGame &
									////////////////////////////// PlayGamController////////////////////////
									file3.createNewFile();
									outStream3 = new FileOutputStream(file3);
									PrintWriter outPrinter3 = new PrintWriter(
											new OutputStreamWriter(outStream3, "UTF-8"), true);
									while (in.hasNext()) {
										String string3 = in.nextLine();
										////////////////////////////////////////// register/////////////////////////////////
										if (string3.equals("Account is not exit!")) {
											labelRegister.setText("Register Now!");
											labelRegister.setTextFill(Color.BLUE);
											labelRegister.setFont(new Font("System Bold Italic", 24));
											labelRegister.setLayoutX(2);
											labelRegister.setLayoutY(220);
											labelRegister.setPrefHeight(27);
											labelRegister.setPrefWidth(1200);
											labelRegister.setAlignment(Pos.CENTER);
											labelRegister.setUnderline(true);

											Platform.runLater(() -> {
												((Label) lock.root.getChildren().get(5)).setText(string3);
												lock.root.getChildren().add(6, labelRegister);
												((Label) lock.root.getChildren().get(6)).setOnMouseClicked(e -> {
													FXMLLoader fxmlLoader;
													try {
														fxmlLoader = new FXMLLoader(
																new File("src/client/Register.fxml").toURL());
														RegisterController sbViewController = new RegisterController(
																outPrinterMess, s, stage);
														fxmlLoader.setController(sbViewController);
														lock.root = fxmlLoader.load();
														lock.root.autosize();
														Scene sc = new Scene(lock.root);
														sc.getStylesheets().addAll(this.getClass()
																.getResource("style.css").toExternalForm());
														stage.setTitle("Register");
														stage.setScene(sc);
													} catch (IOException e2) {
														// TODO Auto-generated catch block
														e2.printStackTrace();
													}

												});
											});

										}
										if (string3.equals("UserName is Existing, Please choose another one!")) {
											System.out.println(lock.root.getChildren().toString());
											Platform.runLater(() -> ((Label) lock.root.getChildren().get(4))
													.setText("UserName is Existing, Please choose another one!"));
										}
										if (string3.equals("Register Successfull")) {
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
										/////////////////////////////////////////////////////////////////
										else if (string3.equals("Wrong Password!")) {
											Platform.runLater(
													() -> ((Label) lock.root.getChildren().get(5)).setText(string3));
											if (lock.root.getChildren().contains(labelRegister))
												Platform.runLater(() -> lock.root.getChildren().remove(labelRegister));
										} else if (string3.equals("User have been used!")) {
											Platform.runLater(
													() -> ((Label) lock.root.getChildren().get(5)).setText(string3));
											if (lock.root.getChildren().contains(labelRegister))
												Platform.runLater(() -> lock.root.getChildren().remove(labelRegister));
										} else if (!string3.contains("DONE_PLAYGAME")) {
											outPrinter3.println(string3);
										} else {
											outPrinter3.close();
											file4.createNewFile();
											outStream4 = new FileOutputStream(file4);
											PrintWriter outPrinter4 = new PrintWriter(
													new OutputStreamWriter(outStream4, "UTF-8"), true);
											while (in.hasNext()) {
												String string4 = in.nextLine().trim();

												if (!string4.contains("FINISHED_PLAYGAME")) {
													outPrinter4.println(string4);
												} else {
													outPrinter4.close();
													URL fileUrl2 = file3.toURL();
													new OpenPlayGameScene(lock, stage, fileUrl2).loadTheScene();
													stage.setOnCloseRequest(
															(EventHandler<WindowEvent>) new EventHandler<WindowEvent>() {
																public void handle(WindowEvent we) {
																	System.out.println("Stage is closing");
																	outPrinterMess.println("BYE, " + stage.getTitle());
																}
															});
													
													ServerListener threadListenr = new ServerListener(s, lock, stage);
													threadListenr.start();
													while (in.hasNext()) {
														String serveRespone = in.nextLine();
														new RunComponent(lock, serveRespone).run();
													}
												}
											}
										}
									}
								}
							}
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
				in.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
