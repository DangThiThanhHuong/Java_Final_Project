package server;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



import javafx.event.ActionEvent;

import javafx.scene.control.Label;

import javafx.scene.control.PasswordField;

public class LoginController {
	@FXML
	private Pane root;
	@FXML
	private TextField userId;
	@FXML
	private PasswordField passId;
	@FXML
	private Button logInId;
	@FXML
	private Label messId;

}
