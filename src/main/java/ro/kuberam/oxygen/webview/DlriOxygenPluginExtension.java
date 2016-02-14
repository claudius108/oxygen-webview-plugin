package ro.kuberam.oxygen.webview;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import ro.sync.db.DBConnectionInfo;
import ro.sync.db.core.DBSourceDriverInfo;
import ro.sync.ecss.extensions.commons.ui.OKCancelDialog;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.options.PerspectivesLayoutInfo;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.PluginWorkspaceProvider;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.options.DataSourceConnectionInfo;
import ro.sync.exml.workspace.api.options.WSOptionsStorage;
import ro.sync.exml.workspace.api.standalone.MenuBarCustomizer;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;
import ro.sync.exml.workspace.api.standalone.ToolbarComponentsCustomizer;
import ro.sync.exml.workspace.api.standalone.ToolbarInfo;
import ro.sync.util.URLUtil;

/**
 * Plugin extension - dlri extension.
 */
public class WebViewPluginExtension implements WorkspaceAccessPluginExtension {

	private static final Logger logger = Logger.getLogger(WebViewPluginExtension.class.getName());

	private static String frameworkId = "dlri";
	private static String frameworkJarName = frameworkId + ".jar";
	private static String datasourceName = frameworkId;
	private static String connectionName = frameworkId + ".ro";
	private static String workCollectionPath = "/db/data/dlri-app/work/";
	private File pluginInstallDir;
	private File frameworkContainerDir;
	private String frameworkDir;
	private File templatesDir;

	private static String dlri_host = "188.212.37.221:8080";
	private static String dlri_app_url = "http://" + dlri_host + "/apps/dlri-app";
	private static String dlri_app_services_url = dlri_app_url + "/services";
	public static String get_usernames_service = dlri_app_services_url + "/get-usernames.xq";
	public JFrame parentFrame;

	private StandalonePluginWorkspace pluginWorkspaceAccess;
	private Action fileExitAction = null;
	private Action fileSaveAction = null;
	private Action addonUpdatesAction = null;
	private DataSourceConnectionInfo dataSourceConnectionInfo = null;

	public DlriOxygenPluginExtension() {
	}

	@Override
	public void applicationStarted(final StandalonePluginWorkspace pluginWorkspaceAccess) {
		this.setPluginWorkspaceAccess(pluginWorkspaceAccess);

		frameworkDir = URLUtil.uncorrect(PluginWorkspaceProvider.getPluginWorkspace().getUtilAccess()
				.expandEditorVariables("${framework(" + frameworkId + ")}", null));
		logger.debug("frameworkDir = " + frameworkDir);

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

	public StandalonePluginWorkspace getPluginWorkspaceAccess() {
		return pluginWorkspaceAccess;
	}

	public void setPluginWorkspaceAccess(StandalonePluginWorkspace pluginWorkspaceAccess) {
		this.pluginWorkspaceAccess = pluginWorkspaceAccess;
	}
}
