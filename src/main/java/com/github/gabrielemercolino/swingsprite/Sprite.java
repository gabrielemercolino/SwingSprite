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

public record Sprite(BufferedImage image) {
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

    public Sprite scale(double factor) {
        int newWidth = (int) (image.getWidth() * factor);
        int newHeight = (int) (image.getHeight() * factor);
        return scale(newWidth, newHeight);
    }

    public Sprite scale(int targetWidth, int targetHeight) {
        RenderingHints hints = new RenderingHints(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR
        );
        return scale(targetWidth, targetHeight, hints);
    }

    public Sprite scale(int targetWidth, int targetHeight, RenderingHints hints) {
        BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaled.createGraphics();
        g2d.setRenderingHints(hints);
        g2d.drawImage(image, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return new Sprite(scaled);
    }

    public Sprite rotate(double radians) {
        int width = image.getWidth();
        int height = image.getHeight();

        AffineTransform transform = new AffineTransform();
        transform.rotate(radians, width / 2.0, height / 2.0);

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

    public Sprite flipHorizontal() {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage flipped = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = flipped.createGraphics();
        g2d.drawImage(image, width, 0, 0, height, 0, 0, width, height, null);
        g2d.dispose();
        return new Sprite(flipped);
    }

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
