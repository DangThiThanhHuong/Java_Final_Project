package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

class ServerListener extends Thread {
	Socket s;
	//double y = 0;
	BufferedWriter os;
	BufferedReader is;
	ResourceLock lock;
	Stage stage;

	public ServerListener(Socket s, ResourceLock lock, Stage stage) {
		this.s = s;
		this.lock = lock;
		this.stage = stage;
	}

	@Override
	public void run() {
		try {
			os = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			is = new BufferedReader(new InputStreamReader(s.getInputStream()));

			while (s.isConnected()) {
				
				stage.getScene().setOnKeyPressed(k -> {
					KeyCode key = k.getCode();

					switch (key) {
					case SPACE:
						try {
							os.write(this.stage.getTitle() + ",fire");
							os.newLine();
							os.flush();
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case UP:
							try {
								os.write(this.stage.getTitle() + "," + String.valueOf(-10));
								os.newLine();
								os.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
						break;
					case DOWN:
							try {
								os.write(this.stage.getTitle()  + "," + String.valueOf(+10));
								os.newLine();
								os.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
						break;
					default:
						break;
					}
				});
			}
			os.close();
			is.close();
			//s.close();
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + s);
			return;
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + s);
			return;
		}
	}
}
