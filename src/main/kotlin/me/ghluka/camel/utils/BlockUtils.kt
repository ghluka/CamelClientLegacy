package me.ghluka.camel.utils

import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.util.*
import org.apache.commons.lang3.RandomUtils
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.function.Predicate


class BlockUtils {
    companion object {
        fun getClosestBlock(radius: Int, height: Int, depth: Int, predicate: Predicate<in BlockPos>): BlockPos? {
            val player: EntityPlayerSP = mc.thePlayer
            val playerPos = BlockPos(
                Math.floor(player.posX).toInt(),
                Math.floor(player.posY).toInt() + 1,
                Math.floor(player.posZ).toInt()
            )
            val vec3Top = Vec3i(radius, height, radius)
            val vec3Bottom = Vec3i(radius, depth, radius)
            var closest: BlockPos? = null
            for (blockPos in BlockPos.getAllInBox(playerPos.subtract(vec3Bottom), playerPos.add(vec3Top))) {
                if (predicate.test(blockPos)) {
                    if (closest == null || player.getDistanceSq(blockPos) < player.getDistanceSq(closest)) {
                        closest = blockPos
                    }
                }
            }
            return closest
        }
    }
}