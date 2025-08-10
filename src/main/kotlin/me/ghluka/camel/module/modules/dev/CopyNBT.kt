package me.ghluka.camel.module.modules.dev

import cc.polyfrost.oneconfig.config.annotations.*
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.*
import cc.polyfrost.oneconfig.utils.Notifications
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Slot
import net.minecraft.nbt.*
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.BlockPos
import net.minecraft.util.MovingObjectPosition
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent
import net.minecraftforge.common.util.Constants
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import org.lwjgl.input.Keyboard
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.StringSelection
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.*

/*
Copied from
https://github.com/RoseGoldIsntGay/GumTuneClient/blob/main/src/main/java/rosegold/gumtuneclient/modules/dev/CopyNBTData.java
https://github.com/RoseGoldIsntGay/GumTuneClient/blob/main/src/main/java/rosegold/gumtuneclient/utils/DevUtils.java
 */
class CopyNBT : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Copy NBT"
        @Exclude
        const val CATEGORY = "Dev"
    }

    @Exclude
    @Info(text = "Copies the NBT data of an item or entity when the keybind is pressed and module is enabled.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable copying", category = CATEGORY, subcategory = MODULE, size = 2)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "Copy keybind", category = CATEGORY, subcategory = MODULE, size = 2)
    var copyNBTDataKeyBind: OneKeyBind = OneKeyBind()

    init {
        initialize()
        registerKeyBind(copyNBTDataKeyBind) {
            if (!moduleEnabled) return@registerKeyBind
            if (mc.thePlayer == null || mc.theWorld == null) return@registerKeyBind

            if (mc.objectMouseOver != null) {
                if (mc.objectMouseOver.typeOfHit === MovingObjectPosition.MovingObjectType.BLOCK) {
                    val blockPos: BlockPos? = mc.objectMouseOver.getBlockPos()
                    val iBlockState: IBlockState = mc.theWorld.getBlockState(blockPos)
                    val block: Block = iBlockState.getBlock()
                    if (block.hasTileEntity(iBlockState)) {
                        val tileEntity: TileEntity = checkNotNull(mc.theWorld.getTileEntity(blockPos))
                        copyStringToClipboard(
                            getTileEntityData(tileEntity),
                            "Tile entity data was copied to clipboard!"
                        )
                    } else {
                        Notifications.INSTANCE.send("Camel", "Block has no tile entity")
                    }
                } else if (mc.objectMouseOver.typeOfHit === MovingObjectPosition.MovingObjectType.ENTITY) {
                    copyStringToClipboard(
                        getEntityData(mc.objectMouseOver.entityHit),
                        "Entity data of " + mc.objectMouseOver.entityHit + " was copied to clipboard!"
                    )
                } else if (mc.objectMouseOver.typeOfHit === MovingObjectPosition.MovingObjectType.MISS) {
                    copyStringToClipboard(
                        getEntityData(mc.thePlayer),
                        "Entity data of the player was copied to clipboard!"
                    )
                }
            }
        }
    }

    @SubscribeEvent
    fun onKeyInput(event: KeyboardInputEvent.Pre) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return

        val eventKey: Int = Keyboard.getEventKey()
        if (!Keyboard.isKeyDown(eventKey)) return
        val keyBinds: ArrayList<Int?> = copyNBTDataKeyBind.getKeyBinds()
        if (keyBinds.size > 0 && keyBinds.get(0) == eventKey) {
            val currentScreen = event.gui

            if (GuiContainer::class.java.isAssignableFrom(currentScreen.javaClass)) {
                val currentSlot: Slot? = (currentScreen as GuiContainer).getSlotUnderMouse()

                if (currentSlot != null && currentSlot.getHasStack()) {
                    copyNBTTagToClipboard(
                        currentSlot.getStack().serializeNBT(),
                        "Item data was copied to clipboard!"
                    )
                }
            }
        }
    }

    fun getEntityData(entity: Entity): String {
        val entityData = NBTTagCompound()
        entity.writeToNBT(entityData)
        val stringBuilder = StringBuilder()
        if (stringBuilder.isNotEmpty()) {
            stringBuilder.append(System.lineSeparator()).append(System.lineSeparator())
        }

        stringBuilder.append("Class: ").append(entity.javaClass.getSimpleName()).append(System.lineSeparator())
        if (entity.hasCustomName() || EntityPlayer::class.java.isAssignableFrom(entity.javaClass)) {
            stringBuilder.append("Name: ").append(entity.getName()).append(System.lineSeparator())
        }

        stringBuilder.append("NBT Data:").append(System.lineSeparator())
        stringBuilder.append(prettyPrintNBT(entityData))

        return stringBuilder.toString()
    }

    fun getTileEntityData(tileEntity: TileEntity): String {
        return prettyPrintNBT(tileEntity.tileData)
    }

    fun copyStringToClipboard(string: String?, successMessage: String?) {
        writeToClipboard(string, successMessage)
    }

    private fun writeToClipboard(text: String?, successMessage: String?) {
        val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val output = StringSelection(text)

        try {
            clipboard.setContents(output, output)
            Notifications.INSTANCE.send("Camel", successMessage)
        } catch (exception: IllegalStateException) {
            Notifications.INSTANCE.send("Camel", "Clipboard not available!")
        }
    }

    fun copyNBTTagToClipboard(nbtTag: NBTBase?, message: String?) {
        if (nbtTag == null) {
            Notifications.INSTANCE.send("Camel", "This item has no NBT data!")
            return
        }
        writeToClipboard(prettyPrintNBT(nbtTag), message)
    }

    fun prettyPrintNBT(nbt: NBTBase): String {
        val INDENT = "    "

        val tagID = nbt.id.toInt()
        var stringBuilder = java.lang.StringBuilder()

        // Determine which type of tag it is.
        if (tagID == Constants.NBT.TAG_END) {
            stringBuilder.append('}')
        } else if (tagID == Constants.NBT.TAG_BYTE_ARRAY || tagID == Constants.NBT.TAG_INT_ARRAY) {
            stringBuilder.append('[')
            if (tagID == Constants.NBT.TAG_BYTE_ARRAY) {
                val nbtByteArray = nbt as NBTTagByteArray
                val bytes = nbtByteArray.byteArray

                for (i in bytes.indices) {
                    stringBuilder.append(bytes[i].toInt())

                    // Don't add a comma after the last element.
                    if (i < (bytes.size - 1)) {
                        stringBuilder.append(", ")
                    }
                }
            } else {
                val nbtIntArray = nbt as NBTTagIntArray
                val ints = nbtIntArray.intArray

                for (i in ints.indices) {
                    stringBuilder.append(ints[i])

                    // Don't add a comma after the last element.
                    if (i < (ints.size - 1)) {
                        stringBuilder.append(", ")
                    }
                }
            }
            stringBuilder.append(']')
        } else if (tagID == Constants.NBT.TAG_LIST) {
            val nbtTagList = nbt as NBTTagList

            stringBuilder.append('[')
            for (i in 0..<nbtTagList.tagCount()) {
                val currentListElement = nbtTagList.get(i)

                stringBuilder.append(prettyPrintNBT(currentListElement))

                // Don't add a comma after the last element.
                if (i < (nbtTagList.tagCount() - 1)) {
                    stringBuilder.append(", ")
                }
            }
            stringBuilder.append(']')
        } else if (tagID == Constants.NBT.TAG_COMPOUND) {
            val nbtTagCompound = nbt as NBTTagCompound

            stringBuilder.append('{')
            if (!nbtTagCompound.hasNoTags()) {
                val iterator = nbtTagCompound.keySet.iterator()

                stringBuilder.append(System.lineSeparator())

                while (iterator.hasNext()) {
                    val key = iterator.next()
                    val currentCompoundTagElement = nbtTagCompound.getTag(key)

                    stringBuilder.append(key).append(": ").append(
                        prettyPrintNBT(currentCompoundTagElement)
                    )

                    if (key.contains("backpack_data") && currentCompoundTagElement is NBTTagByteArray) {
                        try {
                            val backpackData = CompressedStreamTools.readCompressed(
                                ByteArrayInputStream(
                                    currentCompoundTagElement.byteArray
                                )
                            )

                            stringBuilder.append(",").append(System.lineSeparator())
                            stringBuilder.append(key).append("(decoded): ").append(
                                prettyPrintNBT(backpackData)
                            )
                        } catch (e: IOException) {
                            println("Couldn't decompress backpack data into NBT, skipping!")
                            e.printStackTrace()
                        }
                    }

                    // Don't add a comma after the last element.
                    if (iterator.hasNext()) {
                        stringBuilder.append(",").append(System.lineSeparator())
                    }
                }

                // Indent all lines
                val indentedString =
                    stringBuilder.toString().replace(System.lineSeparator().toRegex(), System.lineSeparator() + INDENT)
                stringBuilder = java.lang.StringBuilder(indentedString)
            }

            stringBuilder.append(System.lineSeparator()).append('}')
        } else {
            stringBuilder.append(nbt)
        }

        return stringBuilder.toString()
    }
}