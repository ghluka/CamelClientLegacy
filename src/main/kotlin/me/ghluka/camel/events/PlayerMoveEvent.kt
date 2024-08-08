package me.ghluka.camel.events

import net.minecraftforge.fml.common.eventhandler.Cancelable
import net.minecraftforge.fml.common.eventhandler.Event


@Cancelable
class PlayerMoveEvent {
    @Cancelable
    class Pre : Event()
    class Post : Event()
}