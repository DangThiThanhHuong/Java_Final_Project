package client;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * This class to RunComponent when Client gets message from Server.
 * 
 * @author Huong-Tuan
 *
 */
public class RunComponent {
	ResourceLock lock;
	String string;
	Rectangle rec1;
	Rectangle rec2;
	Stage stage;
	private Timeline explosionAnimation;
	private Timeline CheckAnimationBullet;

	/**
	 * Constructor method.
	 * 
	 * @param lock   ResourceLock class to save flag for synchronized and root to
	 *               working in threads.
	 * @param string (String)Message from client.
	 */
	public RunComponent(ResourceLock lock, String string) {
		super();
		this.string = string;
		this.lock = lock;
	}

	/**
	 * Run() method helps to run each component when each message is send to.
	 */
	public void run() {
		Platform.runLater(() -> {
			try {
				synchronized (lock) {
					while (lock.flag != 3) {
						lock.wait();
					}
//					System.out.println(string);
					String[] array = string.split(",");
					Platform.runLater(() -> {
						switch (array[0].trim()) {
						case "0":
							Platform.runLater(() -> ((ImageView) lock.root.getChildren().get(0))
									.setLayoutY(Double.parseDouble(array[1].trim())));
							break;
						case "1":
							// Restart action
							break;
						case "2":
							// Report action
							break;
						case "3":
							Platform.runLater(() -> {
								((Label) lock.root.getChildren().get(3)).setText(array[1].trim());
							});
							break;
						case "4":
							Platform.runLater(() -> ((ImageView) lock.root.getChildren().get(4))
									.setLayoutY(Double.parseDouble(array[1].trim())));
							break;
						case "5":
							Platform.runLater(() -> {
								((Label) lock.root.getChildren().get(5)).setText(array[1].trim());
							});
							break;
						case "rec1":
							rec1 = new Rectangle(100, 25, 12, 3);
							lock.root.getChildren().add(rec1);
							Platform.runLater(() -> {
								RunBullet(lock.root, ((ImageView) lock.root.getChildren().get(0)),
										((ImageView) lock.root.getChildren().get(4)), rec1,
										Double.parseDouble(array[1].trim()), Double.parseDouble(array[2].trim()),
										Double.parseDouble(array[3].trim()));
							});
							break;
						case "rec2":
							rec2 = new Rectangle(70, 25, 12, 3);
							lock.root.getChildren().add(rec2);
							Platform.runLater(() -> {
								RunBullet(lock.root, ((ImageView) lock.root.getChildren().get(4)),
										((ImageView) lock.root.getChildren().get(0)), rec2,
										Double.parseDouble(array[1].trim()), Double.parseDouble(array[2].trim()),
										Double.parseDouble(array[3].trim()));
							});
							break;
						default:
							break;
						}
					});
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	/**
	 * Method to run an rectangle bullet.Called when Client get message from server
	 * which start with rec1 or rec2.
	 * 
	 * @param pane      Pane Root.
	 * @param enemyPlan ImageView of the plane sent rectangle.
	 * @param otherPlan ImageView of the plane rectangle wants to reach.
	 * @param rec       Rectangle.
	 * @param fromX     Place from X of the rectangle.
	 * @param fromY     Place from Y of the rectangle.
	 * @param toX       Place to X of the rectangle.
	 */
	private void RunBullet(Pane pane, ImageView enemyPlan, ImageView otherPlan, Rectangle rec, double fromX,
			double fromY, double toX) {
		if (enemyPlan.isVisible()) {
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
							checkCollisionBullet(otherPlan, rec, bullet);
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
	}

	/**
	 * Check Collision of Bullet if or not touch the Plane rectangle want to reach.
	 * 
	 * @param a      ImageView of the plane rectangle wants to reach.
	 * @param b      Rectangle;
	 * @param bullet TranslateTransition;
	 */
	private void checkCollisionBullet(ImageView a, Rectangle b, TranslateTransition bullet) {
		if (a.isVisible()) {
			if (a.getBoundsInParent().intersects(b.getBoundsInParent())) {
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
				lock.root.getChildren().addAll(image, image2);
				a.setVisible(false);
				b.setVisible(false);
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
				if (a.getId().equals("planeClient1")) {
					String[] array = ((Label) lock.root.getChildren().get(5)).getText().split(":");
					Platform.runLater(() -> {
						((Label) lock.root.getChildren().get(2)).setText(array[1].trim().toUpperCase() + " WIN !");
					});

				} else {
					String[] array = ((Label) lock.root.getChildren().get(3)).getText().split(":");
					Platform.runLater(() -> {
						((Label) lock.root.getChildren().get(2)).setText(array[1].trim().toUpperCase() + " WIN !");
					});
				}
			}
		}
	}
}
