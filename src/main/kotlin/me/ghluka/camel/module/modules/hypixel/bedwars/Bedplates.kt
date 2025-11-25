package me.ghluka.camel.module.modules.hypixel.bedwars

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import net.minecraft.block.BlockBed
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.BlockPos
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.*
import kotlin.collections.HashSet

class Bedplates : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Bedplates"
        @Exclude
        const val DESCRIPTION = "Shows bed defenses through walls."
        @Exclude
        const val CATEGORY = "Hypixel Bedwars"
    }

    @Exclude
    @Info(text = DESCRIPTION, subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(name = "Max Render Distance", category = CATEGORY, subcategory = MODULE, min = 1F, max = 8F)
    var maxDistance: Int = 6

    @Slider(name = "Item Scale", category = CATEGORY, subcategory = MODULE, min = 0.1F, max = 1.5F)
    var itemScale: Float = 0.6f

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @SubscribeEvent
    fun onRenderWorld(event: RenderWorldLastEvent) {
        if (!moduleEnabled || mc.thePlayer == null || mc.theWorld == null) return

        val player = mc.thePlayer
        val world = mc.theWorld
        val playerPos = BlockPos(player)

        for (pos in BlockPos.getAllInBox(
            playerPos.add(-maxDistance, -maxDistance, -maxDistance),
            playerPos.add(maxDistance, maxDistance, maxDistance)
        )) {
            if (world.getBlockState(pos).block is BlockBed) {
                renderBedAura(world, pos, event.partialTicks)
            }
        }
    }

    private fun getSurroundingBlocks(world: World, origin: BlockPos, maxDistance: Int): Set<BlockPos> {
        val result = HashSet<BlockPos>()
        val queue: Queue<BlockPos> = LinkedList()

        val directions = arrayOf(
            BlockPos(1, 0, 0),
            BlockPos(-1, 0, 0),
            BlockPos(0, 0, 1),
            BlockPos(0, 0, -1),
            BlockPos(0, 1, 0),
        )

        queue.add(origin)
        result.add(origin)

        while (queue.isNotEmpty()) {
            val pos = queue.poll()

            for (dir in directions) {
                val next = pos.add(dir)
                if (next in result) continue
                if (next.distanceSq(origin) > maxDistance * maxDistance) continue
                if (world.isAirBlock(next)) continue

                val block = world.getBlockState(next).block
                if (!isValidDefenseBlock(block)) continue

                result.add(next)
                queue.add(next)
            }
        }
        return result
    }

    private fun isValidDefenseBlock(block: net.minecraft.block.Block): Boolean {
        return when {
            block.material == net.minecraft.block.material.Material.water -> true
            block == net.minecraft.init.Blocks.wool -> true
            block is net.minecraft.block.BlockLadder -> true
            block is net.minecraft.block.BlockPlanks -> true
            block is net.minecraft.block.BlockLog -> true
            block is net.minecraft.block.BlockStainedGlass -> true
            block == net.minecraft.init.Blocks.stained_hardened_clay -> true
            block is net.minecraft.block.BlockObsidian -> true
            block == net.minecraft.init.Blocks.end_stone -> true
            else -> false
        }
    }

    private fun renderBedAura(world: World, bedPos: BlockPos, partialTicks: Float) {
        val blocks = getSurroundingBlocks(world, bedPos, maxDistance)
        val mc = Minecraft.getMinecraft()
        val renderer = mc.renderItem

        val rx = mc.renderManager.viewerPosX
        val ry = mc.renderManager.viewerPosY
        val rz = mc.renderManager.viewerPosZ

        GlStateManager.disableDepth()
        GlStateManager.disableLighting()
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(770, 771)

        for (pos in blocks) {
            val state = world.getBlockState(pos)
            val block = state.block

            val item = Item.getItemFromBlock(block) ?: continue
            val meta = block.getMetaFromState(state)
            val stack = ItemStack(block, 1, meta)

            GlStateManager.pushMatrix()

            GlStateManager.translate(
                pos.x + 0.5 - rx,
                pos.y + 0.5 - ry,
                pos.z + 0.5 - rz
            )

            GlStateManager.scale(itemScale.toDouble(), itemScale.toDouble(), itemScale.toDouble())
            GlStateManager.color(1f, 1f, 1f, 1f)

            renderer.renderItem(stack, ItemCameraTransforms.TransformType.GROUND)

            GlStateManager.popMatrix()
        }

        GlStateManager.enableDepth()
        GlStateManager.enableLighting()
        GlStateManager.disableBlend()
    }
}
