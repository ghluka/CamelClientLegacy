package me.ghluka.camel.module.modules.hud

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.utils.dsl.drawText
import cc.polyfrost.oneconfig.utils.dsl.getTextWidth
import cc.polyfrost.oneconfig.utils.dsl.mc
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import cc.polyfrost.oneconfig.utils.dsl.scale
import cc.polyfrost.oneconfig.utils.dsl.translate
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.config.Font
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import kotlin.math.ceil
import kotlin.math.max

class TargetHud : me.ghluka.camel.module.Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Target HUD"
        @Exclude
        const val CATEGORY = "HUD"
    }
    
    @HUD(name = MODULE, category = CATEGORY, subcategory = MODULE)
    var targetHud = TargetHud()

    init {
        initialize()
    }

    override fun save() {
        moduleEnabled = targetHud.isEnabled
        super.save()
    }

    class TargetHud : BasicHud(
        false,
        1920 - 140f,
        1080 - 70f,
        1f,
        true,
        true,
        8f,
        5f,
        5f,
        OneColor(0, 0, 0, 120),
        false,
        2f,
        OneColor(0, 0, 0)
    ) {
        @Color(name = "Text Color")
        var color = OneColor(255, 255, 255)

        @Color(name = "Health Text Color")
        var healthColor = OneColor(255, 0, 0)

        @Slider(
            name = "Rotation",
            min = 0F,
            max = 360F,
        )
        var rotation = 30

        @Exclude
        private var nametagExtend = 0

        @Transient private var drawBackground = false
        @Transient var renderingNametag = false
            private set

        override fun shouldDrawBackground() = drawBackground

        @Exclude
        var entityName = ""

        override fun draw(matrices: UMatrixStack?, x: Float, y: Float, scale: Float, example: Boolean) {
            if (drawBackground) {
                entityName = ""
                return
            }
            GlStateManager.pushMatrix()
            GlStateManager.enableDepth()
            drawBackground = true
            try {
                drawAll(matrices, example)
            } finally {
                drawBackground = false
            }

            try {
                var entity = mc.objectMouseOver.entityHit
                if (entity == null)
                    entity = mc.pointedEntity
                if (entity == null && example)
                    entity = mc.thePlayer

                if (entity == null || entity !is EntityPlayer) {
                    GlStateManager.disableDepth()
                    GlStateManager.popMatrix()
                    entityName = ""
                    return
                }

                GlStateManager.color(1f, 1f, 1f, 1f)

                renderLiving(entity as EntityLivingBase, matrices, x, y, scale, rotation)

                RenderHelper.disableStandardItemLighting()
                GlStateManager.disableRescaleNormal()
                GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit)
                GlStateManager.disableTexture2D()
                GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit)

                entityName = entity.name
                nanoVG(true) {
                    translate(x, y)
                    scale(scale, scale)
                    drawText(
                        entity.name,
                        36, 6,
                        color.rgb,
                        12f,
                        Font.stringToFont(MainMod.moduleManager.font.font, false)
                    )
                    drawText(
                        ceil(entity.health).toInt().toString(),
                        36, 24,
                        healthColor.rgb,
                        24f,
                        Font.stringToFont(MainMod.moduleManager.font.font, true)
                    )
                }

                GlStateManager.popMatrix()
            }
            catch (_ : Exception) {}
        }

        private fun renderLiving(ent: EntityLivingBase, matrices: UMatrixStack?, x: Float, y: Float, scale: Float, rotation: Int) {
            GlStateManager.enableColorMaterial()
            GlStateManager.pushMatrix()

            GlStateManager.translate(x.toDouble() + (40 * scale), y.toDouble() + (107 + nametagExtend) * scale, 50.0)
            GlStateManager.scale(-(scale * 50), scale * 50, scale * 50)
            GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f)

            val f = ent.renderYawOffset
            val f1 = ent.rotationYaw
            val f2 = ent.rotationPitch
            val f3 = ent.prevRotationYawHead
            val f4 = ent.rotationYawHead

            GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f)
            RenderHelper.enableStandardItemLighting()
            GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f)

            val actualRotation = 360F - rotation
            ent.renderYawOffset = actualRotation
            ent.rotationYaw = actualRotation
            ent.rotationYawHead = ent.rotationYaw
            ent.prevRotationYawHead = ent.rotationYaw

            GlStateManager.translate(0.0f, 0.0f, 0.0f)
            GlStateManager.scale(0.5f, 0.5f, 0.5f)
            GlStateManager.translate(-1.0f, 2.2f, 0.0f)

            val rendermanager = mc.renderManager
            rendermanager.playerViewX = 0f
            rendermanager.setPlayerViewY(180.0f)
            rendermanager.isRenderShadow = false
            renderingNametag = true
            rendermanager.doRenderEntity(ent, 0.0, 0.0, 0.0, 0.0f, 1.0f, false)
            renderingNametag = false
            rendermanager.isRenderShadow = true

            ent.renderYawOffset = f
            ent.rotationYaw = f1
            ent.rotationPitch = f2
            ent.prevRotationYawHead = f3
            ent.rotationYawHead = f4

            GlStateManager.popMatrix()
        }

        override fun getWidth(scale: Float, example: Boolean): Float {
            var width = 120 * scale
            try {
                nanoVG {
                    width = 36 * scale + paddingX + getTextWidth(entityName, 12f * scale, Font.stringToFont(MainMod.moduleManager.font.font, false))
                }
            } catch (_ : Exception) {}
            return max(120 * scale, width)
        }

        override fun getHeight(scale: Float, example: Boolean): Float = (50 + nametagExtend) * scale
    }
}