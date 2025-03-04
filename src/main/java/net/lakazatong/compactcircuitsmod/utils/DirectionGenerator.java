package net.lakazatong.compactcircuitsmod.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DirectionGenerator implements Iterator<Direction> {
    private int currentIndex;
    private final List<Direction> directions;

    public DirectionGenerator(Direction startDirection) {
        this.currentIndex = 0;
        this.directions = new ArrayList<>();
        directions.add(startDirection);
        directions.add(startDirection.opposite());
        directions.add(startDirection.clockwise());
        directions.add(startDirection.counterClockwise());
        directions.add(Direction.UP);
        directions.add(Direction.DOWN);
    }

    @Override
    public boolean hasNext() {
        return currentIndex < directions.size();
    }

    @Override
    public Direction next() {
        if (!hasNext()) {
            throw new IllegalStateException("No more directions to yield.");
        }
        return directions.get(currentIndex++);
    }
}

