package ro.kuberam.oxygen.webview;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Plugin extension - dlri extension.
 */
public class WebViewPluginExtension implements WorkspaceAccessPluginExtension {

	private static final Logger logger = Logger.getLogger(WebViewPluginExtension.class.getName());

	public WebViewPluginExtension() {
	}

	@Override
	public void applicationStarted(final StandalonePluginWorkspace pluginWorkspaceAccess) {

		ActionListener helpActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createNewEditor(pluginWorkspaceAccess,
						"https://www.oxygenxml.com/doc/versions/17.1/ug-author/#introduction.html#introduction");
			}
		};

		ActionListener openUrlInWebWebViewActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					URL urlToOpen = pluginWorkspaceAccess.chooseURL("Open URL in WebView", null, null);
					createNewEditor(pluginWorkspaceAccess, urlToOpen.toURI().toASCIIString());
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		};

		pluginWorkspaceAccess.addMenuBarCustomizer(new MenuBarCustomizer() {
			@Override
			public void customizeMainMenu(JMenuBar mainMenuBar) {
				int menuCount = mainMenuBar.getMenuCount();
				// Iterate over menus to find the needed actions
				for (int i = 0; i < menuCount; i++) {
					// Revert action index in menu
					JMenu menu = mainMenuBar.getMenu(i);
					int itemCount = menu.getItemCount();
					for (int j = 0; j < itemCount; j++) {
						JMenuItem item = menu.getItem(j);
						if (item != null) {
							Action action = item.getAction();
							String oxygenActionID = pluginWorkspaceAccess.getOxygenActionID(action);
							if (oxygenActionID != null) {
								logger.debug(oxygenActionID);

								if (oxygenActionID.equals("File/File_Open_URL")) {
									JMenuItem newItem = new JMenuItem("Open URL in WebView");
									menu.insert(newItem, j + 1);
									newItem.addActionListener(openUrlInWebWebViewActionListener);
									newItem.setActionCommand("File/File_Open_URL_In_Web_View");
									logger.debug("newItem command = "
											+ pluginWorkspaceAccess.getOxygenActionID(newItem.getAction()));
								}

								if (oxygenActionID.equals("Help/Help")) {
									ActionListener[] listeners = item.getActionListeners();

									for (int k = 0; k < listeners.length; k++) {
										item.removeActionListener(listeners[k]);
									}

									item.addActionListener(helpActionListener);
									item.setText("Help");
									item.setActionCommand("Help/Help");
								}
							}
						}
					}
				}

				// mainMenuBar.removeAll();
			}
		});
	}

	@Override
	public boolean applicationClosing() {
		return true;
	}

	private void createNewEditor(StandalonePluginWorkspace pluginWorkspaceAccess, String urlToOpen) {
		pluginWorkspaceAccess.createNewEditor("text", "text/plain", "");

		WSEditor newEditor = pluginWorkspaceAccess.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA);
		newEditor.setEditorTabText(urlToOpen);
		newEditor.changePage(EditorPageConstants.PAGE_TEXT);
		logger.debug("getCurrentPageID = " + newEditor.getCurrentPageID());

		WSTextEditorPage basePage = (WSTextEditorPage) newEditor.getCurrentPage();
		logger.debug("basePage = " + newEditor.getClass().getName());

		JTextArea textArea = (JTextArea) basePage.getTextComponent();
		int width = textArea.getWidth();
		logger.debug("width = " + width);
		int height = textArea.getHeight();
		logger.debug("height = " + height);

		Container parent = textArea.getParent();
		parent.remove(textArea);

		WebViewPanel panel = new WebViewPanel();
		panel.setSize(new Dimension(width, height));
		parent.add(panel);

		panel.loadURL(urlToOpen);
	}
}
