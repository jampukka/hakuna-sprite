package hakuna.sprite;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Before;
import org.junit.Test;

import hakuna.sprite.Icon;
import hakuna.sprite.SpriteGenerator;

public class SpriteGeneratorTest {

    private List<Icon> icons;

    @Before
    public void setup() throws URISyntaxException, IOException {
        File input = getResourceAsFile("test.input");
        icons = SpriteGenerator.parse(input);
    }

    @Test
    public void testParse() {
        assertEquals(3, icons.size());
        assertEquals("swamp", icons.get(0).getName());
        assertEquals("cross", icons.get(1).getName());
        assertEquals("plane", icons.get(2).getName());
        assertEquals(64, icons.get(0).getWidth());
        assertEquals(64, icons.get(0).getHeight());
        assertEquals(128, icons.get(1).getWidth());
        assertEquals(128, icons.get(1).getHeight());
        assertEquals(256, icons.get(2).getWidth());
        assertEquals(256, icons.get(2).getHeight());
    }

    @Test
    public void testPack() {
        assertEquals("swamp", icons.get(0).getName());
        assertEquals("cross", icons.get(1).getName());
        assertEquals("plane", icons.get(2).getName());
        assertEquals(0, icons.get(0).getX());
        assertEquals(0, icons.get(0).getY());
        assertEquals(0, icons.get(1).getX());
        assertEquals(0, icons.get(1).getY());
        assertEquals(0, icons.get(2).getX());
        assertEquals(0, icons.get(2).getY());
        SpriteGenerator.pack(icons);
        assertEquals("swamp", icons.get(0).getName());
        assertEquals("cross", icons.get(1).getName());
        assertEquals("plane", icons.get(2).getName());
        assertEquals(256, icons.get(0).getX());
        assertEquals(128, icons.get(0).getY());
        assertEquals(256, icons.get(1).getX());
        assertEquals(0, icons.get(1).getY());
        assertEquals(0, icons.get(2).getX());
        assertEquals(0, icons.get(2).getY());
    }

    @Test
    public void testCreateSprite() throws IOException {
        SpriteGenerator.pack(icons);
        BufferedImage actual = SpriteGenerator.createSprite(icons);
        BufferedImage expected;
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("test.png")) {
            expected = ImageIO.read(in);
        }
        assertEquals(expected.getWidth(), actual.getWidth());
        assertEquals(expected.getHeight(), actual.getHeight());
        for (int y = 0; y < actual.getHeight(); y++) {
            for (int x = 0; x < actual.getWidth(); x++) {
                assertEquals(expected.getRGB(x, y), actual.getRGB(x, y));
            }
        }
    }

    @Test
    public void testCreateIndex() throws IOException, URISyntaxException {
        SpriteGenerator.pack(icons);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SpriteGenerator.writeIndex(icons, baos);
        byte[] actuals = baos.toByteArray();
        byte[] expecteds = Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("test.json").toURI()));
        assertArrayEquals(expecteds, actuals);
    }

    private File getResourceAsFile(String name) throws URISyntaxException {
        return new File(getClass().getClassLoader().getResource(name).toURI());
    }

}
