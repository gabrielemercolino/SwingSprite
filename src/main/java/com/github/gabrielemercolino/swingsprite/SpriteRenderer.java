package com.github.gabrielemercolino.swingsprite;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Stack;

public final class SpriteRenderer {
	private final SpritePanel panel;
	private final BufferedImage buffer;
	private final Graphics2D g2d;
	private final Stack<Color> colors;
	private final Stack<Font> fonts;

	public SpriteRenderer(final int width, final int height) {
		buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		g2d = buffer.createGraphics();
		panel = new SpritePanel(buffer);
		colors = new Stack<>();
		fonts = new Stack<>();

		// Initialize with defaults
		colors.push(Color.WHITE);
		fonts.push(new Font("Monospaced", Font.PLAIN, 16));

		// Apply defaults
		g2d.setColor(colors.peek());
		g2d.setFont(fonts.peek());
	}

	public SpritePanel getPanel() {
		return panel;
	}

	
	public void clear(final Color color) {
		g2d.setColor(color);
		g2d.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
		g2d.setColor(colors.peek());
	}

	public void render() {
		panel.repaint();
	}

	public void drawSprite(final Sprite sprite, final int x, final int y) {
		g2d.drawImage(sprite.image(), x, y, null);
	}

	public void drawText(final String text, final int x, final int y) {
		g2d.drawString(text, x, y);
	}

	public boolean pushColor(final Color color) {
		if (color == null) return false;
		colors.push(color);
		g2d.setColor(color);
		return true;
	}

	public Color popColor() {
		if (colors.size() == 1) return colors.peek();
		var color = colors.pop();
		g2d.setColor(colors.peek());
		return color;
	}

	public boolean pushFont(final Font font) {
		if (font == null) return false;
		fonts.push(font);
		g2d.setFont(font);
		return true;
	}

	public Font popFont() {
		if (fonts.size() == 1) return fonts.peek();
		var font = fonts.pop();
		g2d.setFont(fonts.peek());
		return font;
	}

	public static final class SpritePanel extends JPanel {
		private final BufferedImage buffer;

		public SpritePanel(BufferedImage buffer) {
			this.buffer = buffer;
			setPreferredSize(new Dimension(buffer.getWidth(), buffer.getHeight()));
			setBackground(Color.BLACK);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2d = (Graphics2D) g;

			int panelWidth = getWidth();
			int panelHeight = getHeight();
			int bufferWidth = buffer.getWidth();
			int bufferHeight = buffer.getHeight();

			// Calculate scaling while keeping aspect ratio
			double scaleX = (double) panelWidth / bufferWidth;
			double scaleY = (double) panelHeight / bufferHeight;
			double scale = Math.min(scaleX, scaleY);

			int scaledWidth = (int) (bufferWidth * scale);
			int scaledHeight = (int) (bufferHeight * scale);

			// To center the image
			int offsetX = (panelWidth - scaledWidth) / 2;
			int offsetY = (panelHeight - scaledHeight) / 2;

			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2d.drawImage(buffer, offsetX, offsetY, scaledWidth, scaledHeight, null);
		}
	}
}
