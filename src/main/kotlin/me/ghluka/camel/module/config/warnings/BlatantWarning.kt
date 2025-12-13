package me.ghluka.camel.module.config.warnings

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.data.InfoType
import me.ghluka.camel.MainMod

class BlatantWarning : Config(MainMod.MOD, MainMod.MODID + "/" + "BlatantWarning.json", true) {
    @Exclude
    @Info(text = "The modules below are highly detectable, even with a good config. Be careful!", subcategory = "", category = "Combat", type = InfoType.ERROR, size = 2)
    var info = false

    override fun initialize() {}

    init {
        mod.config = this
        generateOptionList(this, mod.defaultPage, mod, false)
    }
}