package body;

import application.Main;
import interfaces.GraphicsEngine;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class Obstacle extends Body
{
    private final double scrollSpeed;
    private double currentScroll;
    private final GraphicsEngine callback;

    private boolean hasCollision;

    public Obstacle(Shape shape, BufferedImage texture, Point2D position, float rotation, float scale, GraphicsEngine callback)
    {
        super(shape, texture, position, rotation, scale);

        scrollSpeed = Main.LEVEL_SCROLL_SPEED;
        currentScroll = position.getX();
        this.callback = callback;

        calculateTransformAndShape();
    }

    @Override
    public void update(double deltaTime)
    {
        currentScroll -= deltaTime * scrollSpeed;
        setPosition(new Point2D.Double(currentScroll, position.getY()));

        if (!hasCollision && position.getX() > 625 && position.getX() <= 775) {
            callback.addCollisionToObstacle(this);
            hasCollision = true;
        }
        else if (hasCollision && position.getX() <= 625) {
            callback.removeCollisionFromObstacle(this);
            hasCollision = false;
        }
        else if (position.getX() < -shape.getBounds2D().getWidth())
        {
            callback.scheduleObstacleRemoval(this);
        }
    }
}
