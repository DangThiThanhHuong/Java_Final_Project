package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

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
import javafx.util.Duration;

/**
 * Thread's Class helps to Update PlayGame scene for Server and send back to
 * Clients.
 * 
 * @author Huong-Tuan
 *
 */
public class UpdateScene extends Thread {
	Socket s;
	Pane root;
	SaveClient client;
	PrintWriter outPrinter;
	String request;
	double y1;
	double y2;
	int count = 0;
	private Rectangle rec1;
	private Rectangle rec2;
	private Timeline CheckAnimationBullet;
	private Timeline explosionAnimation;

	/**
	 * Constructor method.
	 * 
	 * @param s          Socket.
	 * @param root       Pane Root.
	 * @param client     SaveClient helps saving clients' name and list's Socket of
	 *                   each client.
	 * @param outPrinter PrintWriter helps send message to clients.
	 * @param request    (String) a message get from each client.
	 */
	public UpdateScene(Socket s, Pane root, SaveClient client, PrintWriter outPrinter, String request) {
		this.s = s;
		this.root = root;
		this.client = client;
		this.request = request;
		this.outPrinter = outPrinter;
	}

	/**
	 * Method Run() of the thread class helps update game Scene and sent back to
	 * clients.
	 */
	@Override
	public void run() {
		try {
/////////////////////////Update Server/////////////////////
			Runnable run = () -> {
				try {
					Thread.sleep(10);
					String getClient = request.split(",")[0];
					rec1 = new Rectangle(100, 25, 12, 3);
					rec2 = new Rectangle(70, 25, 12, 3);
					if (getClient.equalsIgnoreCase(client.client1)) {
						String info = request.split(",")[1];

						try {
							y1 = Double.parseDouble(info);
							Double getY = root.getChildren().get(0).getLayoutY() + y1;
							Platform.runLater(() -> {
								root.getChildren().get(0).setLayoutY(getY);
							});

						} catch (Exception e) {
							Platform.runLater(() -> {
								RunBullet(root, (ImageView) root.getChildren().get(0),
										(ImageView) root.getChildren().get(4), rec1, 10,
										root.getChildren().get(0).getLayoutY(), 1300);
							});
						}
					}
					if (getClient.equalsIgnoreCase(client.client2)) {
						String info = request.split(",")[1];
						try {
							y2 = Double.parseDouble(info);

							Double getY = root.getChildren().get(4).getLayoutY() + y2;
							Platform.runLater(() -> {
								root.getChildren().get(4).setLayoutY(getY);
							});

						} catch (Exception e) {
							Platform.runLater(() -> {
								RunBullet(root, (ImageView) root.getChildren().get(4),
										(ImageView) root.getChildren().get(0), rec2, 1015,
										root.getChildren().get(4).getLayoutY(), -500);
							});

						}
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			};
			new Thread(run).start();
/////////////////////////send to clients/////////////////////
			for (Socket s : client.sockets) {
				InputStream in = s.getInputStream();
				OutputStream out = s.getOutputStream();
				PrintWriter outPrinter = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
				outPrinter.write("3, Player1: " + client.client1);
				outPrinter.println();
				outPrinter.write("5, Player2: " + client.client2);
				outPrinter.println();

				Runnable run2 = () -> {
					try {
						Thread.sleep(10);
						String getClient = request.split(",")[0];
						if (getClient.equalsIgnoreCase(client.client1)) {
							String info = request.split(",")[1];
							try {
								y1 = Double.parseDouble(info);
								Double getY = root.getChildren().get(0).getLayoutY() + y1;
								outPrinter.write("0," + getY);
								outPrinter.println();

							} catch (Exception e) {
								outPrinter.write("rec1, 10, " + root.getChildren().get(0).getLayoutY() + " , 1300");
								outPrinter.println();
							}
						}
						if (getClient.equalsIgnoreCase(client.client2)) {
							String info = request.split(",")[1];
							try {
								y2 = Double.parseDouble(info);
								Double getY = root.getChildren().get(4).getLayoutY() + y2;
								outPrinter.write("4," + getY);
								outPrinter.println();

							} catch (Exception e) {
								outPrinter.write("rec2, 1015, " + root.getChildren().get(4).getLayoutY() + " ,-500");
								outPrinter.println();
							}
						}

					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				};
				new Thread(run2).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		TranslateTransition bullet = new TranslateTransition(Duration.seconds(1), rec);
		if (enemyPlan.isVisible()) {
			root.getChildren().add(rec);
			rec.setFill(Color.DARKRED);
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
				System.out.println("pum");
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
					Platform.runLater(() -> {
						((Label) root.getChildren().get(2)).setText(client.client2.toUpperCase() + " WIN !");
					});

				} else {
					Platform.runLater(() -> {
						((Label) root.getChildren().get(2)).setText(client.client1.toUpperCase() + " WIN !");
					});
				}

			}

		}

	}

}
