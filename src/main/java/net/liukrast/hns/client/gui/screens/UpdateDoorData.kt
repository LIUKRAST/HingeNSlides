package net.liukrast.hns.client.gui.screens

import net.liukrast.hns.HNSConstants
import net.liukrast.hns.registry.HNSItems
import net.liukrast.hns.world.level.block.DoorBlock
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.neoforged.neoforge.server.ServerLifecycleHooks
import net.swzo.brass.ui.kit.net.*
import java.util.*

data class DoorPos(val x: Int, val y: Int, val z: Int) {
    fun toBlockPos() = BlockPos(x,y,z)
    companion object { fun of(p: BlockPos) = DoorPos(p.x, p.y, p.z) }
}

data class DoorUpdate(val pos: DoorPos, val values: Map<String, Any>)
data class DoorRange(val min: Float, val max: Float, val step: Float)
data class DoorSnapshot(
    val values: Map<String, Any>,
    val options: Map<String, List<String>>,
    val categories: Map<String, String>,
    val ranges: Map<String, DoorRange>,
)

private sealed interface Resolved {
    class Ok(val level: ServerLevel, val pos: BlockPos, val door: DoorBlock<*>) : Resolved
    class Fail(val code: String) : Resolved
}

private fun resolve(ctx: BrassActionContext, p: DoorPos): Resolved {
    val server = ServerLifecycleHooks.getCurrentServer() ?: return Resolved.Fail("door.missing")
    val id = ctx.playerId ?: return Resolved.Fail("door.missing")
    val player = server.playerList.getPlayer(UUID.fromString(id)) ?: return Resolved.Fail("door.missing")
    val pos = p.toBlockPos()
    val level = player.serverLevel()

    if(player.distanceToSqr(pos.center) > 64) return Resolved.Fail("door.too_far")
    if(!level.isLoaded(pos)) return Resolved.Fail("door.missing")
    if(!player.isHolding(HNSItems.WRENCH.get())) return Resolved.Fail("door.no_wrench")

    val door = level.getBlockState(pos).block as? DoorBlock<*> ?: return Resolved.Fail("door.missing")
    return Resolved.Ok(level, pos, door)
}

@BrassActionSet
object DoorActions : BrassActions {
    val query = brassAction<DoorPos>(
        id = HNSConstants.MOD_ID + ".door.query",
        permission = HNSConstants.MOD_ID + ".door.query",
        minOpLevel = 0
    ) { ctx, p ->
        val r = resolve(ctx, p)
        if(r !is Resolved.Ok) return@brassAction err((r as Resolved.Fail).code)
        val door = r.door
        ok(DoorSnapshot(
            door.fetchData(r.level, r.pos),
            door.fetchSchema(),
            door.fetchCategories(),
            door.fetchRanges().mapValues { (_, v) -> DoorRange(v.min(), v.max(), v.step()) },
        ))
    }

    val update = brassAction<DoorUpdate>(
        id = HNSConstants.MOD_ID + ".door.update",
        permission = HNSConstants.MOD_ID + ".door.update",
        minOpLevel = 0
    ) { ctx, input ->
        val r = resolve(ctx, input.pos)
        if(r !is Resolved.Ok) return@brassAction err((r as Resolved.Fail).code)
        if(!r.door.updateData(r.level, r.pos, input.values)) return@brassAction err("door.invalid_update")
        ok(r.door.fetchData(r.level, r.pos))
    }
}