package ro.kuberam.oxygen.webview.editors;

import java.awt.Container;

import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import ro.kuberam.oxygen.webview.WebViewPanel;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.text.WSTextEditorPage;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

public class Editors {

	private static final Logger logger = Logger.getLogger(Editors.class.getName());

	public static void createWebEditor(StandalonePluginWorkspace pluginWorkspaceAccess, String urlToOpen) {
		pluginWorkspaceAccess.createNewEditor("text", "text/plain", "");

		WSEditor newEditor = pluginWorkspaceAccess.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA);
		newEditor.setEditorTabText(urlToOpen);
		newEditor.changePage(EditorPageConstants.PAGE_TEXT);
		logger.debug("getCurrentPageID = " + newEditor.getCurrentPageID());

		WSTextEditorPage basePage = (WSTextEditorPage) newEditor.getCurrentPage();
		logger.debug("basePage = " + Runnable.class.getName());

		JTextArea textArea = (JTextArea) basePage.getTextComponent();
		int width = textArea.getWidth();
		logger.debug("width = " + width);
		int height = textArea.getHeight();
		logger.debug("height = " + height);

		Container parent = textArea.getParent();
		parent.remove(textArea);

		WebViewPanel panel = new WebViewPanel(width, height);
		parent.add(panel);

		panel.loadURL(urlToOpen);
	}
	
	public static void createHtmlEditor(StandalonePluginWorkspace pluginWorkspaceAccess, String urlToOpen) {
		pluginWorkspaceAccess.createNewEditor("text", "text/plain", "");

		WSEditor newEditor = pluginWorkspaceAccess.getCurrentEditorAccess(PluginWorkspace.MAIN_EDITING_AREA);
		newEditor.setEditorTabText(urlToOpen);
		newEditor.changePage(EditorPageConstants.PAGE_TEXT);
		logger.debug("getCurrentPageID = " + newEditor.getCurrentPageID());

		WSTextEditorPage basePage = (WSTextEditorPage) newEditor.getCurrentPage();
		logger.debug("basePage = " + Runnable.class.getName());

		JTextArea textArea = (JTextArea) basePage.getTextComponent();
		int width = textArea.getWidth();
		logger.debug("width = " + width);
		int height = textArea.getHeight();
		logger.debug("height = " + height);

		Container parent = textArea.getParent();
		parent.remove(textArea);

		WebViewPanel panel = new WebViewPanel(width, height);
		parent.add(panel);

		panel.loadURL(urlToOpen);
	}

}
