package hakuna.sprite;

/*
 * Copyright (c) 2011, 2012, 2013, 2014, 2015, 2016 Jake Gordon and contributors
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
/**
 * @see https://codeincomplete.com/posts/bin-packing/
 * @see https://github.com/jakesgordon/bin-packing/blob/master/js/packer.growing.js
 */
public class Packer {

    public Node root;

    public void fit(Node[] blocks) {
        root = new Node(blocks[0].sprite);
        for (Node block : blocks) {
            Node node = findNode(root, block.w, block.h);
            if (node != null) {
                block.fit = splitNode(node, block.w, block.h);
            } else {
                block.fit = growNode(block.w, block.h);
            }
        }
    }

    private Node findNode(Node root, int w, int h) {
        if (root.used) {
            Node right = findNode(root.right, w, h);
            return right != null ? right : findNode(root.down, w, h);
        } else if ((w <= root.w) && (h <= root.h)) {
            return root;
        } else {
            return null;
        }
    }

    private Node splitNode(Node node, int w, int h) {
        node.used = true;
        node.down = new Node(node.x, node.y + h, node.w, node.h - h);
        node.right = new Node(node.x + w, node.y, node.w - w, h);
        return node;
    }

    private Node growNode(int w, int h) {
        boolean canGrowDown = w <= root.w;
        boolean canGrowRight = h <= root.h;

        boolean shouldGrowRight = canGrowRight && (root.h >= root.w + w);
        boolean shouldGrowDown  = canGrowDown  && (root.w >= root.h + h);

        if (shouldGrowRight)
            return growRight(w, h);
        else if (shouldGrowDown)
            return growDown(w, h);
        else if (canGrowRight)
            return growRight(w, h);
        else if (canGrowDown)
            return growDown(w, h);
        else
            return null;
    }

    private Node growRight(int w, int h) {
        Node tmp = new Node(0, 0, root.w + w, root.h);
        tmp.used = true;
        tmp.down = root;
        tmp.right = new Node(root.w, 0, w, root.h);
        root = tmp;
        Node node = findNode(root, w, h);
        if (node != null) {
            return splitNode(node, w, h);
        }
        return null;
    }

    private Node growDown(int w, int h) {
        Node tmp = new Node(0, 0, root.w, root.h + h);
        tmp.used = true;
        tmp.down =  new Node(0, root.h, root.w, h);
        tmp.right = root;
        root = tmp;
        Node node = findNode(root, w, h);
        if (node != null) {
            return splitNode(node, w, h);
        }
        return null;
    }

}