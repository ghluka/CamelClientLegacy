package me.ghluka.camel.module.modules.hypixel.arcade

import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import me.ghluka.camel.utils.RenderUtils
import net.minecraft.block.Block
import net.minecraft.block.BlockLever
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color


class HoleInTheWallESP : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Hole In The Wall ESP"
        @Exclude
        const val CATEGORY = "Hypixel Arcade"
    }

    @Exclude
    @Info(text = "Renders where you need to place (green) and break (red) blocks for for the game Hole In The Wall (/play arcade_hole_in_the_wall).", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable Hole In The Wall ESP", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        var levers: List<BlockPos> = findTwoClosestLevers()
        if (levers.isEmpty()) return

        val finale = findTwoClosestLevers().size == 1
        if (finale) {
            val sideLeverPos = when {
                mc.theWorld.getBlockState(levers.first().north(10)).block is BlockLever -> levers.first().north(10)
                mc.theWorld.getBlockState(levers.first().south(10)).block is BlockLever -> levers.first().south(10)
                mc.theWorld.getBlockState(levers.first().east(10)).block is BlockLever -> levers.first().east(10)
                mc.theWorld.getBlockState(levers.first().west(10)).block is BlockLever -> levers.first().west(10)
                else -> null
            }
            levers = listOfNotNull(
                levers.first(),
                sideLeverPos
            )
        }

        var found = false
        var x = 33
        var y_0 = 42
        var y_1 = 45
        var z = 1000
        var range = 14
        if (levers.first().x == 52) {
            x = 69
        }
        else if (levers.first().x == 104) {
            // finale pink
            x = 133
            y_0 = 32
            y_1 = 36
            range = 25
        } else if (levers.first().x == 198) {
            // finale yellow
            x = 194
            y_0 = 32
            y_1 = 36
            range = 25
        } else if (levers.first().z == -49) {
            // finale orange
            z = -20
            y_0 = 32
            y_1 = 36
            range = 25
        } else if (levers.first().z == 45) {
            // finale green
            z = 41
            y_0 = 32
            y_1 = 36
            range = 25
        } else if (levers.first().x != 36) {
            return
        }

        //for (lever in levers) {
            //RenderUtils.re(lever, Color.BLUE.rgb)
        //}

        if (z != 1000) {
            //println(z)
            for (block in BlockPos.getAllInBox(
                BlockPos(levers[1].x, y_1, z),
                BlockPos(levers[0].x, y_0, z - range)
            )) {
                if (mc.theWorld.getBlockState(block).block != Blocks.air) {
                    found = true
                    //println("$block : (${block.x},${block.y},${block.z})")
                    z = block.z
                    break
                }
            }
        } else {
            for (block in BlockPos.getAllInBox(
                BlockPos(x, y_1, levers[1].z),
                BlockPos(x - range, y_0, levers[0].z)
            )) {
                if (mc.theWorld.getBlockState(block).block != Blocks.air) {
                    found = true
                    //println("$block : (${block.x},${block.y},${block.z})")
                    x = block.x
                    break
                }
            }
        }
        if (!found) return

        //println(x)
        var incoming: Iterable<BlockPos>? = null
        if (z != 1000) {
            incoming = BlockPos.getAllInBox(
                BlockPos(levers[1].x, y_1, z),
                BlockPos(levers[0].x, y_0, z)
            )
        } else {
            incoming = BlockPos.getAllInBox(
                BlockPos(x, y_1, levers[1].z),
                BlockPos(x, y_0, levers[0].z)
            )
        }

        if (incoming == null) return
        for (incBlock in incoming) {
            var plrBlock: BlockPos?
            if (finale) {
                // we're in the finale
                if (z != 1000) {
                    plrBlock = BlockPos(incBlock.x, incBlock.y, if (levers.first().z == -49) -46 else 42)
                } else {
                    plrBlock = BlockPos(if (levers.first().x == 104) 107 else 195, incBlock.y, incBlock.z)
                }
            } else {
                // first round
                plrBlock = BlockPos(if (levers.first().x == 36) 34 else 54, incBlock.y, incBlock.z)
            }

            if (mc.theWorld.getBlockState(incBlock).block == Blocks.air &&
                mc.theWorld.getBlockState(plrBlock).block == Blocks.air
            ) {
                RenderUtils.re(plrBlock, Color.GREEN.rgb)
            } else if (mc.theWorld.getBlockState(incBlock).block != Blocks.air &&
                mc.theWorld.getBlockState(plrBlock).block != Blocks.air
            ) {
                RenderUtils.re(plrBlock, Color.RED.rgb)
            }
        }
    }

    fun findTwoClosestLevers(): List<BlockPos> {
        val player = mc.thePlayer ?: return emptyList()
        val world = mc.theWorld ?: return emptyList()

        val closestLever = BlockPos.getAllInBox(
            player.position.add(-20, -20, -20),
            player.position.add(20, 20, 20)
        )
            .filter { world.getBlockState(it).block is BlockLever }
            .minByOrNull { player.getDistanceSq(it) }
            ?: return emptyList()

        val leftPos = closestLever.north(6)
        val rightPos = closestLever.south(6)

        val sideLeverPos = when {
            world.getBlockState(leftPos).block is BlockLever -> leftPos
            world.getBlockState(rightPos).block is BlockLever -> rightPos
            else -> null
        }

        return listOfNotNull(closestLever, sideLeverPos)
    }

}