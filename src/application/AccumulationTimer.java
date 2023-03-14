package application;

import interfaces.GraphicsEngine;
import javafx.animation.AnimationTimer;
import org.jfree.fx.FXGraphics2D;

public class AccumulationTimer extends AnimationTimer
{
    private final GraphicsEngine graphicsEngine;
    private final FXGraphics2D graphics;
    private double refreshRate;
    private long last;
    private double accumulatedTime;

    public AccumulationTimer(double framesPerSecond, GraphicsEngine graphicsEngine, FXGraphics2D graphics)
    {
        this.graphicsEngine = graphicsEngine;
        this.graphics = graphics;
        this.refreshRate = 1d / framesPerSecond;
        this.last = -1;
        this.start();
    }

    @Override
    public void handle(long now)
    {
        if (this.last == -1) this.last = now;

        accumulatedTime += (now - last) / 1e9;

        while(accumulatedTime > refreshRate)
        {
            graphicsEngine.update(refreshRate);
            graphicsEngine.draw(graphics);
            accumulatedTime -= refreshRate;
        }
        last = now;
    }
}
