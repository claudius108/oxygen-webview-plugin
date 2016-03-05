package ro.kuberam.oxygen.webview;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import javafx.embed.swing.JFXPanel;
import ro.kuberam.oxygen.webview.editors.Editors;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ToolbarComponentsCustomizer;
import ro.sync.exml.workspace.api.standalone.ToolbarInfo;

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
				Editors.createWebEditor(pluginWorkspaceAccess,
						"https://www.oxygenxml.com/doc/versions/17.1/ug-author/#introduction.html#introduction");
			}
		};

		ActionListener openUrlInWebWebViewActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					URL urlToOpen = pluginWorkspaceAccess.chooseURL("Open URL in WebView", null, null);
					Editors.createWebEditor(pluginWorkspaceAccess, urlToOpen.toURI().toASCIIString());
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		};

		ActionListener savePageAsActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WSEditor newEditor = pluginWorkspaceAccess.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA);
				WSTextEditorPage basePage = (WSTextEditorPage) newEditor.getCurrentPage();
				JComponent component = (JComponent) newEditor.getComponent();
				Container parent = component.getParent().getParent();
				logger.debug("parent = " + parent.getClass().getName());

				Component[] components = parent.getComponents();

				for (int i = 0; i < components.length; ++i) {
					logger.debug("component = " + components[i].getClass().getName());
				}
				// parent.remove(components[1]);

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

									newItem = new JMenuItem("Save Page As");
									menu.insert(newItem, j + 2);
									newItem.addActionListener(savePageAsActionListener);
									newItem.setActionCommand("File/Save_Page_As");
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

		// add plugin's toolbar
		pluginWorkspaceAccess.addToolbarComponentsCustomizer(new ToolbarComponentsCustomizer() {
			public void customizeToolbar(final ToolbarInfo toolbarInfo) {

				String toolbarId = toolbarInfo.getToolbarID();

				if (toolbarId.equals("WebViewToolbar")) {
					List<JComponent> comps = new ArrayList<JComponent>();

					JFXPanel panel = new Toolbar();

					// panel.setPreferredSize(new Dimension(300, 45));
					comps.add(panel);

					toolbarInfo.setComponents(comps.toArray(new JComponent[0]));

					toolbarInfo.setTitle("WebView Toolbar");
				}
			}
		});
	}

	@Override
	public boolean applicationClosing() {
		return true;
	}
}
