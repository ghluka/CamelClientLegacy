package me.ghluka.camel.module

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.core.ConfigUtils
import me.ghluka.camel.MainMod
import java.io.File

open class Module(name:String) : Config(MainMod.MOD, MainMod.MODID + "/" + name + ".json", true) {
    override fun initialize() {
        var migrate = false
        val profileFile: File = ConfigUtils.getProfileFile(configFile)
        if (profileFile.exists())
            load()
        if (!profileFile.exists()) {
            if (mod.migrator != null)
                migrate = true
            else
                save()
        }
        mod.config = this
        generateOptionList(this, mod.defaultPage, mod, migrate)
        if (migrate)
            save()
    }
}