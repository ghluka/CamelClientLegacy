package me.ghluka.camel.config

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.SubConfig
import cc.polyfrost.oneconfig.config.data.Mod
import cc.polyfrost.oneconfig.config.data.ModType
import me.ghluka.camel.MainMod
import me.ghluka.camel.hud.huds.TargetHud

/**
 * The main Config entrypoint that extends the Config type and inits the config options.
 * See [this link](https://docs.polyfrost.cc/oneconfig/config/adding-options) for more config Options
 */
class ModConfig : Config(Mod(MainMod.NAME, ModType.UTIL_QOL), MainMod.MODID + ".json") {
    @SubConfig
    var targetHud = TargetHud()

    init {
        initialize()
    }
}