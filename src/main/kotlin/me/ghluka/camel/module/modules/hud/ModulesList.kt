package me.ghluka.camel.module.modules.hud

import cc.polyfrost.oneconfig.config.annotations.Dropdown
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.HUD
import cc.polyfrost.oneconfig.config.core.OneColor
import cc.polyfrost.oneconfig.hud.TextHud
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.Module

class ModulesList : me.ghluka.camel.module.Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Modules List"
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
        override fun getLines(lines: MutableList<String>?, example: Boolean) {
            if (lines == null || mc.thePlayer == null) {
                return
            }

            MainMod.moduleManager.modules.forEach { mod: Module ->
                if (mod.moduleEnabled && mod.moduleName != "Hilarity")
                    lines.add(mod.moduleName.replace(" ", ""))
            }
        }
    }
}