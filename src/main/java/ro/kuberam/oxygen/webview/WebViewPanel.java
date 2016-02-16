package ro.kuberam.oxygen.webview;

import static javafx.concurrent.Worker.State.FAILED;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class WebViewPanel extends JFXPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4394124410059449821L;
	private WebEngine webEngine;

	public WebViewPanel() {
		super();
		createScene();
	}

	private void createScene() {

		JFXPanel panel = this;

		Platform.runLater(new Runnable() {
			@Override
			public void run() {

				WebView view = new WebView();
				webEngine = view.getEngine();

				webEngine.getLoadWorker().exceptionProperty().addListener(new ChangeListener<Throwable>() {

					public void changed(ObservableValue<? extends Throwable> o, Throwable old, final Throwable value) {
						if (webEngine.getLoadWorker().getState() == FAILED) {
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									JOptionPane.showMessageDialog(panel,
											(value != null) ? webEngine.getLocation() + "\n" + value.getMessage()
													: webEngine.getLocation() + "\nUnexpected error.",
											"Loading error...", JOptionPane.ERROR_MESSAGE);
								}
							});
						}
					}
				});

				setScene(new Scene(view));
				
				PrinterJob job = PrinterJob.createPrinterJob();
	            if (job != null) {
	                webEngine.print(job);
	                job.endJob();
	            }
			}
		});

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
