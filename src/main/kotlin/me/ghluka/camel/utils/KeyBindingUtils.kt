package me.ghluka.camel.utils

import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard
import java.util.*


class KeyBindingUtils {
    private val keyCodesPressed = ArrayList<Int>()

    fun getKeyBindState(keyCode: Int): Boolean {
        if (Keyboard.isKeyDown(keyCode))
            return true
        return keyCodesPressed.contains(keyCode)
    }

    fun setKeyBindState(keyCode: Int, pressed: Boolean) {
        KeyBinding.setKeyBindState(keyCode, pressed)
        if (pressed && !keyCodesPressed.contains(keyCode))
            Collections.addAll(keyCodesPressed, keyCode)
        else
            keyCodesPressed.remove(keyCode)
    }

    init {
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun onGuiOpen(event: GuiOpenEvent) {
        if (event.gui != null) {
            keyCodesPressed.clear()
        }
    }
}