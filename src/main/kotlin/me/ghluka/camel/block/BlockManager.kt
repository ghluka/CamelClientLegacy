package me.ghluka.camel.block

import me.ghluka.camel.block.blocks.*
import net.minecraftforge.fml.common.registry.GameRegistry

class BlockManager {
    var ghostBlock: GhostBlock = GhostBlock()

    init {
        GameRegistry.registerBlock(ghostBlock, "ghost_block")
    }
}