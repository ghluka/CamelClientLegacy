package me.ghluka.camel.module.modules.hud

import cc.polyfrost.oneconfig.config.annotations.Color
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.HUD
import cc.polyfrost.oneconfig.config.annotations.Text
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.hud.BasicHud
import cc.polyfrost.oneconfig.libs.universal.UMatrixStack
import cc.polyfrost.oneconfig.utils.dsl.drawText
import cc.polyfrost.oneconfig.utils.dsl.getTextWidth
import cc.polyfrost.oneconfig.utils.dsl.nanoVG
import cc.polyfrost.oneconfig.utils.dsl.scale
import cc.polyfrost.oneconfig.utils.dsl.translate
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.Module
import me.ghluka.camel.module.config.Font
import net.minecraft.client.renderer.GlStateManager

class Watermark : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Watermark"
        @Exclude
        const val CATEGORY = "HUD"
    }

    @HUD(name = MODULE, category = CATEGORY, subcategory = MODULE)
    var watermark = Watermark()

    init {
        initialize()
    }

    override fun save() {
        moduleEnabled = watermark.isEnabled
        super.save()
    }

    class Watermark : BasicHud(
        true,
        0f,
        0f,
        1f,
        false,
        false,
        8f,
        5f,
        5f,
        OneColor(0, 0, 0, 120),
        false,
        2f,
        OneColor(0, 0, 0)
    ) {
        @Color(name = "Watermark Color")
        var color = OneColor(244, 225, 185)

        @Color(name = "Build Color")
        var buildColor = OneColor(255, 255, 255)

        @Exclude
        private var nametagExtend = 0

        @Transient private var drawBackground = false
        @Transient var renderingNametag = false
            private set

        @Text(name = "Custom Build Text", category = CATEGORY, subcategory = MODULE,
            placeholder = "b${MainMod.VERSION}", secure = false, multiline = false)
        var buildText: String = ""

        @Exclude
        var offset = 0f

        override fun shouldDrawBackground() = drawBackground

        override fun draw(
            matrices: UMatrixStack?,
            x: Float,
            y: Float,
            scale: Float,
            example: Boolean
        ) {
            if (drawBackground) return
            GlStateManager.pushMatrix()
            GlStateManager.enableDepth()
            drawBackground = true
            try {
                drawAll(matrices, example)
            } finally {
                drawBackground = false
            }

            try {
                nanoVG(true) {
                    translate(x, y)
                    scale(scale, scale)
                    offset = getTextWidth("Camel", 16f, Font.stringToFont(MainMod.moduleManager.font.font, true)) + 1
                    drawText(
                        "Camel",
                        1, 9,
                        java.awt.Color.black.rgb,
                        16f,
                        Font.stringToFont(MainMod.moduleManager.font.font, true)
                    )
                    drawText(
                        if (buildText == "") "b${MainMod.VERSION}" else buildText,
                        offset + 1, 7,
                        java.awt.Color.black.rgb,
                        10f,
                        Font.stringToFont(MainMod.moduleManager.font.font, false)
                    )

                    drawText(
                        "Camel",
                        0, 8,
                        color.rgb,
                        16f,
                        Font.stringToFont(MainMod.moduleManager.font.font, true)
                    )
                    drawText(
                        if (buildText == "") "b${MainMod.VERSION}" else buildText,
                        offset, 6,
                        buildColor.rgb,
                        10f,
                        Font.stringToFont(MainMod.moduleManager.font.font, false)
                    )
                }
            }
            catch (_ : Exception) {}

            GlStateManager.popMatrix()
        }

        override fun getWidth(scale: Float, example: Boolean): Float {
            try {
                var width = 0f
                nanoVG(true) {
                    val text = if (buildText == "") "b${MainMod.VERSION}" else buildText
                    width = offset * scale + getTextWidth(text, 10f * scale, Font.stringToFont(MainMod.moduleManager.font.font, false))
                }
                return width
            }
            catch (_ : Exception) {
                return 1f
            }
        }

        override fun getHeight(scale: Float, example: Boolean): Float = 16 * scale
    }
}