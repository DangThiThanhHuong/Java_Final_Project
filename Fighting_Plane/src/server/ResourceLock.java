package server;

import javafx.scene.layout.Pane;

/**
 * Class to save a flag for synchronized and root to working in threads.
 * 
 * @author Huong-Tuan
 *
 */
public class ResourceLock {
	public volatile int flag = 1;
	public volatile Pane root;
}
