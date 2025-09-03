package me.ghluka.camel.module.modules.hud

import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.HUD
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.hud.TextHud
import cc.polyfrost.oneconfig.renderer.TextRenderer
import cc.polyfrost.oneconfig.utils.dsl.*
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.Module
import me.ghluka.camel.module.config.Font
import me.ghluka.camel.module.modules.misc.Hilarity
import me.ghluka.camel.module.modules.world.PSTWaypoint
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
        0f,
        24f,
        0.85f,
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
        @Exclude
        val hidden = listOf(
            Hilarity.MODULE,
            Watermark.MODULE,
            PSTWaypoint.MODULE
        )

        @Dropdown(name = "Caps Type", options = ["lower case", "Title Case", "UPPER CASE"])
        var capsType: Int = 0
        @Switch(name = "Remove spaces", category = CATEGORY, subcategory = MODULE, size = 1)
        var removeSpaces = false

        @Dropdown(name = "Sort By", options = ["Order", "Alphabetical", "Length"])
        var sortBy: Int = 2
        @Exclude
        var lastSortBy: Int = 0
        @Switch(name = "Reverse sort", category = CATEGORY, subcategory = MODULE, size = 1)
        var reversed = true
        @Exclude
        var lastReversed = false

        init {
            color = OneColor(25, 100, 255, 30F)
            textType = 1
        }

        override fun drawLine(line: String?, x: Float, y: Float, scale: Float) {
            nanoVG(true) {
                translate(x, y)
                scale(scale, scale)
                drawText(
                    line?: "",
                    1, 1,
                    Color.black.rgb,
                    12f,
                    Font.stringToFont(MainMod.moduleManager.font.font, false)
                )
                drawText(
                    line?: "",
                    0, 0,
                    color.rgb,
                    12f,
                    Font.stringToFont(MainMod.moduleManager.font.font, false)
                )
            }
        }
        override fun getHeight(scale: Float, example: Boolean): Float {
            return if (lines == null) 0f else (lines.size * 12 - 4) * scale
        }

        override fun getLineWidth(line: String?, scale: Float): Float {
            try {
                var width = 0f
                nanoVG(true) {
                    width = getTextWidth(line?: "", 12f * scale, Font.stringToFont(MainMod.moduleManager.font.font, false))
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
            if (lastSortBy != sortBy) {
                if (sortBy == 0)
                    modules.sortBy { MainMod.moduleManager.moduleNames.indexOf(it.moduleName) }
                else if (sortBy == 1)
                    modules.sortBy { it.moduleName.lowercase() }
                else if (sortBy == 2)
                    modules.sortBy {
                        var moduleName = it.moduleName
                        if (capsType == 0)
                            moduleName = moduleName.lowercase()
                        else if (capsType == 2)
                            moduleName = moduleName.uppercase()

                        if (removeSpaces)
                            moduleName = moduleName.replace(" ", "").replace("-", "")
                        getLineWidth(moduleName, scale)
                    }
            }
            if (lastReversed != reversed) {
                modules.reverse()
            }
            lastSortBy = sortBy
            lastReversed = reversed

            MainMod.moduleManager.modules.forEach { mod: Module ->
                if (mod.moduleEnabled && !hidden.contains(mod.moduleName)) {
                    var moduleName = mod.moduleName

                    if (capsType == 0)
                        moduleName = moduleName.lowercase()
                    else if (capsType == 2)
                        moduleName = moduleName.uppercase()

                    if (removeSpaces)
                        moduleName = moduleName.replace(" ", "").replace("-", "")

                    lines.add(moduleName)
                }
            }

            if (lines.isEmpty() && example) {
                var moduleName = MODULE
                if (capsType == 0)
                    moduleName = moduleName.lowercase()
                else if (capsType == 2)
                    moduleName = moduleName.uppercase()
                if (removeSpaces)
                    moduleName = moduleName.replace(" ", "").replace("-", "")
                lines.add(moduleName)
            }
        }
    }
}