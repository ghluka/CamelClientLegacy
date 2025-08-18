package me.ghluka.camel.module.modules.hypixel.arcade

import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.Module
import net.minecraft.block.Block
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.item.EntityFallingBlock
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraft.world.World
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.opengl.GL11
import java.util.*
import kotlin.math.floor


class SpiderMazePathfinder : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Spider Maze Pathfinder"
        @Exclude
        const val CATEGORY = "Hypixel Arcade"
    }

    @Exclude
    @Info(text = "Shows the solution for the Spider Maze in the game Party Games (/play party_games).", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable $MODULE", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @Exclude
    private var currentPath: MutableList<BlockPos?>? = null

    @Exclude
    val MIN_X: Int = -24
    @Exclude
    val MAX_X: Int = 114
    @Exclude
    val Y: Int = 4
    @Exclude
    val MIN_Z: Int = 2029
    @Exclude
    val MAX_Z: Int = 2168

    @Exclude
    val DIRS: Array<IntArray> = arrayOf<IntArray>(
        intArrayOf(1, 0), intArrayOf(-1, 0), intArrayOf(0, 1), intArrayOf(0, -1)
    )

    fun findPath(start: BlockPos?, end: BlockPos?, world: World): MutableList<BlockPos?> {
        if (!isWalkable(end, world)) return Collections.emptyList()

        val queue: Queue<BlockPos?> = ArrayDeque<BlockPos?>()
        val parent: MutableMap<BlockPos?, BlockPos?> = HashMap()

        queue.add(start)
        parent.put(start, null)

        while (!queue.isEmpty()) {
            val current: BlockPos? = queue.poll()
            if (current == end) {
                return reconstructPath(parent, current)
            }
            if (current != null) {
                for (d in DIRS) {
                    val next = BlockPos(current.x + d[0], Y, current.z + d[1])

                    if (!inBounds(next)) continue
                    if (!isWalkable(next, world)) continue
                    if (parent.containsKey(next)) continue  // visited


                    parent.put(next, current)
                    queue.add(next)
                }
            }
        }

        return Collections.emptyList()
    }

    private fun isWalkable(pos: BlockPos?, world: World): Boolean {
        return world.isAirBlock(pos) && world.isAirBlock(pos?.up())
    }

    private fun inBounds(pos: BlockPos): Boolean {
        return pos.getX() >= MIN_X && pos.getX() <= MAX_X && pos.getZ() >= MIN_Z && pos.getZ() <= MAX_Z && pos.getY() == Y
    }

    private fun reconstructPath(parent: MutableMap<BlockPos?, BlockPos?>, end: BlockPos?): MutableList<BlockPos?> {
        val path: LinkedList<BlockPos?> = LinkedList()
        var at = end
        while (at != null) {
            path.addFirst(at)
            at = parent.get(at)
        }
        return path
    }

    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent?) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        if (event == null) return

        val playerPos = BlockPos(
            floor(mc.thePlayer.posX),
            4.0,
            floor(mc.thePlayer.posZ)
        )

        val goal = BlockPos(44, 4, 2099)

        val path: MutableList<BlockPos?> = findPath(playerPos, goal, mc.theWorld)
        currentPath = path

        if (currentPath == null || currentPath!!.isEmpty()) return

        val player: EntityPlayer = mc.thePlayer
        val px = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks
        val py = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks
        val pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks

        GL11.glPushMatrix()
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT or GL11.GL_LINE_BIT)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_LIGHTING)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glLineWidth(3.0f)
        GL11.glDepthMask(false)

        // Move coordinates so world vertices are relative to camera
        GL11.glTranslated(-px, -py, -pz)
        val tess = Tessellator.getInstance()
        val wr = tess.worldRenderer
        wr.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR)

        // draw each node center (y slightly above ground)
        for (pos in currentPath!!) {
            val x = pos!!.x + 0.5
            val y = pos.y + 0.05
            val z = pos.z + 0.5
            wr.pos(x, y, z).color(1.0f, 0.0f, 0.0f, 0.9f).endVertex()
        }
        tess.draw()

        // restore GL state
        GL11.glDepthMask(true)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_LIGHTING)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glPopAttrib()
        GL11.glPopMatrix()
    }
}