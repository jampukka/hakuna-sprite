package hakuna.sprite;

public class Node {

    public Icon sprite;
    public int x;
    public int y;
    public int w;
    public int h;
    public boolean used;
    public Node right;
    public Node down;
    public Node fit;

    public Node(Icon sprite) {
        this(0, 0, sprite.getWidth(), sprite.getHeight());
        this.sprite = sprite;
    }

    public Node(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

}