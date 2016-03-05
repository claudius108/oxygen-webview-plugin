package ro.kuberam.oxygen.webview;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class Toolbar extends JFXPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6846419571589812272L;

	public Toolbar() {
		Platform.runLater(() -> {
			createToolbar();
		});
	}

	private void createToolbar() {
		ToolBar toolBar = new ToolBar();

		Region spacer = new Region();
		spacer.getStyleClass().setAll("spacer");

		HBox buttonBar = new HBox();
		buttonBar.getStyleClass().setAll("segmented-button-bar");

		Button newFileButton = new Button();
		newFileButton.setGraphic(getIcon("FileView.fileIcon"));
		newFileButton.setTooltip(new Tooltip("Save Page As"));
		newFileButton.getStyleClass().addAll("first");
		newFileButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				FileChooser fileChooser = new FileChooser();

				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Web Page, HTML only",
						"*.html");
				fileChooser.getExtensionFilters().add(extFilter);

				File file = fileChooser.showSaveDialog(null);
			}
		});

		Button openFileButton = new Button();
		openFileButton.setGraphic(getIcon("FileView.fileIcon"));
		openFileButton.setTooltip(new Tooltip("Save Page As"));
		openFileButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				FileChooser fileChooser = new FileChooser();

				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Web Page, HTML only",
						"*.html");
				fileChooser.getExtensionFilters().add(extFilter);

				File file = fileChooser.showSaveDialog(null);
			}
		});

		buttonBar.getChildren().addAll(openFileButton);
		toolBar.getItems().addAll(spacer, buttonBar);

		VBox layout = new VBox(10, toolBar);
		layout.setPadding(new Insets(10));

		Scene scene = new Scene(layout);

		setScene(scene);
	}

	private Node getIcon(String string) {
		ImageIcon icon = (ImageIcon) UIManager.getIcon("FileView.fileIcon");
		BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		icon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
		Image picture = SwingFXUtils.toFXImage(bufferedImage, null);

		return new ImageView(picture);
	}
}
