package body;

import interfaces.GraphicsEngine;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Block extends Obstacle {
    public Block(ArrayList<BufferedImage> obstacleSprites, Point2D position, float scale, GraphicsEngine callback) {
        super(new Rectangle2D.Double(0, 0, 75, 75), obstacleSprites.get(0), position, scale, callback);

        calculateTransformAndShape();
    }
}
