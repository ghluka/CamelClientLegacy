package me.ghluka.camel.block.blocks

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.util.EnumWorldBlockLayer

class GhostBlock : Block(Material.barrier) {
    init {
        setUnlocalizedName("ghost_block")
        setBlockUnbreakable()
        disableStats()
    }

    override fun isOpaqueCube(): Boolean = false
    override fun isFullCube(): Boolean = false
    override fun getRenderType(): Int = -1
    override fun getBlockLayer(): EnumWorldBlockLayer = EnumWorldBlockLayer.TRANSLUCENT
}