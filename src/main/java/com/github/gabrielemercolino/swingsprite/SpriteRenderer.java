package com.github.gabrielemercolino.swingsprite;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Stack;

/**
 * Simplified 2D rendering canvas for Swing applications.
 * <p>
 * This class provides an off-screen buffer for rendering sprites and text,
 * with automatic aspect ratio preservation and scaling when displayed.
 * <p>
 * The renderer uses a stack-based system for managing colors and fonts,
 * allowing temporary changes that can be easily reverted.
 * <p>
 * The rendered output is displayed through a {@link JPanel} obtained via
 * {@link #getPanel()}, which handles scaling and centering automatically.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Create a 320x240 game canvas
 * SpriteRenderer renderer = new SpriteRenderer(320, 240);
 *
 * // Add to a JFrame
 * JFrame frame = new JFrame("Game");
 * frame.setContentPane(renderer.getPanel());
 * frame.pack();
 * frame.setVisible(true);
 *
 * // Load and draw a sprite
 * Sprite sprite = Sprite.fromResource("player.png").orElseThrow();
 * renderer.clear(Color.BLACK);
 * renderer.drawSprite(sprite, 100, 100);
 * renderer.render(); // Display the frame
 * }</pre>
 *
 * @author Gabriele Mercolino
 * @version 1.0.0
 */
public final class SpriteRenderer {
    private final SpritePanel panel;
    private final BufferedImage buffer;
    private final Graphics2D g2d;
    private final Stack<Color> colors;
    private final Stack<Font> fonts;

    /**
     * Creates a new sprite renderer with the specified buffer dimensions.
     * <p>
     * The buffer uses ARGB format to support transparency.
     * Default color is white, default font is Monospaced 16pt.
     *
     * @param width  the logical width of the rendering buffer
     * @param height the logical height of the rendering buffer
     */
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

    /**
     * Returns the panel for displaying the rendered output.
     * <p>
     * This panel should be added to a container (e.g., {@code JFrame.setContentPane()}).
     * The panel automatically handles scaling and aspect ratio preservation.
     *
     * @return the display panel
     */
    public SpritePanel getPanel() {
        return panel;
    }

    /**
     * Clears the entire buffer with the specified color.
     *
     * @param color the fill color
     */
    public void clear(final Color color) {
        g2d.setColor(color);
        g2d.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
        g2d.setColor(colors.peek());
    }

    /**
     * Triggers a repaint of the display panel.
     * <p>
     * Call this after all drawing operations to display the rendered frame.
     */
    public void render() {
        panel.repaint();
    }

    /**
     * Draws a sprite at the specified position.
     *
     * @param sprite the sprite to draw
     * @param x      the x coordinate
     * @param y      the y coordinate
     */
    public void drawSprite(final Sprite sprite, final int x, final int y) {
        g2d.drawImage(sprite.image(), x, y, null);
    }

    /**
     * Draws text at the specified position.
     * <p>
     * Uses the current font and color from the stacks.
     *
     * @param text the text to draw
     * @param x    the x coordinate (baseline position)
     * @param y    the y coordinate (baseline position)
     */
    public void drawText(final String text, final int x, final int y) {
        g2d.drawString(text, x, y);
    }

    /**
     * Pushes a new color onto the stack and applies it.
     *
     * @param color the color to push
     * @return true if color was pushed successfully (not null)
     */
    public boolean pushColor(final Color color) {
        if (color == null) return false;
        colors.push(color);
        g2d.setColor(color);
        return true;
    }

    /**
     * Pops the current color from the stack and restores the previous one.
     * <p>
     * The stack always keeps at least one color (default white).
     *
     * @return the popped color, or current color if stack would be empty
     */
    public Color popColor() {
        if (colors.size() == 1) return colors.peek();
        var color = colors.pop();
        g2d.setColor(colors.peek());
        return color;
    }

    /**
     * Pushes a new font onto the stack and applies it.
     *
     * @param font the font to push
     * @return true if font was pushed successfully (not null)
     */
    public boolean pushFont(final Font font) {
        if (font == null) return false;
        fonts.push(font);
        g2d.setFont(font);
        return true;
    }

    /**
     * Pops the current font from the stack and restores the previous one.
     * <p>
     * The stack always keeps at least one font (default Monospaced 16pt).
     *
     * @return the popped font, or current font if stack would be empty
     */
    public Font popFont() {
        if (fonts.size() == 1) return fonts.peek();
        var font = fonts.pop();
        g2d.setFont(fonts.peek());
        return font;
    }

    /**
     * Panel that displays the rendered buffer with automatic scaling.
     * <p>
     * Maintains aspect ratio when the panel is resized, centering the image
     * and using NEAREST_NEIGHBOR interpolation for pixel-perfect display.
     */
    public static final class SpritePanel extends JPanel {
        private final BufferedImage buffer;

        private SpritePanel(BufferedImage buffer) {
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
