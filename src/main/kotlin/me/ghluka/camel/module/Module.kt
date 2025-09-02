package me.ghluka.camel.module

import cc.polyfrost.oneconfig.config.Config
import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.core.ConfigUtils
import cc.polyfrost.oneconfig.events.EventManager
import me.ghluka.camel.MainMod
import net.minecraftforge.common.MinecraftForge
import java.io.File


open class Module(name:String) : Config(MainMod.MOD, MainMod.MODID + "/" + name.replace(" ", "") + ".json", true) {
    @Exclude
    var moduleName = name
    @Exclude
    open var moduleEnabled: Boolean = false

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
        MinecraftForge.EVENT_BUS.register(this)
        EventManager.INSTANCE.register(this)
    }
}