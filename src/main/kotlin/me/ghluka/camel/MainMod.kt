package me.ghluka.camel

import net.fabricmc.api.ClientModInitializer

class MainMod : ClientModInitializer
{
    override fun onInitializeClient() {
        println(CamelConstants.NAME)
    }
}
