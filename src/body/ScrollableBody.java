package body;

import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class ScrollableBody extends Body
{
    private double scrollSpeed;
    private double currentScroll;
    private final double textureWidth;
    private final double textureHeight;
    private final double textureSpanX;
    private final double textureSpanY;

    public ScrollableBody(BufferedImage texture, double scrollSpeed, double textureWidth, double textureHeight, double textureSpanX, double textureSpanY)
    {
        super(new Rectangle2D.Double(), texture, new Point2D.Double(), 0, 1);

        this.scrollSpeed = scrollSpeed;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.textureSpanX = textureSpanX;
        this.textureSpanY = textureSpanY;

        shape = new Rectangle2D.Double(0, 0, textureSpanX, textureSpanY);
        calculateTransformAndShape();
    }

    @Override
    public void update(double deltaTime)
    {
        currentScroll -= deltaTime * scrollSpeed;
    }

    @Override
    public void draw(FXGraphics2D graphics)
    {
        graphics.setPaint(new TexturePaint(
                texture,
                new Rectangle2D.Double(currentScroll, 0, textureWidth, textureHeight)
        ));
        graphics.fill(shape);
    }
}
