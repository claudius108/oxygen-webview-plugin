package ro.kuberam.oxygen.webview;

import static javafx.concurrent.Worker.State.FAILED;

import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class WebViewPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4394124410059449821L;
	private final JFXPanel jfxPanel = new JFXPanel();
	private WebEngine webEngine;
	private int width;
	private int height;

	public WebViewPanel(int width, int height) {
		super();

		Platform.runLater(() -> {
			createScene();
		});
		
		this.width = width - 10;
		this.height = height - 10;

		add(jfxPanel);
	}

	private void createScene() {

		ToolBar toolBar = new ToolBar();

		Region spacer = new Region();
		spacer.getStyleClass().setAll("spacer");

		HBox buttonBar = new HBox();
		buttonBar.getStyleClass().setAll("segmented-button-bar");

		Button backButton = new Button("\u22b2");
		backButton.setTooltip(new Tooltip("Back"));
		backButton.getStyleClass().addAll("first");

		Button forwardButton = new Button("\u22b3");
		forwardButton.setTooltip(new Tooltip("Forward"));

		Button savePageAsButton = new Button();
		ImageIcon icon = (ImageIcon) UIManager.getIcon("FileView.floppyDriveIcon");
		BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		icon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
		Image picture = SwingFXUtils.toFXImage(bufferedImage, null);
		savePageAsButton.setGraphic(new ImageView(picture));
		savePageAsButton.setTooltip(new Tooltip("Save Page As"));

		Button refreshButton = new Button("\u21BA");
		refreshButton.setTooltip(new Tooltip("Refresh"));

		buttonBar.getChildren().addAll(backButton, forwardButton, savePageAsButton, refreshButton);
		toolBar.getItems().addAll(spacer, buttonBar);

		StackPane viewHolder = new StackPane();
		viewHolder.setPrefSize(width, height);

		WebView webView = new WebView();
		webEngine = webView.getEngine();

		webEngine.getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {

			public void changed(ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) {
				if (webEngine.getLoadWorker().getState() == FAILED) {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							JOptionPane.showMessageDialog(jfxPanel,
									(value != null) ? webEngine.getLocation() + "\n" + value.getMessage()
											: webEngine.getLocation() + "\nUnexpected error.",
									"Loading error...", JOptionPane.ERROR_MESSAGE);
						}
					});
				}
			}
		});
		viewHolder.getChildren().setAll(webView);

		VBox layout = new VBox(10, toolBar, viewHolder);
		layout.setPadding(new Insets(10));

		Scene scene = new Scene(layout, width, height);

		jfxPanel.setScene(scene);

		PrinterJob job = PrinterJob.createPrinterJob();
		if (job != null) {
			webEngine.print(job);
			job.endJob();
		}
	}

	public void loadURL(final String url) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				String tmp = toURL(url);

				if (tmp == null) {
					tmp = toURL("http://" + url);
				}

				webEngine.load(tmp);
			}
		});
	}

	private static String toURL(String str) {
		try {
			return new URL(str).toExternalForm();
		} catch (MalformedURLException exception) {
			return null;
		}
	}

}
