package me.ghluka.camel.command.commands

import cc.polyfrost.oneconfig.utils.commands.annotations.Command
import cc.polyfrost.oneconfig.utils.commands.annotations.Main
import me.ghluka.camel.MainMod

@Command(value = MainMod.MODID, description = "Access the " + MainMod.NAME + " GUI.")
class Camel {
    @Main
    private fun handle() {
        MainMod.moduleManager.openGui()
    }
}