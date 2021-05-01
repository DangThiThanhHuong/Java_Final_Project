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
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ThreadSceneWorking extends Thread {
	File file, file2, file3, file4;
	Socket s;
	Stage stage;
	File controller;
	String client;
	public static ResourceLock lock = new ResourceLock();

	public ThreadSceneWorking(Socket s, Stage stage, File controller, String client) {
		this.s = s;
		this.stage = stage;
		this.controller = controller;
		this.client = client;
	}

	@Override
	public void run() {
		System.out.println("Scene Working start");
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
						
						if (!string.equalsIgnoreCase("DONE"))
						
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
									new OpenLoginScene(lock, stage, fileUrl, outPrinterMess).loadTheScene();

									////////////////////////////// Print PlayGame &
									////////////////////////////// PlayGamController////////////////////////
									file3.createNewFile();
									outStream3 = new FileOutputStream(file3);
									PrintWriter outPrinter3 = new PrintWriter(
											new OutputStreamWriter(outStream3, "UTF-8"), true);
									while (in.hasNext()) {
										String string3 = in.nextLine();
										if (!string3.contains("DONE_PLAYGAME")) {
											outPrinter3.println(string3);
										} else {
											outPrinter3.close();
											file4.createNewFile();
											outStream4 = new FileOutputStream(file4);
											PrintWriter outPrinter4 = new PrintWriter(
													new OutputStreamWriter(outStream4, "UTF-8"), true);
											while (in.hasNext()) {
												String string4 = in.nextLine();
												if (!string4.contains("FINISHED_PLAYGAME")) {
													outPrinter4.println(string4);
												} else {
													outPrinter4.close();
													URL fileUrl2 = file3.toURL();
													new OpenPlayGameScene(lock, stage, fileUrl2).loadTheScene();
													
//													Socket sock = new Socket();
//													sock.connect(new InetSocketAddress("localhost", 8189), 3000);
													ServerListener threadListenr = new ServerListener(s, lock, client);
													threadListenr.start();
													
													while (in.hasNext()) {
														Platform.runLater(() -> {
															((Label) lock.root.getChildren().get(3)).setText(in.nextLine());
															((Label) lock.root.getChildren().get(5)).setText(in.nextLine());
														});

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
