package application;

import body.*;
import interfaces.GraphicsEngine;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import org.jfree.fx.FXGraphics2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class Main extends Application implements GraphicsEngine
{
    public static final double LEVEL_SCROLL_SPEED = 920;

    private Canvas canvas;
    private Label mousePosLog;

    private ArrayList<ScrollableBody> scrollableBodies;
    private ScrollableBody ground;
    private Player player;
    public ArrayList<Obstacle> obstacles;
    private ArrayList<Obstacle> obstaclesWithCollision;

    private ArrayList<MapSection> mapSections;
    private ArrayList<BufferedImage> cubeIcons;
    private ArrayList<BufferedImage> obstacleSprites;

    private MediaPlayer mediaPlayer;

    private double obstacleSpawnTime;
    private int currentMapColumn;
    private int currentMapSection;
    private boolean isPaused;

    //todo debugging
    private Rectangle2D debugShape;

    @Override
    public void init()
    {
        canvas = new Canvas(1920, 1080);

        try
        {
            this.mapSections = MapIO.loadMap(ImageIO.read(getClass().getResource("/obstacles.png")), canvas.getWidth(), this);
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
                    50,
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
                    2.2
            );

            obstacles = new ArrayList<>();
            obstaclesWithCollision = new ArrayList<>();

            FXGraphics2D graphics = new FXGraphics2D(canvas.getGraphicsContext2D());
            graphics.scale(1, -1);
            graphics.translate(0, -canvas.getHeight());

            canvas.setFocusTraversable(true);
            canvas.setOnKeyPressed(e ->
            {
                if (e.getCode() == KeyCode.SPACE)
                    player.setHoldingJumpButton(true);
                else if (e.getCode() == KeyCode.E)
                    player.cycleIcon();
                //todo debugging
                else if (e.getCode() == KeyCode.RIGHT) { update(1 / 60d); draw(graphics); }
            });
            canvas.setOnKeyReleased(e ->
            {
                if (e.getCode() == KeyCode.SPACE)
                    player.setHoldingJumpButton(false);
            });
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        FXGraphics2D graphics = new FXGraphics2D(canvas.getGraphicsContext2D());
        graphics.scale(1, -1);
        graphics.translate(0, -canvas.getHeight());

        mousePosLog = new Label("X: 0\t\tY: 0");
        canvas.setOnMouseMoved(e -> mousePosLog.setText("X: " + e.getX() + "\t\tY: " + e.getY()));
        BorderPane mainPane = new BorderPane(canvas, mousePosLog, null, null, null);

        primaryStage.setScene(new Scene(mainPane));
        primaryStage.setTitle("Infigeo");
        primaryStage.show();

        this.mediaPlayer = new MediaPlayer(new Media(
                Paths.get("resources/sound/StereoMadness.mp3").toUri().toString())
        );
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.play();
        primaryStage.setOnCloseRequest(windowEvent -> {
            mediaPlayer.stop();
        });

        new AccumulationTimer(60, this, graphics);
    }

    @Override
    public void update(double deltaTime)
    {
        if (isPaused) return;

        // Update textures of ScrollableBodies
        for (ScrollableBody scrollableBody : scrollableBodies)
            scrollableBody.update(deltaTime);

        if (obstacleSpawnTime <= 0)
        {
            obstacles.addAll(mapSections.get(currentMapSection).getColumn(currentMapColumn));

            currentMapColumn = (currentMapColumn + 1) % mapSections.get(currentMapSection).getWidth();
            if (currentMapColumn == 0) currentMapSection = (currentMapSection + 1) % mapSections.size();
            obstacleSpawnTime = 4 * deltaTime;
        }
        obstacleSpawnTime -= deltaTime;

        // Update obstacles
        for (Obstacle obstacle : obstacles) {
            if (obstacle != null)
                obstacle.update(deltaTime);
        }
        obstacles.removeIf(Objects::isNull);

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
            if (collisionShape.getHeight() > 25)
            {
                gameOver();
                return;
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
        if (collisionArea.isEmpty())
        {
            if (player.isGrounded() && Math.round(player.getRotationDegrees() % 90) == 0)
                player.unGround();
        }
        else if (player.getYAcceleration() < 0)
        {
            player.ground(collisionArea.getBounds2D().getY() + collisionArea.getBounds2D().getHeight());
        }
    }

    @Override
    public void draw(FXGraphics2D graphics)
    {
        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, (int) canvas.getWidth(), (int) canvas.getHeight());

        for (ScrollableBody scrollableBody : scrollableBodies)
            scrollableBody.draw(graphics);

        if (!isPaused) player.draw(graphics);

        for (Obstacle obstacle : obstacles)
            obstacle.draw(graphics);

        if (debugShape != null)
        {
            graphics.setColor(Color.RED);
            graphics.fill(debugShape);
            graphics.setColor(Color.BLACK);
        }
    }

    //todo debugging
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
        obstacles.set(obstacles.indexOf(obstacle), null);
    }

    private void gameOver()
    {
        obstacleSpawnTime = 0;
        currentMapColumn = 0;
        isPaused = true;

        mediaPlayer.stop();
        mediaPlayer.setAutoPlay(false);
        mediaPlayer = new MediaPlayer(new Media(
                Paths.get("resources/sound/explode_11.mp3").toUri().toString())
        );
        mediaPlayer.play();
        mediaPlayer.setOnEndOfMedia(() -> {
            player.reset();
            isPaused = false;

            mediaPlayer = new MediaPlayer(new Media(
                    Paths.get("resources/sound/StereoMadness.mp3").toUri().toString())
            );
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.play();
        });

        obstaclesWithCollision.clear();
        obstacles.clear();
    }

    public static void main(String[] args)
    {
        launch(Main.class);
    }
}