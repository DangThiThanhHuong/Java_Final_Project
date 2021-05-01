package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class UpdateScene extends Thread {
	Socket s;
	Pane root;
	String responseLine;
	double y1;
	double y2;

	private Rectangle rec1;
	private Rectangle rec2;
	private Timeline CheckAnimationBullet;
	private Timeline explosionAnimation;

	public UpdateScene(Socket s, Pane root) {
		this.s = s;
		this.root = root;
	}

	@Override
	public void run() {
		System.out.println("Update scene start");

		try {

			BufferedReader is = new BufferedReader(new InputStreamReader(s.getInputStream()));
			while ((responseLine = is.readLine()) != null) {
				String client = responseLine.split(",")[0];

				rec1 = new Rectangle(70, 310, 12, 3);
				rec2 = new Rectangle(70, 310, 12, 3);
				Platform.runLater(() -> {
					if (client.equalsIgnoreCase("client1")) {
						String info = responseLine.split(",")[1];
						try {
							y1 = Double.parseDouble(info);
							RootPane.y1 = y1;
							root.getChildren().get(0).setLayoutY(RootPane.y1);
						} catch (Exception e) {
							RunBullet(root, (ImageView) root.getChildren().get(4), rec1, 10, y1 - 300, 1300);
						}
					}
					if (client.equalsIgnoreCase("client2")) {
						String info = responseLine.split(",")[1];
						try {
							y2 = Double.parseDouble(info);
							RootPane.y2 = y2;
							root.getChildren().get(4).setLayoutY(RootPane.y2);
						} catch (Exception e) {
							RunBullet(root, (ImageView) root.getChildren().get(0), rec2, 1015, y2 - 300, -500);
						}
					}

				});
			}

		} catch (IOException ex) {
			Platform.exit();
		}
	}

	private void RunBullet(Pane pane, ImageView enemyPlan, Rectangle rec, double fromX, double fromY, double toX) {

		pane.getChildren().add(rec);
		rec.setFill(Color.DARKRED);

		TranslateTransition bullet = new TranslateTransition(Duration.seconds(1), rec);
		Rectangle copy = rec;
		Platform.runLater(() -> {
			Runnable task = () -> {
				try {
					bullet.setFromY(fromY);
					bullet.setFromX(fromX);
					bullet.setToX(toX);
					bullet.setNode(copy);
					bullet.playFromStart();

					CheckAnimationBullet = new Timeline(new KeyFrame(new Duration(0.1), t -> {
						checkCollisionBullet(enemyPlan, rec, bullet);
					}));
					CheckAnimationBullet.setCycleCount(Timeline.INDEFINITE);
					CheckAnimationBullet.playFromStart();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			};
			new Thread(task).start();
		});
	}

	private void checkCollisionBullet(ImageView a, Rectangle b, TranslateTransition bullet) {
//		System.out.println("check");
		if (a.getBoundsInParent().intersects(b.getBoundsInParent())) {
			System.out.println("pum");
//			if(a.isVisible() == true && b.isVisible() == true) {
//				score++;
//				scoreId.setText(""+score);
			Image im = new Image("explosion2.png");
			ImageView image = new ImageView();
			image.setImage(im);
			image.setFitWidth(100);
			image.setPreserveRatio(true);
			image.setSmooth(true);
			image.setCache(true);
			image.setX(b.getBoundsInParent().getMinX() - 50);
			image.setY(b.getBoundsInParent().getMinY() - 50);
			image.setVisible(true);

			Image im2 = new Image("explosion3.png");
			ImageView image2 = new ImageView();
			image2.setImage(im2);
			image2.setFitWidth(100);
			image2.setPreserveRatio(true);
			image2.setSmooth(true);
			image2.setCache(true);
			image2.setX(b.getBoundsInParent().getMinX() - 50);
			image2.setY(b.getBoundsInParent().getMinY() - 50);
			image2.setVisible(false);
			root.getChildren().addAll(image, image2);
			a.setVisible(false);
			b.setVisible(false);
//				for(TranslateTransition ob:obstacles) {
//			if (bullet.getNode() == a)
//				bullet.stop();
//				}
			explosionAnimation = new Timeline(new KeyFrame(new Duration(100.0), t -> {
				if (image.isVisible() && !image2.isVisible()) {
					image.setVisible(false);
					image2.setVisible(true);
				} else
					image2.setVisible(false);
			}));
			b.setLayoutY(1000);
			explosionAnimation.setCycleCount(2);
			explosionAnimation.playFromStart();
		}
	}

}
