package server;


import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

public class RootPane {
	static Pane root;
	static double y1 = 0;
	static double y2 = 0;
	
	@SuppressWarnings("static-access")
	public RootPane() {
		FXMLLoader fxmlLoader = new FXMLLoader(
				Server.class.getResource("PlayGame.fxml"));
		PlayGameController sbViewController = new PlayGameController();
		fxmlLoader.setController(sbViewController);
		
		try {
			this.root = fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
