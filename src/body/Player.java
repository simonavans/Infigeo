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

    private final ArrayList<BufferedImage> cubeIcons;
    private double gravity;
    private Point2D acceleration;
    private boolean isGrounded;

    private float overRotationDegrees;
    private int iconCycle;
    private final double initialPosY;

    public Player(Shape shape, ArrayList<BufferedImage> cubeIcons, Point2D position, float rotation, float scale, double gravity)
    {
        super(shape, cubeIcons.get(0), position, rotation, scale);

        this.cubeIcons = cubeIcons;
        this.gravity = gravity;
        acceleration = new Point2D.Double();
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
            acceleration = new Point2D.Double(0, acceleration.getY() - gravity);
            setRotationDegrees(getRotationDegrees() - ROTATE_SPEED_FALLING);
        }
        else if (overRotationDegrees != 0)
        {
            // If the player is slightly tilted on the platform it is grounded on,
            // correct the rotation of the player.
            if (overRotationDegrees <= -45)
            {
                overRotationDegrees = roundToMultiple((overRotationDegrees - ROTATE_SPEED_LANDING) % 90, ROTATE_SPEED_LANDING);
            }
            else
            {
                overRotationDegrees = roundToMultiple((overRotationDegrees + ROTATE_SPEED_LANDING) % 90, ROTATE_SPEED_LANDING);
            }
            setPosition(new Point2D.Double(position.getX(), position.getY() - 5));
            setRotationDegrees(overRotationDegrees);
        }
        else
        {
            acceleration = new Point2D.Double(0, acceleration.getY() - gravity);
        }

        setPosition(new Point2D.Double(position.getX(), position.getY() + acceleration.getY()));
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

    public void reset()
    {
        setPosition(new Point2D.Double(position.getX(), initialPosY));
        setRotationDegrees(0);

        isGrounded = false;
        overRotationDegrees = 0;
        acceleration = new Point2D.Double();
    }

    public void setGravity(double gravity)
    {
        this.gravity = gravity;
    }

    public double getAcceleration()
    {
        return acceleration.getY();
    }

    public boolean isGrounded()
    {
        return isGrounded;
    }

    public void setGrounded(boolean grounded)
    {
        isGrounded = grounded;
        if (isGrounded)
        {
            overRotationDegrees = getRotationDegrees() % 90;
            acceleration = new Point2D.Double(0, 0);
        }
    }

    private float roundToMultiple(float number, int multiple)
    {
        return multiple * Math.round(number / multiple);
    }
}
