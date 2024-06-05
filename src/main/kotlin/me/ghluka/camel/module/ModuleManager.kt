package me.ghluka.camel.module

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.SubConfig
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import me.ghluka.camel.MainMod
import me.ghluka.camel.module.modules.render.TargetHud

class ModuleManager : Config(Mod(MainMod.NAME, ModType.UTIL_QOL), MainMod.MODID + ".json") {
    @SubConfig public var targetHud = TargetHud()

    init {
        initialize()
    }
}