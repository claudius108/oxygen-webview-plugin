package ro.kuberam.oxygen.webview;

import ro.sync.exml.plugin.Plugin;
import ro.sync.exml.plugin.PluginDescriptor;

/**
 * Workspace access plugin.
 */
public class WebViewPlugin extends Plugin {
	/**
	 * The static plugin instance.
	 */
	private static WebViewPlugin instance = null;

	/**
	 * Constructs the plugin.
	 * 
	 * @param descriptor
	 *            The plugin descriptor
	 */
	public WebViewPlugin(PluginDescriptor descriptor) {
		super(descriptor);

		if (instance != null) {
			throw new IllegalStateException("Already instantiated!");
		}
		instance = this;
	}

	/**
	 * Get the plugin instance.
	 * 
	 * @return the shared plugin instance.
	 */
	public static WebViewPlugin getInstance() {
		return instance;
	}
}
