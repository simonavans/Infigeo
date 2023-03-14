package body;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class Obstacle extends Body
{
    public Obstacle(Shape shape, BufferedImage texture, Point2D position, float rotation, float scale)
    {
        super(shape, texture, position, rotation, scale);

        calculateTransformAndShape();
    }

    @Override
    public void update(double deltaTime)
    {

    }
}
