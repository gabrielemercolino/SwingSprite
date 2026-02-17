# SwingSprite

A lightweight sprite rendering library for Swing applications, designed for 2D game development.

## Features

- **Simple Canvas**: Off-screen buffer with automatic aspect ratio preservation
- **Sprite Loading**: Load images from classpath resources
- **Transformations**: Scale, rotate, and flip sprites with immutable operations
- **Stack-Based State**: Manage colors and fonts with push/pop operations
- **Transparency Support**: Full alpha channel support (ARGB format)
- **Pixel-Perfect**: NEAREST_NEIGHBOR interpolation by default for crisp pixel art
- **Lightweight**: No external dependencies, minimal overhead
- **Java 21+**: Modern Java features

## Installation

### Gradle (Kotlin DSL)

```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // from the main branch
    implementation("com.github.gabrielemercolino:SwingSprite:main-SNAPSHOT")
    // from a specific tag
    implementation("com.github.gabrielemercolino:SwingSprite:v1.0.0")
}
```

### Maven

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
<dependency>
    <groupId>com.github.gabrielemercolino</groupId>
    <artifactId>SwingSprite</artifactId>
    <version>v1.0.0</version>
</dependency>
</dependencies>
```

## Quick Start

```java
import com.github.gabrielemercolino.swingsprite.Sprite;
import com.github.gabrielemercolino.swingsprite.SpriteRenderer;

import javax.swing.JFrame;
import javax.swing.Timer;
import java.awt.Color;

public class Game {
    public static void main(String[] args) {
        // Create a 320x240 game canvas
        SpriteRenderer renderer = new SpriteRenderer(320, 240);

        // Setup window
        JFrame frame = new JFrame("Game");
        frame.setContentPane(renderer.getPanel());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        // Load a sprite
        Sprite player = Sprite.fromResource("player.png").orElseThrow();

        // Game loop using Swing Timer (~60 FPS)
        Timer timer = new Timer(16, e -> {
            // Clear the canvas
            renderer.clear(Color.BLACK);

            // Draw the sprite
            renderer.drawSprite(player, 100, 100);

            // Display the frame
            renderer.render();
        });
        timer.start();

        // Keep the application running, consider something better than this
        while (frame.isVisible()) Thread.sleep(100);
    }
}
```

## API Reference

### Sprite Class

A lightweight wrapper around `BufferedImage` with transformation methods.

| Method                                | Description                                                         |
|---------------------------------------|---------------------------------------------------------------------|
| `fromResource(String name)`           | Load a sprite from classpath resources (returns `Optional<Sprite>`) |
| `scale(double factor)`                | Scale by a uniform factor (NEAREST_NEIGHBOR)                        |
| `scale(int w, int h)`                 | Scale to specific dimensions (NEAREST_NEIGHBOR)                     |
| `scale(int w, int h, RenderingHints)` | Scale with custom interpolation                                     |
| `rotate(double radians)`              | Rotate around center (angle in radians)                             |
| `flipHorizontal()`                    | Mirror left-right                                                   |
| `flipVertical()`                      | Mirror top-bottom                                                   |

**All transformations return new Sprite instances (immutable).**

### SpriteRenderer Class

Main rendering canvas with off-screen buffer.

| Method                             | Description                                 |
|------------------------------------|---------------------------------------------|
| `getPanel()`                       | Returns the JPanel for display              |
| `clear(Color)`                     | Fill the entire buffer with a color         |
| `render()`                         | Trigger display update (call after drawing) |
| `drawSprite(Sprite, int x, int y)` | Draw a sprite at position                   |
| `drawText(String, int x, int y)`   | Draw text at position                       |
| `pushColor(Color)`                 | Push color for text drawing onto stack      |
| `popColor()`                       | Pop color from stack (restore previous)     |
| `pushFont(Font)`                   | Push font for text drawing onto stack       |
| `popFont()`                        | Pop font from stack (restore previous)      |

### SpritePanel Class

The display panel obtained from `getPanel()`. Automatically handles:

- Aspect ratio preservation
- Centering the rendered image
- NEAREST_NEIGHBOR scaling for pixel art

## Complementary Libraries

For input handling, consider using **[SwingInput](https://github.com/gabrielemercolino/SwingInput)**, a polling-based
input library designed to work well with SwingSprite.

## Important Notes

1. **Call `render()` after drawing** - The off-screen buffer is only displayed when you call `render()`.

2. **Sprite transformations create new instances** - This is by design for immutability. Cache transformed sprites if
   used repeatedly.

3. **Resource loading returns Optional** - Always check `isPresent()` or use `orElseThrow()`/`orElse()` when loading
   sprites.

4. **Stack operations** - `popColor()` and `popFont()` will not empty the stack completely; at least the default value
   is always kept.

## License

This project is licensed under the terms of the MIT license.
