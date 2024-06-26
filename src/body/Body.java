package body;

import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public abstract class Body
{
    protected Shape shape;
    protected BufferedImage sprite;
    protected Point2D position;
    protected float rotation;
    protected float scale;

    protected AffineTransform transform;
    protected Shape transformedShape;

    public Body(Shape shape, BufferedImage sprite, Point2D position, float rotation, float scale)
    {
        this.shape = shape;
        this.sprite = sprite;
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public abstract void update(double deltaTime);

    public void draw(FXGraphics2D graphics)
    {
        graphics.drawImage(sprite, transform, null);
    }

    protected void calculateTransformAndShape()
    {
        transform = new AffineTransform();
        double anchorX = shape.getBounds2D().getWidth() / 2;
        double anchorY = shape.getBounds2D().getHeight() / 2;

        transform.translate(position.getX(), position.getY());
        transform.rotate(rotation, anchorX, anchorY);
        transform.scale(scale,scale);
        transformedShape = transform.createTransformedShape(shape);
    }

    public AffineTransform getTransform()
    {
        return transform;
    }

    public Shape getTransformedShape()
    {
        return transformedShape;
    }

    public Point2D getPosition()
    {
        return position;
    }

    public void setPosition(Point2D position)
    {
        this.position = position;
        calculateTransformAndShape();
    }

    public float getRotation()
    {
        return rotation;
    }

    public void setRotation(float rotation)
    {
        this.rotation = rotation;
        if (this.rotation < 0) this.rotation += Math.PI * 2;
        else if (this.rotation >= Math.PI * 2) this.rotation -= Math.PI * 2;

        calculateTransformAndShape();
    }

    public float getRotationDegrees()
    {
        return (float) Math.toDegrees(rotation);
    }

    public void setRotationDegrees(float rotation)
    {
        this.rotation = (float) Math.toRadians(rotation);
        if (this.rotation < 0) this.rotation += Math.PI * 2;
        else if (this.rotation >= Math.PI * 2) this.rotation -= Math.PI * 2;

        calculateTransformAndShape();
    }

    public void setScale(float scale)
    {
        this.scale = scale;
        calculateTransformAndShape();
    }
}
