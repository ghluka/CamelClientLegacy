package me.ghluka.camel.module

import cc.polyfrost.oneconfig.config.Config
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.modules.render.Chams
import me.ghluka.camel.module.modules.render.TargetHud

class ModuleManager : Config(MainMod.MOD, MainMod.MODID + ".json") {
    var chams = Chams()
    var targetHud = TargetHud()

    init {
        initialize()
    }
}