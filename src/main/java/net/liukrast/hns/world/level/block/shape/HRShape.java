package net.liukrast.hns.world.level.block.shape;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

public class HRShape {

    private final Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);

    public HRShape(VoxelShape northShape) {
        shapes.put(Direction.NORTH, northShape);
        shapes.put(Direction.EAST,  rotate(northShape, 1));
        shapes.put(Direction.SOUTH, rotate(northShape, 2));
        shapes.put(Direction.WEST,  rotate(northShape, 3));
    }

    public VoxelShape get(Direction dir) {
        VoxelShape shape = shapes.get(dir);
        return shape != null ? shape : shapes.get(Direction.NORTH);
    }

    private static VoxelShape rotate(VoxelShape shape, int times) {
        VoxelShape[] buffer = { shape, Shapes.empty() };

        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((x1, y1, z1, x2, y2, z2) ->
                    buffer[1] = Shapes.or(buffer[1],
                            Shapes.box(1 - z2, y1, x1, 1 - z1, y2, x2))
            );
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }
}