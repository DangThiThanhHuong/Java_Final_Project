package server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Class help to save clients's name and a List's Socket of each client.
 * 
 * @author Huong-Tuan
 *
 */
public class SaveClient {
	public volatile String client1;
	public volatile String client2;
	public volatile List<Socket> sockets = new ArrayList<Socket>();
}
