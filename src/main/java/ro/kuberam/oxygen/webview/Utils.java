package ro.kuberam.oxygen.webview;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.UIManager;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Utils {
	
	public static Node getIcon(String string) {
		ImageIcon icon = (ImageIcon) UIManager.getIcon(string);
		BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
				BufferedImage.TYPE_INT_ARGB);
		icon.paintIcon(null, bufferedImage.getGraphics(), 0, 0);
		Image picture = SwingFXUtils.toFXImage(bufferedImage, null);

		return new ImageView(picture);
	}

}
