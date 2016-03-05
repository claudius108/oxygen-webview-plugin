package ro.kuberam.oxygen.webview.editors.web;

import static javafx.concurrent.Worker.State.FAILED;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.print.PrinterJob;
import javafx.scene.Node;
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
import javafx.scene.shape.Circle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;

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

		Button backButton = new Button("\u22b2");
		backButton.setTooltip(new Tooltip("Back"));
		backButton.getStyleClass().addAll("first");
		backButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				webEngine.getHistory().go(-1);
			}
		});

		Button forwardButton = new Button("\u22b3");
		forwardButton.setTooltip(new Tooltip("Forward"));
		forwardButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				webEngine.getHistory().go(1);
			}
		});

		Button savePageAsButton = new Button();
		ImageIcon icon = (ImageIcon) UIManager.getIcon("FileView.floppyDriveIcon");
		BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		icon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
		Image picture = SwingFXUtils.toFXImage(bufferedImage, null);
		savePageAsButton.setGraphic(new ImageView(picture));
		savePageAsButton.setTooltip(new Tooltip("Save Page As"));
		savePageAsButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Document doc = webEngine.getDocument();
				String result = null;

				try {
					StringWriter resultWriter = new StringWriter();

					Transformer transformer = TransformerFactory.newInstance().newTransformer();
					transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
					transformer.setOutputProperty(OutputKeys.METHOD, "xml");
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
					transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
					transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

					transformer.transform(new DOMSource(doc), new StreamResult(resultWriter));

					StringBuffer sb = resultWriter.getBuffer();
					result = sb.toString();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				FileChooser fileChooser = new FileChooser();
				Path fileName = Paths.get(webEngine.getLocation());

				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Web Page, HTML only",
						"*.html");
				fileChooser.getExtensionFilters().add(extFilter);

				fileChooser.setInitialFileName(fileName.getFileName().toString());

				File file = fileChooser.showSaveDialog(null);

				if (file != null) {
					saveFile(result, file);
				}
			}
		});

		Button refreshButton = new Button("\u21BA");
		refreshButton.setTooltip(new Tooltip("Refresh"));
		refreshButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				webEngine.reload();
			}
		});

		Button printButton = new Button("\uD83D\uDDB6");
		printButton.setTooltip(new Tooltip("Print"));
		printButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Node node = new Circle(100, 200, 200);
				// Printer printer = Printer.getDefaultPrinter();
				// System.out.println("printer = " + printer);
				//
				// PageLayout pageLayout =
				// printer.createPageLayout(Paper.NA_LETTER,
				// PageOrientation.PORTRAIT, Printer.MarginType.DEFAULT);
				// double scaleX = pageLayout.getPrintableWidth() /
				// node.getBoundsInParent().getWidth();
				// double scaleY = pageLayout.getPrintableHeight() /
				// node.getBoundsInParent().getHeight();
				// node.getTransforms().add(new Scale(scaleX, scaleY));

				PrinterJob job = PrinterJob.createPrinterJob();
				System.out.println("job = " + job);
				if (job != null) {
					System.out.println(job.getJobSettings().getPageLayout());
					job.showPrintDialog(null);
					webEngine.print(job); // job.printPage(webView);
					System.out.println("job = " + job);
				}
			}
		});

		// save as image
		// File destFile = new File("test.png");
		// WritableImage snapshot = webView.snapshot(new SnapshotParameters(),
		// null);
		// RenderedImage renderedImage = SwingFXUtils.fromFXImage(snapshot,
		// null);
		// try {
		// ImageIO.write(renderedImage, "png", destFile);
		// } catch (IOException ex) {
		// Logger.getLogger(GoogleMap.class.getName()).log(Level.SEVERE, null,
		// ex);
		// }

		buttonBar.getChildren().addAll(backButton, forwardButton, savePageAsButton, refreshButton);
		toolBar.getItems().addAll(spacer, buttonBar);

		StackPane viewHolder = new StackPane();
		viewHolder.setPrefSize(width, height);

		WebView webView = new WebView();
		webEngine = webView.getEngine();

		// webEngine.getLoadWorker().stateProperty().addListener(new
		// ChangeListener<State>() {
		// public void changed(ObservableValue<? extends State> observable,
		// State oldValue, State newValue) {
		// if (newValue == javafx.concurrent.Worker.State.SUCCEEDED) {
		// }
		// }
		// });

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
