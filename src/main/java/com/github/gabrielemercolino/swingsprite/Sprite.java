package com.github.gabrielemercolino.swingsprite;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import static java.lang.System.err;

/**
 * A lightweight sprite wrapper around {@link BufferedImage} for 2D game development.
 * <p>
 * This class provides an immutable representation of a 2D image with convenient
 * transformation methods. All transformations return new {@code Sprite} instances,
 * preserving the original image.
 * <p>
 * Sprites can be loaded from resources, transformed (scale, rotate, flip), and
 * rendered using {@link SpriteRenderer}.
 *
 * <p>Example usage:
 * <pre>{@code
 * // Load a sprite from resources
 * Optional<Sprite> player = Sprite.fromResource("player.png");
 *
 * // Transform the sprite
 * Sprite scaled = player.get().scale(2.0);
 * Sprite rotated = scaled.rotate(Math.PI / 2);
 * Sprite flipped = rotated.flipHorizontal();
 * }</pre>
 *
 * @author Gabriele Mercolino
 * @version 1.0.0
 */
public record Sprite(BufferedImage image) {

    /**
     * Loads a sprite from the classpath resources.
     * <p>
     * Searches for the image file in the classpath and returns an {@link Optional}
     * containing the sprite if found and successfully loaded.
     *
     * @param name the resource name/path (e.g., "player.png" or "images/enemy.png")
     * @return an {@code Optional<Sprite>} containing the loaded sprite, or empty if not found or error
     */
    public static Optional<Sprite> fromResource(final String name) {
        URL resource = Sprite.class.getClassLoader().getResource(name);
        if (resource == null) return Optional.empty();

        try {
            return Optional.of(new Sprite(ImageIO.read(resource)));
        } catch (IOException e) {
            err.println("Error while reading image");
            e.printStackTrace(err);
            return Optional.empty();
        }
    }

    /**
     * Scales the sprite by a uniform factor.
     * <p>
     * Uses NEAREST_NEIGHBOR interpolation, suitable for pixel art.
     *
     * @param factor the scaling factor (e.g., 2.0 for double size)
     * @return a new scaled sprite
     */
    public Sprite scale(double factor) {
        int newWidth = (int) (image.getWidth() * factor);
        int newHeight = (int) (image.getHeight() * factor);
        return scale(newWidth, newHeight);
    }

    /**
     * Scales the sprite to specific dimensions.
     * <p>
     * Uses NEAREST_NEIGHBOR interpolation, suitable for pixel art.
     *
     * @param targetWidth  the desired width in pixels
     * @param targetHeight the desired height in pixels
     * @return a new scaled sprite
     */
    public Sprite scale(int targetWidth, int targetHeight) {
        RenderingHints hints = new RenderingHints(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );
        return scale(targetWidth, targetHeight, hints);
    }

    /**
     * Scales the sprite to specific dimensions with custom rendering hints.
     * <p>
     * Allows specifying interpolation quality for scaling.
     *
     * @param targetWidth  the desired width in pixels
     * @param targetHeight the desired height in pixels
     * @param hints        rendering hints for scaling quality
     * @return a new scaled sprite
     */
    public Sprite scale(int targetWidth, int targetHeight, RenderingHints hints) {
        BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaled.createGraphics();
        g2d.setRenderingHints(hints);
        g2d.drawImage(image, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return new Sprite(scaled);
    }

    /**
     * Rotates the sprite around its center.
     * <p>
     * Calculates the bounding box to accommodate the rotated image.
     * Uses NEAREST_NEIGHBOR interpolation.
     *
     * @param radians the rotation angle in radians
     * @return a new rotated sprite
     */
    public Sprite rotate(double radians) {
        int width = image.getWidth();
        int height = image.getHeight();

        AffineTransform transform = new AffineTransform();
        transform.rotate(radians, width / 2.0, height / 2.0);

        // Calculate new bounding box dimensions after rotation
        int newWidth = (int) Math.ceil(Math.abs(width * Math.cos(radians)) + Math.abs(height * Math.sin(radians)));
        int newHeight = (int) Math.ceil(Math.abs(width * Math.sin(radians)) + Math.abs(height * Math.cos(radians)));

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2d.translate((newWidth - width) / 2.0, (newHeight - height) / 2.0);
        g2d.drawImage(image, transform, null);
        g2d.dispose();
        return new Sprite(rotated);
    }

    /**
     * Flips the sprite horizontally (mirror left-right).
     *
     * @return a new horizontally flipped sprite
     */
    public Sprite flipHorizontal() {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage flipped = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = flipped.createGraphics();
        g2d.drawImage(image, width, 0, 0, height, 0, 0, width, height, null);
        g2d.dispose();
        return new Sprite(flipped);
    }

    /**
     * Flips the sprite vertically (mirror top-bottom).
     *
     * @return a new vertically flipped sprite
     */
    public Sprite flipVertical() {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage flipped = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = flipped.createGraphics();
        g2d.drawImage(image, 0, height, width, 0, 0, 0, width, height, null);
        g2d.dispose();
        return new Sprite(flipped);
    }
}
