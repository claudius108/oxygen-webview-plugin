package ro.kuberam.oxygen.webview.editors.html;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;
import ro.kuberam.oxygen.webview.Utils;

public class HtmlViewPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4394124410059449821L;
	private final JFXPanel jfxPanel = new JFXPanel();
	private HTMLEditor htmlEditor;
	private String content;
	public File file;
	private int width;
	private int height;

	public HtmlViewPanel(File file, String content, int width, int height) {
		super();

		this.file = file;
		this.content = content;
		this.width = width - 10;
		this.height = height - 10;

		Platform.runLater(() -> {
			createScene();
		});

		add(jfxPanel);
	}

	private void createScene() {

		ToolBar toolBar = new ToolBar();

		Region spacer = new Region();
		spacer.getStyleClass().setAll("spacer");

		HBox buttonBar = new HBox();
		buttonBar.getStyleClass().setAll("segmented-button-bar");

		Button saveButton = new Button("Save");
//		saveButton.setGraphic(Utils.getIcon("FileView.floppyDriveIcon"));
		saveButton.setTooltip(new Tooltip("Save"));
		saveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				String fileContent = htmlEditor.getHtmlText();

				try {
					FileUtils.writeStringToFile(file, fileContent);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		buttonBar.getChildren().addAll(saveButton);
		toolBar.getItems().addAll(spacer, buttonBar);

		StackPane viewHolder = new StackPane();
		viewHolder.setPrefSize(width, height);

		htmlEditor = new HTMLEditor();
		htmlEditor.setHtmlText(content);

		viewHolder.getChildren().setAll(htmlEditor);

		VBox layout = new VBox(10, toolBar, viewHolder);
		layout.setPadding(new Insets(10));

		Scene scene = new Scene(layout, width, height);

		jfxPanel.setScene(scene);
	}

	private void saveFile(String content, File file) {
		try {
			FileWriter fileWriter = null;

			fileWriter = new FileWriter(file);
			fileWriter.write(content);
			fileWriter.close();
		} catch (IOException ex) {
		}
	}

}
