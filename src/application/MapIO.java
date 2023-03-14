package application;

import body.Block;
import body.Obstacle;
import interfaces.GraphicsEngine;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MapIO
{
    private static final String FILE_PATH = "resources/maps/stereo_madness.json";

    public static ArrayList<MapSection> loadMap(BufferedImage obstacleSprites, double canvasWidth)
    {
        ArrayList<MapSection> mapSections = new ArrayList<>();

        try
        {
            BufferedImage blockImage = obstacleSprites.getSubimage(0, 0, 75, 75);
            BufferedImage spikeUpImage = obstacleSprites.getSubimage(0, 76, 75, 75);
            BufferedImage spikeDownImage = obstacleSprites.getSubimage(0, 152, 75, 75);
            GraphicsEngine callback = Main.class.newInstance();

            JsonReader reader = Json.createReader(Files.newInputStream(Paths.get(FILE_PATH)));
            JsonObject root = reader.readObject();

            JsonArray sections = root.getJsonArray("sections");
            for (JsonArray section : sections.getValuesAs(JsonArray.class))
            {
                int mapWidth = section.getJsonArray(0).size();
                int[][] sectionData = new int[10][mapWidth];
                Obstacle[][] obstacleMap = new Obstacle[10][mapWidth];
                for (int y = 0; y < section.size(); y++)
                {
                    JsonArray sectionRow = section.getJsonArray(y);
                    for (int x = 0; x < sectionRow.size(); x++)
                    {
                        sectionData[y][x] = sectionRow.getJsonNumber(x).intValue();
                        switch (sectionData[y][x])
                        {
                            case 1:
                                obstacleMap[y][x] = new Block(
                                        blockImage,
                                        new Point2D.Double(canvasWidth + 75, y * 75),
                                        1,
                                        callback
                                );
                                break;
                            case 2:
                                obstacleMap[y][x] = new Block(
                                        spikeUpImage,
                                        new Point2D.Double(canvasWidth + 75, y * 75),
                                        1,
                                        callback
                                );
                                break;
                            case 3:
                                obstacleMap[y][x] = new Block(
                                        spikeDownImage,
                                        new Point2D.Double(canvasWidth + 75, y * 75),
                                        1,
                                        callback
                                );
                                break;
                        }
                    }
                }
                mapSections.add(new MapSection(obstacleMap));
            }
        }
        catch (IOException | InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }

        return mapSections;
    }
}
