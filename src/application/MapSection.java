package application;

import body.Obstacle;

import java.util.ArrayList;

public class MapSection {
    private Obstacle[][] obstacleMap;

    public MapSection(Obstacle[][] obstacleMap)
    {
        this.obstacleMap = obstacleMap;
    }

    public ArrayList<Obstacle> getColumn(int number)
    {
        ArrayList<Obstacle> obstacleColumn = new ArrayList<>();
        for (Obstacle[] obstacleRow : obstacleMap)
            obstacleColumn.add(obstacleRow[number]);
        return obstacleColumn;
    }

    public int getWidth()
    {
        return obstacleMap[0].length;
    }
}
