package application;

import body.Block;
import body.Obstacle;
import body.Spike;
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

    public static ArrayList<MapSection> loadMap(BufferedImage obstacleSprites, double canvasWidth, GraphicsEngine callback)
    {
        ArrayList<MapSection> mapSections = new ArrayList<>();

        try
        {
            BufferedImage blockImage = obstacleSprites.getSubimage(0, 0, 75, 75);
            BufferedImage spikeUpImage = obstacleSprites.getSubimage(0, 76, 75, 75);
            BufferedImage spikeDownImage = obstacleSprites.getSubimage(0, 152, 75, 75);

            JsonReader reader = Json.createReader(Files.newInputStream(Paths.get(FILE_PATH)));
            JsonObject root = reader.readObject();

            JsonArray sections = root.getJsonArray("sections");

            // Loop through map sections
            for (JsonArray section : sections.getValuesAs(JsonArray.class))
            {
                // Get the map width by getting the first array's size
                // of the section.
                int mapWidth = section.getJsonArray(0).size();

                // A section is mapWidth wide and 10 high
                Obstacle[][] obstacleMap = new Obstacle[10][mapWidth];

                // Loop through all the rows of the section
                for (int y = 0; y < section.size(); y++)
                {
//                    JsonArray sectionRow = section.getJsonArray(section.size()-1-y);
                    JsonArray sectionRow = section.getJsonArray(y);

                    // Loop through all elements of one section row
                    for (int x = 0; x < sectionRow.size(); x++)
                    {
                        // Generate a different obstacle based on the number stored in the section
                        switch (sectionRow.getJsonNumber(x).intValue())
                        {
                            case 1:
                                obstacleMap[y][x] = new Block(
                                        blockImage,
                                        new Point2D.Double(canvasWidth + x * 75, 270 + y * 75),
                                        1,
                                        callback
                                );
                                break;
                            case 2:
                                obstacleMap[y][x] = new Spike(
                                        spikeUpImage,
                                        new Point2D.Double(canvasWidth + x * 75, 270 + y * 75),
                                        1,
                                        callback
                                );
                                break;
                            case 3:
                                obstacleMap[y][x] = new Spike(
                                        spikeDownImage,
                                        new Point2D.Double(canvasWidth + x * 75, 270 + y * 75),
                                        1,
                                        callback
                                );
                                break;
                        }
                    }
                }
                mapSections.add(new MapSection(obstacleMap, mapWidth));
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        return mapSections;
    }
}
