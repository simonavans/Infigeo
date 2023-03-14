package body;

import interfaces.GraphicsEngine;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Spike extends Obstacle {
    public Spike(ArrayList<BufferedImage> obstacleSprites, Point2D position, float scale, GraphicsEngine callback) {
        super(new Rectangle2D.Double(), obstacleSprites.get(1), position, scale, callback);

        GeneralPath triangle = new GeneralPath();
        triangle.moveTo(0, 0);
        triangle.lineTo(75, 0);
        triangle.lineTo(37.5, 70);
        triangle.lineTo(0, 0);
        triangle.closePath();
        shape = triangle;

        calculateTransformAndShape();
    }
}
