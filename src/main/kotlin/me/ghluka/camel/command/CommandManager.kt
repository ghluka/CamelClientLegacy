package me.ghluka.camel.command

import cc.polyfrost.oneconfig.utils.commands.CommandManager
import me.ghluka.camel.command.commands.*

class CommandManager {
    init {
        CommandManager.INSTANCE.registerCommand(Camel())
    }
}