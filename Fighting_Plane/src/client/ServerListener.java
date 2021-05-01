package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.scene.input.KeyCode;

class ServerListener extends Thread {
	Socket s = new Socket();
	double y = 100;
	BufferedWriter os;
	BufferedReader is;
	ResourceLock lock;
	String name;

	public ServerListener(Socket s, ResourceLock lock, String name) {
		this.s = s;
		this.lock = lock;
		this.name = name;
	}

	@Override
	public void run() {
		try {
			os = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			is = new BufferedReader(new InputStreamReader(s.getInputStream()));

			while (s.isConnected()) {
				
				lock.scene.setOnKeyPressed(k -> {
					KeyCode key = k.getCode();

					switch (key) {
					case SPACE:
						try {
							os.write(this.name + ",fire");
							os.newLine();
							os.flush();
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					case UP:
//						if (lock.root.getChildren().get(0).getTranslateY() >= -300) {
							y -= 10;
							try {
								os.write(this.name + "," + String.valueOf(y));
								os.newLine();
								os.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
//						}
						break;
					case DOWN:
//						if (lock.root.getTranslateY() <= 400) {
							y += 10;
							try {
								os.write(this.name + "," + String.valueOf(y));
								os.newLine();
								os.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
//						}
						break;
					default:
						break;
					}
				});
			}
			os.close();
			is.close();
			s.close();
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + s);
			return;
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to " + s);
			return;
		}
	}
}
