package body;

import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends Body
{
    private ArrayList<BufferedImage> cubeIcons;
    private double gravity;
    private Point2D acceleration;
    private boolean isGrounded;

    private float overRotationDegrees;
    private int iconCycle;

    public Player(Shape shape, ArrayList<BufferedImage> cubeIcons, Point2D position, float rotation, float scale, double gravity)
    {
        super(shape, cubeIcons.get(0), position, rotation, scale);

        this.cubeIcons = cubeIcons;
        this.gravity = gravity;
        acceleration = new Point2D.Double();
        isGrounded = false;
        calculateTransformAndShape();
    }

    @Override
    public void update(double deltaTime)
    {
        if (!isGrounded)
        {
            setPosition(new Point2D.Double(position.getX(), position.getY() + acceleration.getY()));
            setRotationDegrees(getRotationDegrees() - 6);
            acceleration = new Point2D.Double(0, acceleration.getY() - gravity);
        }
        else if (overRotationDegrees != 0)
        {
            if (overRotationDegrees <= -45)
            {
                overRotationDegrees = roundToMultiple((overRotationDegrees - 12) % 90, 12);
            }
            else
            {
                overRotationDegrees = roundToMultiple((overRotationDegrees + 12) % 90, 12);
            }
            setPosition(new Point2D.Double(position.getX(), position.getY() - 30));
            setRotationDegrees(overRotationDegrees);
        }
    }

    public void jump()
    {
        if (!isGrounded) return;

        overRotationDegrees = 0;
        acceleration = new Point2D.Double(0, 28);
        isGrounded = false;
    }

    public void cycleIcon()
    {
        iconCycle = (iconCycle + 1) % cubeIcons.size();
        texture = cubeIcons.get(iconCycle);
    }

    public void setGravity(double gravity)
    {
        this.gravity = gravity;
    }

    public double getAcceleration()
    {
        return acceleration.getY();
    }

    public void setGrounded(boolean grounded)
    {
        overRotationDegrees = getRotationDegrees() % 90;
        isGrounded = grounded;
    }

    private float roundToMultiple(float number, int multiple)
    {
        return multiple * Math.round(number / multiple);
    }
}
