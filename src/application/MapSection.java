package application;

import body.Obstacle;

import java.util.ArrayList;

public class MapSection {
    private final Obstacle[][] obstacleMap;
    private final int mapWidth;

    public MapSection(Obstacle[][] obstacleMap, int mapWidth)
    {
        this.obstacleMap = obstacleMap;
        this.mapWidth = mapWidth;
    }

    public ArrayList<Obstacle> getColumn(int number)
    {
        ArrayList<Obstacle> obstacleColumn = new ArrayList<>();
        for (Obstacle[] obstacleRow : obstacleMap)
        {
            Obstacle obstacle = obstacleRow[number];
            if (obstacle != null)
            {
                obstacleColumn.add(obstacle.getCloned());
            }
        }

        return obstacleColumn;
    }

    public int getWidth()
    {
        return mapWidth;
    }
}
