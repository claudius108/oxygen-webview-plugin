package ro.kuberam.oxygen.webview.WebViewPluginExtension;

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

	private static final Logger logger = Logger.getLogger(DlriOxygenPluginExtension.class.getName());

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

		String pluginJarPathString = DlriOxygenPluginExtension.class.getProtectionDomain().getCodeSource().getLocation()
				.getPath();
		pluginJarPathString = pluginJarPathString.substring(0, pluginJarPathString.lastIndexOf("/"));
		pluginJarPathString = pluginJarPathString.replaceAll("%20", " ");
		pluginInstallDir = new File(new File(pluginJarPathString).getParent());
		logger.debug("pluginInstallDir = " + pluginInstallDir);

		Path pluginInstallDirPath = pluginInstallDir.toPath();
		Properties addonProperties = new Properties();
		try {
			InputStream addonPropertiesIs = new FileInputStream(
					new File(pluginInstallDir + File.separator + "addon.properties"));
			addonProperties.load(addonPropertiesIs);
			addonPropertiesIs.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String frameworkDirName = addonProperties.getProperty("frameworkDirName");
		frameworkDirName = frameworkDirName.replaceAll("[^A-Za-z0-9.]", "_");
		logger.debug("frameworkDirName = " + frameworkDirName);

		Path frameworkPath = pluginInstallDirPath.resolve(".." + File.separator + ".." + File.separator + ".."
				+ File.separator + "frameworks" + File.separator + frameworkDirName + File.separator);
		logger.debug("frameworkPath = " + frameworkPath);

		File frameworkJar = new File(pluginInstallDir + File.separator + "lib" + File.separator + frameworkJarName);
		frameworkContainerDir = frameworkPath.toFile();
		frameworkContainerDir.mkdirs();
		templatesDir = new File(frameworkContainerDir + File.separator + frameworkId + File.separator + "templates");
		logger.debug("templatesDir = " + templatesDir);

		String frameworkDescriptorContent = null;

		try {
			if (!frameworkContainerDir.exists() && frameworkJar.exists()) {
				Zip.unZipAll(frameworkJar, frameworkContainerDir);
				new File(frameworkContainerDir + File.separator + frameworkId + "dev")
						.renameTo(new File(frameworkContainerDir + File.separator + frameworkId));
				FileUtils.deleteDirectory(new File(frameworkContainerDir + File.separator + "META-INF"));
				frameworkJar.delete();
			}

			if (frameworkContainerDir.exists() && frameworkJar.exists()) {
				frameworkContainerDir.setWritable(true);
				FileUtils.forceDelete(frameworkContainerDir);
				Zip.unZipAll(frameworkJar, frameworkContainerDir);
				new File(frameworkContainerDir + File.separator + frameworkId + "dev")
						.renameTo(new File(frameworkContainerDir + File.separator + frameworkId));
				FileUtils.deleteDirectory(new File(frameworkContainerDir + File.separator + "META-INF"));
				frameworkJar.delete();
			}

			File frameworkDir = new File(frameworkContainerDir + File.separator + frameworkId);
			File frameworkDescriptorFile = new File(frameworkDir + File.separator + frameworkId + "dev" + ".framework");

			if (frameworkDescriptorFile.exists()) {
				frameworkDescriptorContent = new Scanner(new FileInputStream(frameworkDescriptorFile),
						StandardCharsets.UTF_8.displayName()).useDelimiter("\\A").next();
				logger.debug("frameworkDescriptorContent = " + frameworkDescriptorContent);

				frameworkDescriptorContent = frameworkDescriptorContent.replaceFirst("<String>01-dev.xml</String>",
						"<String>*</String>");
				frameworkDescriptorContent = frameworkDescriptorContent.replaceFirst("<String>dlridev</String>",
						"<String>dlri</String>");
				frameworkDescriptorContent = frameworkDescriptorContent.replaceFirst(
						"<field name=\"priority\">\\s*<Integer>4</Integer>",
						"<field name=\"priority\"><Integer>3</Integer>");
				logger.debug("processsed frameworkDescriptorContent = " + frameworkDescriptorContent);

				FileUtils.writeStringToFile(frameworkDescriptorFile, frameworkDescriptorContent,
						StandardCharsets.UTF_8.displayName());
				frameworkDescriptorFile.renameTo(new File(frameworkDir + File.separator + frameworkId + ".framework"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

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

		File layoutFile = new File(frameworkDir + File.separator + "layout" + File.separator + "dlri.layout");
		logger.debug("layoutFile = " + layoutFile.getPath());

		PerspectivesLayoutInfo info = new PerspectivesLayoutInfo(true, false, "", layoutFile.getPath());
		pluginWorkspaceAccess.setGlobalObjectProperty("perspectives.layout.info", info);

		// set up the data source and connection
		final WSOptionsStorage optionsStorage = getPluginWorkspaceAccess().getOptionsStorage();

		dataSourceConnectionInfo = getPluginWorkspaceAccess().getDataSourceAccess()
				.getDataSourceConnectionInfo(connectionName);

		if (dataSourceConnectionInfo == null) {
			// show the dialog for credentials
			JFrame parentFrame = (JFrame) getPluginWorkspaceAccess().getParentFrame();
			OKCancelDialog credentialsDialog = new OKCancelDialog(parentFrame, "Introduceți datele pentru conectare",
					true);
			credentialsDialog.setLayout(new BoxLayout(credentialsDialog.getContentPane(), BoxLayout.Y_AXIS));
			credentialsDialog.setPreferredSize(new Dimension(500, 200));

			JPanel upperPanel = new JPanel();
			upperPanel.setPreferredSize(new Dimension(100, 100));
			JLabel usernameLabel = new JLabel("Selectați numele de utilizator");
			upperPanel.add(usernameLabel);
			JComboBox<String> searchCriterionComboBox = new JComboBox<String>();
			searchCriterionComboBox.setPrototypeDisplayValue("123456789012345678901234567");
			searchCriterionComboBox.setModel(getUsernames());
			upperPanel.add(searchCriterionComboBox);

			JPanel bottomPanel = new JPanel();
			bottomPanel.setPreferredSize(new Dimension(100, 100));
			JLabel passwordLabel = new JLabel("Introduceți parola");
			bottomPanel.add(passwordLabel);
			JPasswordField passwordField = new JPasswordField("", 22);
			bottomPanel.add(passwordField);

			credentialsDialog.getContentPane().add(upperPanel);
			credentialsDialog.getContentPane().add(bottomPanel);

			credentialsDialog.pack();
			credentialsDialog.setLocationRelativeTo(null);
			credentialsDialog.setVisible(true);
			credentialsDialog.requestFocus();
			credentialsDialog.repaint();

			if (credentialsDialog.getResult() == 0) {
				getPluginWorkspaceAccess().showErrorMessage("Conexiunea la server nu a fost configurată!");
				return;
			}

			credentialsDialog.dispose();

			String eXistFilesDir = pluginInstallDir + File.separator + "exist" + File.separator;
			PluginWorkspace pluginWorkspace = PluginWorkspaceProvider.getPluginWorkspace();

			try {
				DBSourceDriverInfo dsdi = new DBSourceDriverInfo("eXist", datasourceName, "",
						new URL[] { URLUtil.correct(new File(eXistFilesDir + "exist.jar")),
								URLUtil.correct(new File(eXistFilesDir + "ws-commons-util-1.0.2.jar")),
								URLUtil.correct(new File(eXistFilesDir + "xmldb.jar")),
								URLUtil.correct(new File(eXistFilesDir + "xmlrpc-client-3.1.3.jar")),
								URLUtil.correct(new File(eXistFilesDir + "xmlrpc-common-3.1.3.jar")) });

				pluginWorkspace.setGlobalObjectProperty("database.jdbc.drivers.1", new DBSourceDriverInfo[] { dsdi });

				Username usernameObj = (Username) searchCriterionComboBox.getSelectedItem();
				String username = usernameObj.getUserid();

				DBConnectionInfo eXistSessionInfo = new DBConnectionInfo(connectionName, datasourceName,
						"xmldb:exist://" + dlri_host + "/xmlrpc", username, new String(passwordField.getPassword()),
						null, null, workCollectionPath + username);
				pluginWorkspace.setGlobalObjectProperty("database.stored.sessions1",
						new DBConnectionInfo[] { eXistSessionInfo });

				optionsStorage.setOption("dlri.username", username);
				optionsStorage.setOption("dlri.datasourceName", datasourceName);
				optionsStorage.setOption("dlri.connectionName", connectionName);

			} catch (MalformedURLException e3) {
				e3.printStackTrace();
			}
		}

		// add plugin's toolbar
		pluginWorkspaceAccess.addToolbarComponentsCustomizer(new ToolbarComponentsCustomizer() {
			public void customizeToolbar(final ToolbarInfo toolbarInfo) {

				String toolbarId = toolbarInfo.getToolbarID();

				if (toolbarId.equals("DlriToolbar")) {
					// parentFrame = (JFrame)
					// pluginWorkspaceAccess.getParentFrame();
					//
					List<JComponent> comps = new ArrayList<JComponent>();
					//
					// final String content = new
					// Scanner(getClass().getResourceAsStream("toolbar.html"),
					// "UTF-8").useDelimiter("\\A").next();
					//
					// JavaFXPanel panel = new JavaFXPanel(content, null, new
					// Java());
					//
					// panel.setPreferredSize(new Dimension(300, 45));

					JButton lemmaButton = new JButton("Lemă");
					lemmaButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								String templateContent = FileUtils.readFileToString(
										new File(templatesDir + File.separator + "lemma.xml"), "UTF-8");
								pluginWorkspaceAccess.createNewEditor("xml", "text/xml",
										PluginWorkspaceProvider.getPluginWorkspace().getUtilAccess()
												.expandEditorVariables(templateContent, null));
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					});

					JButton variantButton = new JButton("Variantă");
					variantButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								String templateContent = FileUtils.readFileToString(
										new File(templatesDir + File.separator + "variant.xml"), "UTF-8");
								pluginWorkspaceAccess.createNewEditor("xml", "text/xml",
										PluginWorkspaceProvider.getPluginWorkspace().getUtilAccess()
												.expandEditorVariables(templateContent, null));
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					});

					JButton saveButton = new JButton();
					saveButton.setAction(fileSaveAction);
					saveButton.setText("Salvare");

					JButton validateButton = new JButton("Validare");
					validateButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							WSEditor currentEditor = pluginWorkspaceAccess
									.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA);

							try {
								URL currentEditorLocation = new URL(URLDecoder
										.decode(currentEditor.getEditorLocation().toURI().toASCIIString(), "UTF-8"));

								String validateDir = frameworkDir + "validate/";
								logger.debug("validateDir = " + validateDir);

								String xqueryScriptURL = validateDir + "validate.xq";
								xqueryScriptURL = xqueryScriptURL.replaceAll("/", "%2F");
								logger.debug("xqueryScriptURL = " + xqueryScriptURL);

								URL newEditorURL = new URL("convert:/processor=xquery;ss=" + xqueryScriptURL + "!/"
										+ currentEditorLocation);
								logger.debug("newEditorURL = " + newEditorURL);

								pluginWorkspaceAccess.open(newEditorURL, EditorPageConstants.PAGE_AUTHOR, "text/html");
								WSEditor newEditor = pluginWorkspaceAccess
										.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA);
								newEditor.setEditorTabText(
										"validate:" + new File(currentEditorLocation.getFile()).getName());
							} catch (MalformedURLException | UnsupportedEncodingException | URISyntaxException e) {
								e.printStackTrace();
							}
						}
					});

					JButton renderButton = new JButton("Redare");
					renderButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							WSEditor currentEditor = pluginWorkspaceAccess
									.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA);

							try {
								URL currentEditorLocation = new URL(URLDecoder
										.decode(currentEditor.getEditorLocation().toURI().toASCIIString(), "UTF-8"));

								String renderDir = frameworkDir + "views/";
								logger.debug("renderDir = " + renderDir);

								String xqueryScriptURL = renderDir + "render-entry.xq";
								xqueryScriptURL = xqueryScriptURL.replaceAll("/", "%2F");
								logger.debug("xqueryScriptURL = " + xqueryScriptURL);

								URL newEditorURL = new URL("convert:/processor=xquery;ss=" + xqueryScriptURL + "!/"
										+ currentEditorLocation);
								logger.debug("newEditorURL = " + newEditorURL);

								pluginWorkspaceAccess.open(newEditorURL, EditorPageConstants.PAGE_AUTHOR, "text/html");
								WSEditor newEditor = pluginWorkspaceAccess
										.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA);
								newEditor.setEditorTabText(
										"render:" + new File(currentEditorLocation.getFile()).getName());
							} catch (MalformedURLException | UnsupportedEncodingException | URISyntaxException e) {
								e.printStackTrace();
							}
						}
					});

					JButton entriesButton = new JButton("Intrări");
					entriesButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							getPluginWorkspaceAccess().showErrorMessage(
									"Această acțiune este disponibilă începând cu versiunea 17 a Oxygen-ului.");
							// String content = Get
							// .run("http://188.212.37.221:8080/apps/dlri-app/services/views/get-redactor-entries.xq?redactor-name="
							// + optionsStorage.getOption("dlri.username", ""));
							//
							// final DialogModel dialogModel = new
							// DialogModel("redactor-entries-dialog",
							// "modal", "Intrări", 700, 400, "both", new
							// String[] { "auto" }, "",
							// content);
							// SwingUtilities.invokeLater(new Runnable() {
							// public void run() {
							// new
							// JavaFXDialog(AddonBuilderPluginExtension.parentFrame,
							// dialogModel);
							// }
							// });
						}
					});

					JButton exitButton = new JButton();
					exitButton.setAction(fileExitAction);
					exitButton.setText("Ieșire");

					JButton addonUpdatesButton = new JButton();
					addonUpdatesButton.setAction(addonUpdatesAction);
					addonUpdatesButton.setText("Actualizare");

					JButton button1 = new JButton("1");
					button1.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent event) {
							try {
								String templateContent = FileUtils.readFileToString(
										new File(templatesDir + File.separator + "lemma.xml"), "UTF-8");
								pluginWorkspaceAccess.createNewEditor("xml", "text/xml",
										PluginWorkspaceProvider.getPluginWorkspace().getUtilAccess()
												.expandEditorVariables(templateContent, null));

								WSEditor currentEditor = pluginWorkspaceAccess
										.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA);
								currentEditor.changePage(EditorPageConstants.PAGE_TEXT);
								logger.debug("getCurrentPageID = " + currentEditor.getCurrentPageID());

								WSTextEditorPage basePage = (WSTextEditorPage) currentEditor.getCurrentPage();
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
								
								panel.loadURL("https://www.youtube.com/watch?v=E2LM3ZlcDnk");

							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					});

					pluginWorkspaceAccess.addMenuBarCustomizer(new MenuBarCustomizer() {
						@Override
						public void customizeMainMenu(JMenuBar mainMenuBar) {

						}
					});

					comps.add(lemmaButton);
					comps.add(variantButton);
					comps.add(entriesButton);
					comps.add(saveButton);
					comps.add(validateButton);
					comps.add(renderButton);
					comps.add(addonUpdatesButton);
					comps.add(exitButton);
					comps.add(button1);

					toolbarInfo.setComponents(comps.toArray(new JComponent[0]));

					toolbarInfo.setTitle("DLRI Toolbar");
				}

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

	@SuppressWarnings("rawtypes")
	private DefaultComboBoxModel getUsernames() {
		// make the http connection
		URL url;
		HttpURLConnection connection;
		BufferedReader reader;
		String line = null;

		ArrayList<Username> usernames = new ArrayList<Username>();

		try {
			url = new URL(get_usernames_service);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "text/plain");
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			while ((line = reader.readLine()) != null) {
				String userid = line.substring(0, line.indexOf("\t"));
				String username = line.substring(line.indexOf("\t") + 1);
				usernames.add(new Username(userid, username));
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		DefaultComboBoxModel searchCriterionModel = new DefaultComboBoxModel(usernames.toArray());

		return searchCriterionModel;
	}
}
