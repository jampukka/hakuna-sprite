package hakuna.sprite;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

public class SpriteGenerator {

    public static void main(String[] args) throws IOException {
        File input = new File(args[0]);
        List<Icon> icons = parse(input);
        pack(icons);

        String name = getSpriteName(input.getName());
        File dir = input.getParentFile();

        BufferedImage sprite = createSprite(icons);
        ImageIO.write(sprite, "png", new File(dir, name + ".png"));
        try (OutputStream out = new FileOutputStream(new File(dir, name + ".json"))) {
            writeIndex(icons, out);
        }

        List<Icon> highDPI = icons.stream()
                .map(icon -> highDPI(dir, icon))
                .collect(Collectors.toList());
        BufferedImage sprite2x = createSprite(highDPI);
        ImageIO.write(sprite2x, "png", new File(dir, name + "@2x.png"));
        try (OutputStream out = new FileOutputStream(new File(dir, name + "@2x.json"))) {
            writeIndex(highDPI, out);
        }
    }

    public static List<Icon> parse(File input) throws IOException {
        return Files.readAllLines(input.toPath(), StandardCharsets.UTF_8).stream()
                .map(l -> l.trim())
                .filter(l -> !l.isEmpty())
                .filter(l -> l.charAt(0) != '#')
                .map(l -> parse(input.getParentFile(), l))
                .collect(Collectors.toList());
    }

    private static String getSpriteName(String name) {
        int i = name.lastIndexOf('.');
        return i < 0 ? name : name.substring(0, i);
    }

    private static Icon parse(File dir, String line) {
        String[] splitted = line.split(" ");
        Icon icon = new Icon();
        icon.setName(splitted[0]);
        if (splitted.length > 1) {
            icon.setWidth(Integer.parseInt(splitted[1]));
        }
        if (splitted.length > 1) {
            icon.setHeight(Integer.parseInt(splitted[2]));
        }
        if (splitted.length > 3) {
            icon.setPixelRatio(Integer.parseInt(splitted[3]));
        }
        BufferedImage bi = transcode(dir, icon);
        icon.setBi(bi);
        if (bi != null) {
            icon.setWidth(bi.getWidth());
            icon.setHeight(bi.getHeight());
        }
        return icon;
    }

    private static BufferedImage transcode(File dir, Icon icon) {
        File in = new File(dir, icon.getName() + ".svg");
        if (!in.canRead()) {
            return null;
        }
        return transcode(in, icon.getWidth(), icon.getHeight());
    }

    private static BufferedImage transcode(File file, int width, int height) {
        String svgURI = file.toURI().toString();
        TranscoderInput input = new TranscoderInput(svgURI);

        BufferedImageTranscoder t = new BufferedImageTranscoder();
        if (width != 0) {
            t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(width));
        }
        if (height != 0) {
            t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(height));
        }

        try {
            t.transcode(input, null);
            return t.getBufferedImage();
        } catch (TranscoderException e) {
            throw new RuntimeException(e);
        }
    }

    public static void pack(List<Icon> icons) {
        Node[] blocks = new Node[icons.size()];
        int i = 0;
        for (Icon icon : icons) {
            blocks[i++] = new Node(icon);
        }
        Arrays.sort(blocks, new MaxWidthHeightComparator());

        new Packer().fit(blocks);
        for (Node block : blocks) {
            block.sprite.setX(block.fit.x);
            block.sprite.setY(block.fit.y);
        }
    }

    public static BufferedImage createSprite(List<Icon> icons) throws IOException {
        int width = 0;
        int height = 0;
        for (Icon icon : icons) {
            int w = icon.getX() + icon.getWidth();
            if (w > width) {
                width = w;
            }
            int h = icon.getY() + icon.getHeight();
            if (h > height) {
                height = h;
            }
        }

        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        for (Icon icon : icons) {
            draw(g2d, icon);
        }
        g2d.dispose();
        return bi;
    }

    private static void draw(Graphics2D g2d, Icon icon) {
        BufferedImage bi = icon.getBi();
        int x = icon.getX();
        int y = icon.getY();
        int w = icon.getWidth();
        int h = icon.getHeight();
        g2d.drawImage(bi, x, y, w, h, null);
    }

    public static Icon highDPI(File dir, Icon icon) {
        Icon highDPI = new Icon();
        highDPI.setName(icon.getName());
        highDPI.setPixelRatio(icon.getPixelRatio() * 2);
        highDPI.setX(icon.getX() * 2);
        highDPI.setY(icon.getY() * 2);
        highDPI.setWidth(icon.getWidth() * 2);
        highDPI.setHeight(icon.getHeight() * 2);
        highDPI.setBi(transcode(dir, highDPI));
        return highDPI;
    }

    public static void writeIndex(List<Icon> icons, OutputStream out) throws IOException {
        try (JsonGenerator json = new JsonFactory().createGenerator(out)) {
            json.setPrettyPrinter(new DefaultPrettyPrinter());
            json.writeStartObject();
            for (Icon icon : icons) {
                icon.toIndex(json);
            }
            json.writeEndObject();
        }
    }

}
