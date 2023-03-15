package application;

import body.*;
import interfaces.GraphicsEngine;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.jfree.fx.FXGraphics2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application implements GraphicsEngine
{
    public static final double LEVEL_SCROLL_SPEED = 800;

    private Canvas canvas;
    private Label mousePosLog;

    private ArrayList<ScrollableBody> scrollableBodies;
    private ScrollableBody ground;
    private Player player;
    private ArrayList<Obstacle> obstacles;
    private ArrayList<Obstacle> obstaclesWithCollision;
    private ArrayList<Obstacle> obstaclesToRemove;

    private ArrayList<BufferedImage> cubeIcons;
    private ArrayList<BufferedImage> obstacleSprites;

    private double obstacleSpawnTime;

    @Override
    public void init()
    {
        canvas = new Canvas(1920, 1080);

        try
        {
            BufferedImage cubeIconSheet = ImageIO.read(getClass().getResource("/icons_cube.png"));
            cubeIcons = new ArrayList<>();
            for (int i = 0; i < 10; i++)
                cubeIcons.add(cubeIconSheet.getSubimage(0, (i * 75) + i, 75, 75));

            BufferedImage obstacleSpriteSheet = ImageIO.read(getClass().getResource("/obstacles.png"));
            obstacleSprites = new ArrayList<>();
            for (int i = 0; i < 3; i++)
                obstacleSprites.add(obstacleSpriteSheet.getSubimage(0, (i * 75) + i, 75, 75));

            scrollableBodies = new ArrayList<>();
            BufferedImage backgroundImage = ImageIO.read(getClass().getResource("/background.png"));
            scrollableBodies.add(new ScrollableBody(
                    backgroundImage,
                    200,
                    backgroundImage.getWidth() * 2,
                    canvas.getHeight(),
                    canvas.getWidth(),
                    canvas.getHeight()
            ));
            ground = new ScrollableBody(
                    ImageIO.read(getClass().getResource("/ground.png")),
                    LEVEL_SCROLL_SPEED,
                    400,
                    270,
                    canvas.getWidth(),
                    270
            );
            scrollableBodies.add(ground);

            player = new Player(
                    new Rectangle2D.Double(0, 0, 75, 75),
                    cubeIcons,
                    new Point2D.Double(700, canvas.getHeight()/3),
                    0,
                    1,
                    2
            );

            obstacles = new ArrayList<>();
            obstaclesToRemove = new ArrayList<>();
            obstaclesWithCollision = new ArrayList<>();

            FXGraphics2D graphics = new FXGraphics2D(canvas.getGraphicsContext2D());
            graphics.scale(1, -1);
            graphics.translate(0, -canvas.getHeight());

            canvas.setFocusTraversable(true);
            canvas.setOnKeyPressed(e ->
            {
                if (e.getCode() == KeyCode.SPACE) player.jump();
                else if (e.getCode() == KeyCode.E) player.cycleIcon();
                //todo debugging
                else if (e.getCode() == KeyCode.RIGHT) { update(1 / 60d); draw(graphics); }
            });
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXGraphics2D graphics = new FXGraphics2D(canvas.getGraphicsContext2D());
        graphics.scale(1, -1);
        graphics.translate(0, -canvas.getHeight());

        mousePosLog = new Label("X: 0\t\tY: 0");
        canvas.setOnMouseMoved(e -> mousePosLog.setText("X: " + e.getX() + "\t\tY: " + e.getY()));
        BorderPane mainPane = new BorderPane(canvas, mousePosLog, null, null, null);

        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setTitle("Infigeo");
        primaryStage.show();

//        new AccumulationTimer(60, this, graphics);
    }

    @Override
    public void update(double deltaTime)
    {
        // Update textures of ScrollableBodies
        for (ScrollableBody scrollableBody : scrollableBodies)
            scrollableBody.update(deltaTime);

        // Spawn obstacles at random intervals
        if (obstacleSpawnTime <= 0)
        {
            obstacles.add(new Block(
                    obstacleSprites,
                    new Point2D.Double(canvas.getWidth(), 270),
                    1,
                    this
            ));
            obstacles.add(new Block(
                    obstacleSprites,
                    new Point2D.Double(canvas.getWidth() + 75, 270),
                    1,
                    this
            ));
//            obstacles.add(new Block(
//                    obstacleSprites,
//                    new Point2D.Double(canvas.getWidth() + 75, 345),
//                    1,
//                    this
//            ));
            obstacleSpawnTime = 2 + Math.random();
        }
        obstacleSpawnTime -= deltaTime;

        // Update obstacles
        for (Obstacle obstacle : obstacles)
            obstacle.update(deltaTime);

        // Remove obstacles which are out of bounds
        for (Obstacle obstacle : obstaclesToRemove)
            obstacles.remove(obstacle);

        // Create an Area which amounts to the surface of
        // all elements in objectsWithCollision
        Area collisionArea = new Area();
        Area spikeArea = new Area();

        if (obstaclesWithCollision.size() > 0)
        {
            for (Obstacle obstacle : obstaclesWithCollision)
            {
                if (obstacle instanceof Block)
                    collisionArea.add(new Area(obstacle.getTransformedShape()));
                else if (obstacle instanceof Spike)
                    spikeArea.add(new Area(obstacle.getTransformedShape()));
            }

            Area fatalCollisionArea = (Area) collisionArea.clone();
            fatalCollisionArea.intersect(new Area(player.getTransformedShape()));

            // If the player's Area and fatalCollisionArea's Area overlap,
            // then the player is dead.
            Rectangle2D collisionShape = fatalCollisionArea.getBounds2D();
            if (collisionShape.getWidth() > 14)
            {
                System.out.println(collisionShape.getWidth());
//                gameOver();
//                return;
            }
        }

        // Update the player
        player.update(deltaTime);

        spikeArea.intersect(new Area(player.getTransformedShape()));
        if (!spikeArea.isEmpty())
        {
            gameOver();
            return;
        }

        // Create an Area that combines collisionArea and
        // the Area of the ground
        collisionArea.add(new Area(ground.getTransformedShape()));
        collisionArea.intersect(new Area(player.getTransformedShape()));

        // Check for non-fatal collisions
        if (!collisionArea.isEmpty() && player.getAcceleration() < 0)
        {
            // Correct the player's position to be right above the platform
            // which it is grounded on.
            player.setPosition(new Point2D.Double(
                    player.getPosition().getX(),
                    player.getPosition().getY() + collisionArea.getBounds2D().getHeight()
            ));
            player.setGrounded(true);
        }
    }

    @Override
    public void draw(FXGraphics2D graphics)
    {
        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());

        for (ScrollableBody scrollableBody : scrollableBodies)
            scrollableBody.draw(graphics);

        player.draw(graphics);

        for (Obstacle obstacle : obstacles)
            obstacle.draw(graphics);
    }

    @Override
    public void addCollisionToObstacle(Obstacle obstacle)
    {
        obstaclesWithCollision.add(obstacle);
    }

    @Override
    public void removeCollisionFromObstacle(Obstacle obstacle)
    {
        obstaclesWithCollision.remove(obstacle);
    }

    @Override
    public void scheduleObstacleRemoval(Obstacle obstacle)
    {
        obstaclesToRemove.add(obstacle);
    }

    private void gameOver()
    {
        obstaclesToRemove.clear();
        obstaclesWithCollision.clear();
        obstacles.clear();
        obstacleSpawnTime = 2 + Math.random();
        player.reset();
    }

    public static void main(String[] args)
    {
        launch(Main.class);
    }
}