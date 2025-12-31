package com.github.gabrielemercolino.swingsprite;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import static java.lang.System.err;

public record Sprite(BufferedImage image) {
	public static Sprite fromResource(final String name) {
		URL resource = Sprite.class.getClassLoader().getResource(name);
		if (resource == null) return null;

		try {
			return new Sprite(ImageIO.read(resource));
		} catch (IOException e) {
			err.println("Error while reading image");
			e.printStackTrace(err);
			return null;
		}
	}
}
