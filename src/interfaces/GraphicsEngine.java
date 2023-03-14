package interfaces;

import org.jfree.fx.FXGraphics2D;

public interface GraphicsEngine
{
    void update(double deltaTime);
    void draw(FXGraphics2D graphics);
}

