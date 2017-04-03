import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TCPMultiClientChat extends Application {

	private static GridPane mainPane;
	private static HBox loginPane;
	private static Label labelLogin;
	private static TextArea te;
	private static TextField tf, tfLogin;
	private static Button connectBt;
	private static Socket clientSocket;
	private static boolean connectStatus = false;
	private static DataOutputStream sendToServer;
	private static DataInputStream inFromServer;
	private static String name;

	private GridPane getPane() {
		loginPane = new HBox();
		labelLogin = new Label();
		labelLogin.setText("Name: ");
		tfLogin = new TextField();
		tfLogin.setEditable(true);

		connectBt = new Button();

		connectBt.setText("Connect");

		loginPane.setAlignment(Pos.CENTER);
		loginPane.setSpacing(20);
		loginPane.getChildren().addAll(labelLogin, tfLogin, connectBt);
		connectBt.setOnAction(new HandlerClass());
 		tfLogin.setOnAction(new HandlerClass());
		mainPane = new GridPane();
		te = new TextArea();
		te.setStyle("-fx-font-size: 25");
		te.setEditable(false);
		tf = new TextField();
		mainPane.setPadding(new Insets(11, 11, 11, 11));
		mainPane.setAlignment(Pos.CENTER);
		mainPane.add(loginPane, 0, 0);
		mainPane.add(tf, 0, 1);
		mainPane.add(te, 0, 2);
		mainPane.setVgap(10);
		tf.setOnAction(e -> {
			try {
				String message = "1[" + name + "] " + tf.getText();
				sendToServer.writeUTF(message + "\n");
				tf.setText("");
			} catch (Exception ex) {
				ex.getMessage();
			}
		});
		mainPane.setId("pane");
		return mainPane;
	}

	static void buttonClicked() {
		connectStatus = !connectStatus;
		if (connectStatus) {
			connectBt.setText("Disconnect");
			name = tfLogin.getText();
			try {
				clientSocket = new Socket("localhost", 6789);
				sendToServer = new DataOutputStream(clientSocket.getOutputStream());
				inFromServer = new DataInputStream(clientSocket.getInputStream());
				tfLogin.setEditable(false);
				new Thread(() -> {
					while (true) {
						if (connectStatus) {
							try {
								te.setText(inFromServer.readUTF());
							} catch (IOException ex) {
								ex.getMessage();
							} catch (Exception ex) {
								ex.getMessage();
							}
						} else {
							break;
						}
					}
				}).start();
			} catch (Exception ex) {
				ex.getMessage();
			}
		} else {
			try {
				sendToServer.writeUTF("0");
				connectBt.setText("Connect");
				tfLogin.setEditable(true);
				clientSocket.close();
			} catch (Exception ex) {
				ex.getMessage();
			}
		}

	}

	@Override
	public void start(Stage primaryStage) {
		Scene scene = new Scene(getPane(), 600, 400);
		scene.getStylesheets().add("file:transparent-text-area.css");
		primaryStage.setTitle("TCP Multi Client Chat App");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

class HandlerClass implements EventHandler<ActionEvent> {
	@Override
	public void handle(ActionEvent e) {
		TCPMultiClientChat.buttonClicked();
	}
	}
}
