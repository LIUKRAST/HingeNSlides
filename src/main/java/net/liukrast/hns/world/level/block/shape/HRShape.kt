package net.liukrast.hns.world.level.block.shape

import net.minecraft.core.Direction
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.EnumMap

class HRShape(northShape: VoxelShape) {

    private val shapes: MutableMap<Direction, VoxelShape> = EnumMap(Direction::class.java)

    init {
        shapes[Direction.NORTH] = northShape
        shapes[Direction.EAST] = rotate(northShape, 1)
        shapes[Direction.SOUTH] = rotate(northShape, 2)
        shapes[Direction.WEST] = rotate(northShape, 3)
    }

    operator fun get(dir: Direction): VoxelShape {
        return shapes[dir] ?: shapes.getValue(Direction.NORTH)
    }

    companion object {
        private fun rotate(shape: VoxelShape, times: Int): VoxelShape {
            var current = shape

            repeat(times) {
                var next = Shapes.empty()
                current.forAllBoxes { x1, y1, z1, x2, y2, z2 ->
                    next = Shapes.or(next, Shapes.box(1.0 - z2, y1, x1, 1.0 - z1, y2, x2))
                }
                current = next
            }

            return current
        }
    }
}