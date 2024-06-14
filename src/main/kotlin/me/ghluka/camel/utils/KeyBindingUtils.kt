package me.ghluka.camel.utils

import net.minecraft.client.settings.KeyBinding
import org.lwjgl.input.Keyboard

import java.util.Collections

class KeyBindingUtils {
    private val keyCodesPressed = ArrayList<Int>()

    fun getKeyBindState(keyCode: Int): Boolean {
        if (Keyboard.isKeyDown(keyCode))
            return true
        return keyCodesPressed.contains(keyCode)
    }

    fun setKeyBindState(keyCode: Int, pressed: Boolean) {
        KeyBinding.setKeyBindState(keyCode, pressed)
        if (pressed)
            Collections.addAll(keyCodesPressed, keyCode)
        else
            keyCodesPressed.remove(keyCode)
    }
}