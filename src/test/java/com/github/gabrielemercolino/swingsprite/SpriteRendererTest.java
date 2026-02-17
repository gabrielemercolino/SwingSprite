package com.github.gabrielemercolino.swingsprite;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class SpriteRendererTest {
    static SpriteRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new SpriteRenderer(300, 200);
    }

    @Test
    void pushColor() {
        assertTrue(renderer.pushColor(Color.RED));
        renderer.popColor();
    }

    @Test
    void popColor() {
        assertEquals(Color.WHITE, renderer.popColor());
    }

    @Test
    void pushFont() {
        var currentFont = renderer.getPanel().getFont();
        assertTrue(renderer.pushFont(currentFont.deriveFont(10f)));
        renderer.popFont();
    }

    @Test
    void popFont() {
        assertNotNull(renderer.popFont());
    }

    @Test
    void drawSprite() {
        JFrame frame = createTestFrame(testName());

        var maybeSprite = Sprite.fromResource("boy_down_1.png");
        assertTrue(maybeSprite.isPresent());
        var sprite = maybeSprite.get();
        var flippedH = sprite.flipHorizontal();
        var flippedV = sprite.flipVertical();
        var rotated = sprite.rotate(Math.PI);
        var scaled = sprite.scale(2);

        renderer.clear(Color.DARK_GRAY);
        renderer.drawSprite(sprite, 50, 50);
        renderer.drawSprite(flippedH, 100, 50);
        renderer.drawSprite(flippedV, 150, 50);
        renderer.drawSprite(rotated, 50, 100);
        renderer.drawSprite(scaled, 100, 100);
        renderer.render();

        while (frame.isVisible()) Thread.onSpinWait();
    }

    @Test
    void drawText() {
        JFrame frame = createTestFrame(testName());

        renderer.clear(Color.BLACK);
        renderer.drawText("Hello world", 20, 20);
        renderer.render();

        while (frame.isVisible()) Thread.onSpinWait();
    }

    @Test
    void clear() {
        JFrame frame = createTestFrame(testName());

        renderer.clear(Color.RED);

        while (frame.isVisible()) Thread.onSpinWait();
    }

    private static String testName() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        if (stack.length > 2) return stack[2].getMethodName();
        return "Unknown method";
    }

    private static JFrame createTestFrame(final String testName) {
        var frame = new JFrame(testName);
        frame.setContentPane(renderer.getPanel());
        frame.pack();
        frame.setVisible(true);
        return frame;
    }
}