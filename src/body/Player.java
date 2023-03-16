package body;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player extends Body
{
    // The speed at which the cube rotates when jumping/falling
    private final int ROTATE_SPEED_FALLING = 6;
    // The speed at which the cube rotates when it lands
    private final int ROTATE_SPEED_LANDING = 12;
    private final int ROTATION_RATIO_LANDING = 10;

    private final ArrayList<BufferedImage> cubeIcons;
    private double gravity;
    private double yAcceleration;
    private boolean isGrounded;

    //todo debugging
//    private float overRotationDegrees;
    private float rotationIncrementDegrees;
    private int iconCycle;
    private final double initialPosY;

    public Player(Shape shape, ArrayList<BufferedImage> cubeIcons, Point2D position, float rotation, float scale, double gravity)
    {
        super(shape, cubeIcons.get(0), position, rotation, scale);

        this.cubeIcons = cubeIcons;
        this.gravity = gravity;
        initialPosY = position.getY();

        calculateTransformAndShape();
    }

    @Override
    public void update(double deltaTime)
    {
        if (!isGrounded)
        {
            // If the player is not grounded, increase the acceleration with gravity
            // and increment the rotation of the player
            yAcceleration -= gravity;
            setPosition(new Point2D.Double(position.getX(), position.getY() + yAcceleration));
            setRotationDegrees(getRotationDegrees() - ROTATE_SPEED_FALLING);
        }
        else if (getRotationDegrees() % 90 != 0)
        {
            float rotationDeg = Math.round(getRotationDegrees());

            if (
                    rotationDeg > 0 && rotationDeg < 45 ||
                    rotationDeg > 90 && rotationDeg < 135 ||
                    rotationDeg > 180 && rotationDeg < 225 ||
                    rotationDeg > 270 && rotationDeg < 315
            )
            {
                setRotationDegrees(rotationDeg - rotationIncrementDegrees);
            }
            else if (
                    rotationDeg >= 45 && rotationDeg < 90 ||
                            rotationDeg >= 135 && rotationDeg < 180 ||
                            rotationDeg >= 225 && rotationDeg < 270 ||
                            rotationDeg >= 315 && rotationDeg < 360
            )
            {
                setRotationDegrees(rotationDeg + rotationIncrementDegrees);
            }
        }
    }

    public void jump()
    {
        if (!isGrounded) return;

        yAcceleration = 28;
        isGrounded = false;
    }

    public void cycleIcon()
    {
        iconCycle = (iconCycle + 1) % cubeIcons.size();
        texture = cubeIcons.get(iconCycle);
    }

    public void reset()
    {
        setPosition(new Point2D.Double(position.getX(), initialPosY));
        setRotationDegrees(0);

        isGrounded = false;
        yAcceleration = 0;
    }

    public void setGravity(double gravity)
    {
        this.gravity = gravity;
    }

    public double getyAcceleration()
    {
        return yAcceleration;
    }

    public boolean isGrounded()
    {
        return isGrounded;
    }

    public void ground(double endY)
    {
        isGrounded = true;
        yAcceleration = 0;

        setRotationDegrees(roundToMultiple(getRotationDegrees(), 9));
        rotationIncrementDegrees = 9;
        setPosition(new Point2D.Double(position.getX(), endY));
    }

    public void unGround()
    {
        isGrounded = false;
    }

    private float roundToMultiple(float number, int multiple)
    {
        return multiple * Math.round(number / multiple);
    }
}
