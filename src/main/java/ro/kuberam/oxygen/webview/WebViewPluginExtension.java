package ro.kuberam.oxygen.webview;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Plugin extension - dlri extension.
 */
public class WebViewPluginExtension implements WorkspaceAccessPluginExtension {

	private static final Logger logger = Logger.getLogger(WebViewPluginExtension.class.getName());

	private Action fileExitAction = null;
	private Action fileSaveAction = null;
	private Action addonUpdatesAction = null;

	public WebViewPluginExtension() {
	}

	@Override
	public void applicationStarted(final StandalonePluginWorkspace pluginWorkspaceAccess) {

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
								System.out.println(oxygenActionID);
								switch (oxygenActionID) {
								case "File/File_Exit":
									fileExitAction = action;
									break;
								case "File/File_Save":
									fileSaveAction = action;
									break;
								case "Help/Check_for_addons_updates":
									addonUpdatesAction = action;
									break;
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
}
