package me.ghluka.camel.module.modules.hud

import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.HUD
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.hud.TextHud
import cc.polyfrost.oneconfig.utils.dsl.*
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.Module
import me.ghluka.camel.module.config.Font
import me.ghluka.camel.module.modules.dev.*
import me.ghluka.camel.module.modules.hypixel.skyblock.*
import me.ghluka.camel.module.modules.misc.*
import java.awt.Color

class ModulesList : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Module List"
        @Exclude
        const val CATEGORY = "HUD"
    }

    @HUD(name = MODULE, category = CATEGORY, subcategory = MODULE)
    var modulesList = ModulesList()

    init {
        initialize()
    }

    override fun save() {
        moduleEnabled = modulesList.isEnabled
        super.save()
    }

    class ModulesList : TextHud(
        true,
        6f,
        24f,
        .8f,
        true,
        false,
        8f,
        6f,
        5f,
        OneColor(0, 0, 0, 120),
        true,
        1f,
        OneColor(244, 225, 185)
    ) {
        @Exclude
        val hidden = listOf(
            CopyNBT.MODULE,
            Hilarity.MODULE,
            CustomMenu.MODULE,
            Watermark.MODULE,
            PSTWaypoint.MODULE
        )
        @Exclude
        val configMap: Map<String, String> = mapOf(
            "Reach" to "maxRange",
            "Hitboxes" to "expand",
        )

        @Dropdown(name = "Caps Type", options = ["lower case", "Title Case", "UPPER CASE"])
        var capsType: Int = 0
        @Switch(name = "Remove spaces", category = CATEGORY, subcategory = MODULE, size = 1)
        var removeSpaces = false

        @Dropdown(name = "Sort By", options = ["Order", "Alphabetical", "Length"])
        var sortBy: Int = 2
        @Switch(name = "Reverse sort", category = CATEGORY, subcategory = MODULE, size = 1)
        var reversed = true

        @cc.polyfrost.oneconfig.config.annotations.Color(name = "Config color", category = CATEGORY, subcategory = MODULE, size = 1)
        var cfgColor: OneColor = OneColor(209, 209, 209)
        @Switch(name = "Show config", category = CATEGORY, subcategory = MODULE, size = 1)
        var showConfig = true

        @Switch(name = "Show visual modules", category = CATEGORY, subcategory = MODULE, size = 1)
        var showRenders = false

        init {
            color = OneColor(244, 225, 185)
            textType = 1
        }

        override fun drawBackground(x: Float, y: Float, width: Float, height: Float, s: Float) {
            if (lines == null || lines.isEmpty()) return

            var textY = 0f
            var offset = 0
            if (border)
                offset = (borderSize * s).toInt()
            nanoVG(true) {
                translate(x + offset, y)
                scale(s, s)
                for (line in lines) {
                    val moduleName = line.split("&")[0].removeSuffix(" ")
                    val line = line.replace("&", "")

                    var textWidth = 0f
                    if (showConfig)
                        textWidth = getTextWidth("$line ", 12f, Font.stringToFont(MainMod.moduleManager.font.font, false))
                    else
                        textWidth = getTextWidth("$moduleName ", 12f, Font.stringToFont(MainMod.moduleManager.font.font, false))

                    drawRect(0, textY, textWidth, 12f, bgColor.rgb)
                    if (border)
                        drawRect(-offset, textY, borderSize, 12f, borderColor.rgb);
                    textY += 12f
                }
            }
        }

        override fun drawLine(line: String?, x: Float, y: Float, scale: Float) {
            var offset = 0
            if (border)
                offset = (borderSize * scale).toInt()
            nanoVG(true) {
                translate(x + offset - 2f, y)
                scale(scale, scale)
                var line = line?: ""
                val moduleName = line.split("&")[0]

                drawText(
                    moduleName,
                    1, 1,
                    Color.black.rgb,
                    12f,
                    Font.stringToFont(MainMod.moduleManager.font.font, false)
                )
                drawText(
                    moduleName,
                    0, 0,
                    color.rgb,
                    12f,
                    Font.stringToFont(MainMod.moduleManager.font.font, false)
                )

                if (showConfig && line.contains("&")) {
                    val configText = line.split("&")[1]
                    val configOffset = getTextWidth(moduleName, 12f, Font.stringToFont(MainMod.moduleManager.font.font, false))
                    drawText(
                        configText,
                        1 + configOffset, 1,
                        Color.black.rgb,
                        12f,
                        Font.stringToFont(MainMod.moduleManager.font.font, false)
                    )
                    drawText(
                        configText,
                        configOffset, 0,
                        cfgColor.rgb,
                        12f,
                        Font.stringToFont(MainMod.moduleManager.font.font, false)
                    )
                }
            }
        }
        override fun getHeight(scale: Float, example: Boolean): Float {
            return if (lines == null) 0f else (lines.size * 12 - 4) * scale
        }

        override fun getLineWidth(line: String?, scale: Float): Float {
            try {
                var width = 0f
                nanoVG(true) {
                    scale(scale, scale)
                    width = getTextWidth(line?: "", 12f, Font.stringToFont(MainMod.moduleManager.font.font, false))
                }
                return width
            }
            catch (_ : Exception) {
                return 1f
            }
        }

        override fun getLines(lines: MutableList<String>?, example: Boolean) {
            if (lines == null || mc.thePlayer == null) {
                return
            }

            val modules = MainMod.moduleManager.modules

            if (sortBy == 0)
                modules.sortBy { MainMod.moduleManager.moduleNames.indexOf(it.moduleName) }
            else if (sortBy == 1)
                modules.sortBy { it.moduleName.lowercase() }
            else if (sortBy == 2)
                modules.sortBy { getLineWidth("${getModuleDisplayName(it)} ".replace(" &", ""), scale) }

            if (reversed) {
                modules.reverse()
            }

            MainMod.moduleManager.modules.forEach { mod ->
                if (mod.moduleEnabled && !hidden.contains(mod.moduleName)) {
                    if (showRenders || !(
                                mod.javaClass.`package`.name.endsWith("render") ||
                                        mod.javaClass.`package`.name.endsWith("hud")
                                ))
                        lines.add(getModuleDisplayName(mod))
                }
            }

            if (lines.isEmpty() && example) {
                lines.add(getModuleDisplayName(MainMod.moduleManager.getModuleByName("Module List")!!)) // just show the module itself
            }
        }

        fun getModuleDisplayName(mod: Module): String {
            var name = mod.moduleName

            var configText: Any? = null
            if (showConfig) {
                val mlFunc = mod::class.members.firstOrNull { it.name == "mlString" && it.parameters.size == 1 }
                if (mlFunc != null) {
                    configText = try {
                        mlFunc.call(mod) as? String
                    } catch (_: Exception) { null }
                }

                // Fallback to configMap property
                if (configText == null) {
                    val propName = configMap[mod.moduleName]
                    if (propName != null) {
                        configText = try {
                            val property = mod::class.members.firstOrNull { it.name == propName }
                            property?.call(mod)
                        } catch (_: Exception) {
                            null
                        }
                        if (configText != null) {
                            if (configText is Float)
                                configText = String.format("%.1f", configText)
                            configText = configText.toString()
                        }
                    }
                }
            }

            if (removeSpaces)
                name = name.replace(" ", "").replace("-", "")
            if (configText is String) {
                name += " &$configText"
            }

            name = when (capsType) {
                0 -> name.lowercase()
                2 -> name.uppercase()
                else -> name
            }

            return name
        }
    }
}