package client;


import java.util.ArrayList;
import java.util.List;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ResourceLock {
	public volatile int flag = 1;
	public volatile Pane root = new Pane();
	//public volatile Pane rootRe = new Pane();
	//public volatile Scene scene = new Scene(root);
}
