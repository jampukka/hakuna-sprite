package hakuna.sprite;

import java.awt.image.BufferedImage;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;

public class Icon {

    private String name;
    private int width;
    private int height;
    private int x;
    private int y;
    private int pixelRatio = 1;
    private BufferedImage bi;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getPixelRatio() {
        return pixelRatio;
    }

    public void setPixelRatio(int pixelRatio) {
        this.pixelRatio = pixelRatio;
    }

    public BufferedImage getBi() {
        return bi;
    }

    public void setBi(BufferedImage bi) {
        this.bi = bi;
    }

    public void toIndex(JsonGenerator json) throws IOException {
        json.writeFieldName(name);
        json.writeStartObject();
        json.writeNumberField("width", width);
        json.writeNumberField("height", height);
        json.writeNumberField("x", x);
        json.writeNumberField("y", y);
        json.writeNumberField("pixelRatio", pixelRatio);
        json.writeEndObject();
    }

}
