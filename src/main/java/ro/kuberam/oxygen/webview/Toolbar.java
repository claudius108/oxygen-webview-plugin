package ro.kuberam.oxygen.webview;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import org.apache.commons.io.FileUtils;

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
import ro.kuberam.oxygen.webview.editors.Editors;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class Toolbar extends JFXPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6846419571589812272L;
	private StandalonePluginWorkspace pluginWorkspaceAccess;

	public Toolbar(StandalonePluginWorkspace pluginWorkspaceAccess) {
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;

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

		Button newFileButton = new Button("New");
//		newFileButton.setGraphic(Utils.getIcon("FileView.fileIcon"));
		newFileButton.setTooltip(new Tooltip("New File"));
		newFileButton.getStyleClass().addAll("first");
		newFileButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
			}
		});

		Button openFileButton = new Button("Open");
//		openFileButton.setGraphic(Utils.getIcon("Tree.openIcon"));
		openFileButton.setTooltip(new Tooltip("Open File"));
		openFileButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				FileChooser fileChooser = new FileChooser();

				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("HTML", "*.html");
				fileChooser.getExtensionFilters().add(extFilter);

				File file = fileChooser.showOpenDialog(null);
				String fileContent = null;
				try {
					fileContent = FileUtils.readFileToString(file, "UTF-8");
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				Editors.createHtmlEditor(pluginWorkspaceAccess, fileContent, file);
			}
		});

		buttonBar.getChildren().addAll(newFileButton, openFileButton);
		toolBar.getItems().addAll(spacer, buttonBar);

		VBox layout = new VBox(10, toolBar);
		layout.setPadding(new Insets(10));

		Scene scene = new Scene(layout);

		setScene(scene);
	}
}
