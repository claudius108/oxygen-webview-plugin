package ro.kuberam.oxygen.webview;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class SegmentedButtonBarApp extends Application {
	@Override
	public void start(Stage stage) throws Exception {
		BorderPane root = new BorderPane();
		root.setId("background");

		ToolBar toolBar = new ToolBar();
		root.setTop(toolBar);

		Region spacer = new Region();
		spacer.getStyleClass().setAll("spacer");

		HBox buttonBar = new HBox();
		buttonBar.getStyleClass().setAll("segmented-button-bar");
		Button sampleButton = new Button("Tasks");
		sampleButton.getStyleClass().addAll("first");
		Button sampleButton2 = new Button("Administrator");
		Button sampleButton3 = new Button("Search");
		Button sampleButton4 = new Button("Line");
		Button sampleButton5 = new Button("Process");
		sampleButton5.getStyleClass().addAll("last", "capsule");
		buttonBar.getChildren().addAll(sampleButton, sampleButton2, sampleButton3, sampleButton4, sampleButton5);
		toolBar.getItems().addAll(spacer, buttonBar);

		Scene scene = new Scene(root, 800, 600);
		stage.setScene(scene);
		stage.setTitle("Segmented Button Bar");
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}