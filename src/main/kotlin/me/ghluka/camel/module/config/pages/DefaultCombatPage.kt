package me.ghluka.camel.module.config.pages

import cc.polyfrost.oneconfig.config.annotations.Switch

class DefaultCombatPage {
    @Switch(name = "Only with weapon", size = 1)
    var onlyWithWeapon: Boolean = false
    @Switch(name = "Only while targeting", size = 1)
    var onlyWhileTargeting: Boolean = false
    @Switch(name = "Only on ground", size = 1)
    var onlyOnGround: Boolean = false
    @Switch(name = "Only while moving", size = 1)
    var onlyWhileMoving: Boolean = false
    @Switch(name = "Only while sprinting", size = 1)
    var onlyWhileSprinting: Boolean = false
    @Switch(name = "Only with speed", size = 1)
    var onlyWithSpeed: Boolean = false
    @Switch(name = "Disable while holding S", size = 1)
    var disableWhileS: Boolean = false
}