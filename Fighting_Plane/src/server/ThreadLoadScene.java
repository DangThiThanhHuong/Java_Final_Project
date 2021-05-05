package server;

import java.io.File;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Thread's Class Scan LoginController and Login(FXML)and sent to Clients.
 * 
 * @author Huong-Tuan
 *
 */
public class ThreadLoadScene extends Thread {
	ResourceLock lock;
	Socket incoming;
	PrintWriter outPrinter;

	/**
	 * Constructor method
	 * 
	 * @param lock       ResourceLock class to save flag for synchronized and root
	 *                   to working in threads.
	 * @param incoming   Socket.
	 * @param outPrinter PrintWriter helps to sent message to clients.
	 */
	ThreadLoadScene(ResourceLock lock, Socket incoming, PrintWriter outPrinter) {
		this.lock = lock;
		this.incoming = incoming;
		this.outPrinter = outPrinter;
	}

	/**
	 * Method Run() of the thread class which works on Scan LoginController and
	 * Login(FXML)and sent to Clients.
	 */
	@Override
	public void run() {
		try {
			synchronized (lock) {
				while (lock.flag != 1) {
					lock.wait();
				}
				Scanner sc = new Scanner(new File("src/server/Login.fxml"));
				Scanner sc2 = new Scanner(new File("src/server/LoginController.java"));
				StringBuilder sb = new StringBuilder();
				StringBuilder sb2 = new StringBuilder();

				while (sc.hasNext()) {
					sb.append(sc.nextLine() + "\n");
				}
				sc2.nextLine();
				sb2.append("package client;\n");
				while (sc2.hasNext()) {
					sb2.append(sc2.nextLine() + "\n");
				}
				outPrinter.write(sb.toString());
				outPrinter.write("DONE \n");
				outPrinter.write(sb2.toString());
				outPrinter.write("FINISHED");
				outPrinter.println();
				Thread.sleep(10);
				lock.flag = 2;
				lock.notifyAll();
				sc.close();
				sc2.close();
			}
		} catch (Exception e) {
			System.out.println("Exception 1 :" + e.getMessage());
		}
	}
}
