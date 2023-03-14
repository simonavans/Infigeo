package interfaces;

import body.Obstacle;
import org.jfree.fx.FXGraphics2D;

public interface GraphicsEngine
{
    void update(double deltaTime);
    void draw(FXGraphics2D graphics);
    void addCollisionToObstacle(Obstacle obstacle);
    void removeCollisionFromObstacle(Obstacle obstacle);
    void scheduleObstacleRemoval(Obstacle obstacle);
}

