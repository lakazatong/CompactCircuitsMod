package net.lakazatong.compactcircuitsmod.utils;

public enum Direction {
    NORTH(0), SOUTH(1), EAST(2), WEST(3), UP(4), DOWN(5);

    public final int side;

    private static final Direction[] sideToDirection = values();
    private static final Direction[] opposites = {SOUTH, NORTH, WEST, EAST, DOWN, UP};
    private static final Direction[] clockwise = {EAST, WEST, SOUTH, NORTH, UP, DOWN};
    private static final Direction[] counterClockwise = {WEST, EAST, NORTH, SOUTH, UP, DOWN};
    private static final net.minecraft.core.Direction[] minecraftDirections = {
            net.minecraft.core.Direction.NORTH,
            net.minecraft.core.Direction.SOUTH,
            net.minecraft.core.Direction.EAST,
            net.minecraft.core.Direction.WEST,
            net.minecraft.core.Direction.UP,
            net.minecraft.core.Direction.DOWN
    };
    private static final Direction[] myDirections = { DOWN, UP, NORTH, SOUTH, WEST, EAST };

    Direction(int side) {
        this.side = side;
    }

    public Direction opposite() {
        return opposites[this.side];
    }

    public Direction clockwise() {
        return clockwise[this.side];
    }

    public Direction counterClockwise() {
        return counterClockwise[this.side];
    }

    public net.minecraft.core.Direction toMinecraftDirection() {
        return minecraftDirections[this.side];
    }

    // I want the FRONT, BACK, RIGHT or LEFT direction given that I face this way
    public Direction toAbsolute(Direction facing) {
        if (this == Direction.UP || this == Direction.DOWN) return this;
        if (facing == Direction.NORTH) return this;
        if (facing == Direction.SOUTH) return this.opposite();
        if (facing == Direction.EAST) return this.clockwise();
        if (facing == Direction.WEST) return this.counterClockwise();
        return this;
    }

    public Direction toAbsolute(net.minecraft.core.Direction facing) {
        return toAbsolute(fromMinecraftDirection(facing));
    }

    // align the direction given that I face this way
    public Direction toRelative(Direction facing) {
        if (this == Direction.UP || this == Direction.DOWN) return this;
        if (facing == Direction.NORTH) return this;
        if (facing == Direction.SOUTH) return this.opposite();
        if (facing == Direction.EAST) return this.counterClockwise();
        if (facing == Direction.WEST) return this.clockwise();
        return this;
    }

    public Direction toRelative(net.minecraft.core.Direction facing) {
        return toRelative(fromMinecraftDirection(facing));
    }

    public static Direction fromSide(int side) {
        return sideToDirection[side];
    }

    public static Direction fromMinecraftDirection(net.minecraft.core.Direction direction) {
        return myDirections[direction.get3DDataValue()];
    }
}
