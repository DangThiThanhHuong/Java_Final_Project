package server;

import javafx.scene.layout.Pane;

public class ResourceLock {
	public volatile int flag = 1;
	public volatile Pane root;	
}
