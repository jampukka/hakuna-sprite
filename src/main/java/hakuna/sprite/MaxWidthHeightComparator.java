package hakuna.sprite;

import java.util.Comparator;

public class MaxWidthHeightComparator implements Comparator<Node> {

    @Override
    public int compare(Node a, Node b) {
        return Math.max(b.w, b.h) - Math.max(a.w, a.h);
    }

}
