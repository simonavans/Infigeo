package body;

import interfaces.GraphicsEngine;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Block extends Obstacle {
    public Block(BufferedImage sprite, Point2D position, float scale, GraphicsEngine callback) {
        super(new Rectangle2D.Double(0, 0, 75, 75), sprite, position, scale, callback);

        calculateTransformAndShape();
    }
}
