package me.ghluka.camel.module.modules.hud

import cc.polyfrost.oneconfig.config.annotations.HUD
import cc.polyfrost.oneconfig.hud.TextHud
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.Module

class ModulesList : me.ghluka.camel.module.Module("Modules List") {
    @HUD(
        name = "Modules List",
        category = "HUD",
        subcategory = "Modules List"
    )
    var modulesList = ModulesList()

    init {
        initialize()
    }

    class ModulesList : TextHud(false) {
        override fun getLines(lines: MutableList<String>?, example: Boolean) {
            if (lines == null || mc.thePlayer == null) {
                return
            }

            MainMod.moduleManager.modules.forEach { mod: Module ->
                if (mod.moduleEnabled)
                    lines.add(mod.moduleName)
            }
        }
    }
}