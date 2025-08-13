package me.ghluka.camel.module.modules.hypixel.arcade

import cc.polyfrost.oneconfig.config.annotations.Exclude
import cc.polyfrost.oneconfig.config.annotations.Info
import cc.polyfrost.oneconfig.config.annotations.KeyBind
import cc.polyfrost.oneconfig.config.annotations.Slider
import cc.polyfrost.oneconfig.config.annotations.Switch
import cc.polyfrost.oneconfig.config.core.OneKeyBind
import cc.polyfrost.oneconfig.config.data.InfoType
import cc.polyfrost.oneconfig.utils.Notifications
import cc.polyfrost.oneconfig.utils.dsl.mc
import me.ghluka.camel.module.Module
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.inventory.ContainerFurnace
import net.minecraft.inventory.ContainerWorkbench
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.item.crafting.IRecipe
import net.minecraft.item.crafting.ShapedRecipes
import net.minecraft.item.crafting.ShapelessRecipes
import net.minecraftforge.oredict.ShapedOreRecipe
import net.minecraftforge.oredict.ShapelessOreRecipe


class WorkshopAIO : Module(MODULE) {
    @Exclude
    companion object {
        @Exclude
        const val MODULE = "Workshop AIO"
        @Exclude
        const val CATEGORY = "Hypixel Arcade"
    }

    @Exclude
    val items: Map<String, Item> = mapOf(
        "Iron Pickaxe" to Items.iron_pickaxe,
    )

    @Exclude
    private val recipeCache = mutableMapOf<Item, Map<Int, Item>?>(
        Items.stick to mapOf(
            1 to ItemBlock.getItemFromBlock(Blocks.planks),
            4 to ItemBlock.getItemFromBlock(Blocks.planks),
        ),
    )

    @Exclude
    @Info(text = "Automatically crafts and smelts for you in Workshop for the game Party Games (/play party_games). Not localized, requires your Hypixel set to English.", subcategory = MODULE, category = CATEGORY, type = InfoType.INFO, size = 2)
    var info: Boolean = false

    @Switch(name = "Enable Workshop Autocraft", category = CATEGORY, subcategory = MODULE, size = 1)
    override var moduleEnabled: Boolean = false
    @KeyBind(name = "", category = CATEGORY, subcategory = MODULE, size = 1)
    var moduleKeyBind: OneKeyBind = OneKeyBind()

    @Slider(name = "Speed (ms)", category = CATEGORY, subcategory = MODULE, min = 0F, max = 75F)
    var debounce: Int = 50

    init {
        initialize()
        registerKeyBind(moduleKeyBind) {
            moduleEnabled = !moduleEnabled
        }
    }

    @Exclude
    var timer = 0L
    @Exclude
    var taking = false

    @Exclude
    private val clickQueue: MutableList<() -> Unit> = mutableListOf()

    @SubscribeEvent
    fun onRender(e: RenderWorldLastEvent?) {
        if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        if ((timer + debounce.toLong()) > System.currentTimeMillis()) {
            //println("NOT READY; ${timer + debounce} > ${System.currentTimeMillis()}")
            return
        }
        //println("READY.")

        if (clickQueue.isNotEmpty()) {
            val action = clickQueue.removeAt(0)
            action.invoke()
            timer = System.currentTimeMillis()
            return
        }
        handleFurnaceOpen()
        handleWorkbenchOpen()
    }

    @SubscribeEvent
    fun chat(event: ClientChatReceivedEvent) {
        //if (!moduleEnabled) return
        if (mc.thePlayer == null || mc.theWorld == null) return
        val message: String = event.message.unformattedText.replace("§[0-9a-fk-or]".toRegex(), "")

        if (!message.startsWith("Foreman ")) return
        if ("so I need you to craft me a" !in message) return

        val itemName = message.split("so I need you to craft me a")[1]
            .removePrefix("n")
            .removePrefix(" ")
            .removeSuffix("!")
        if (!items.containsKey(itemName)) {
            if (moduleEnabled) {
                Notifications.INSTANCE.send(
                    "Camel",
                    "Recipe for $itemName not found! Please contact a developer to add this."
                )
                Notifications.INSTANCE.send("Camel", "$MODULE disabled.")
                moduleEnabled = false
            }
            return
        }
        currRecipe = items[itemName]
    }

    private fun handleFurnaceOpen() {
        if (taking) return
        if (mc.thePlayer.openContainer !is ContainerFurnace) return

        val furnace = mc.thePlayer.openContainer as ContainerFurnace
        val inv = mc.thePlayer.inventory
        val controller = mc.playerController

        if (furnace.getSlot(2).stack != null) {
            taking = true
            clickQueue.add {
                taking = false
                controller.windowClick(furnace.windowId, 2, 0, 1, mc.thePlayer)
            }
            timer = System.currentTimeMillis()
        }
        else {
            taking = false
            for (i in 0..<inv.sizeInventory) {
                val stack = inv.getStackInSlot(i)
                if (stack != null && isOre(stack.item)) {
                    clickQueue.add {
                        controller.windowClick(furnace.windowId, iToSlot(i, 3), 0, 1, mc.thePlayer)
                    }
                    break
                }
            }
        }
    }

    private fun getRecipeSlotsFor(
        item: Item?,
        visited: MutableSet<Item> = mutableSetOf()
    ): Map<Int, Item>? {
        if (item == null) return null

        if (!visited.add(item)) {
            return null
        }

        recipeCache[item]?.let { return it }

        val slotMap = mutableMapOf<Int, Item>()

        for (recipe in CraftingManager.getInstance().recipeList as List<IRecipe>) {
            val output = recipe.recipeOutput
            if (output != null && output.item == item) {
                when (recipe) {
                    is ShapedRecipes -> {
                        val width = recipe.recipeWidth
                        val height = recipe.recipeHeight
                        val items = recipe.recipeItems
                        for (y in 0 until height) {
                            for (x in 0 until width) {
                                val slot = y * 3 + x + 1
                                val ingredient = items[y * width + x]
                                if (ingredient != null) {
                                    slotMap[slot] = ingredient.item
                                }
                            }
                        }
                    }
                    is ShapelessRecipes -> {
                        for (i in recipe.recipeItems.indices) {
                            val ingredient = recipe.recipeItems[i]
                            if (ingredient != null) {
                                slotMap[i + 1] = ingredient.item
                            }
                        }
                    }
                    is ShapedOreRecipe -> {
                        val items = recipe.input
                        for (y in 0 until 3) {
                            for (x in 0 until 3) {
                                val slot = y * 3 + x + 1
                                try {
                                    val ingredient = items[y * 3 + x]
                                    val stack = when (ingredient) {
                                        is ItemStack -> ingredient
                                        is List<*> -> ingredient.firstOrNull() as? ItemStack
                                        else -> null
                                    }
                                    if (stack != null) {
                                        slotMap[slot] = stack.item
                                    }
                                } catch (_: ArrayIndexOutOfBoundsException) { }
                            }
                        }
                    }
                    is ShapelessOreRecipe -> {
                        val items = recipe.input
                        for (i in items.indices) {
                            val ingredient = items[i]
                            val stack = when (ingredient) {
                                is ItemStack -> ingredient
                                is List<*> -> ingredient.firstOrNull() as? ItemStack
                                else -> null
                            }
                            if (stack != null) {
                                slotMap[i + 1] = stack.item
                            }
                        }
                    }
                }
                break
            }
        }

        recipeCache[item] = slotMap.ifEmpty { null }
        return recipeCache[item]
    }


    @Exclude
    var currRecipe: Item? = null

    private fun handleWorkbenchOpen() {
        if (mc.thePlayer.openContainer !is ContainerWorkbench) return
        if (currRecipe == null) return
        if (clickQueue.isNotEmpty()) return

        if (mc.thePlayer.inventory.hasItem(currRecipe)) {
            currRecipe = null
            return
        }

        val workbench = mc.thePlayer.openContainer as ContainerWorkbench

        val visited = mutableSetOf<Item>()
        craftWithDependenciesQueued(currRecipe!!, visited, workbench)
    }

    private fun craftWithDependenciesQueued(
        target: Item,
        visited: MutableSet<Item>,
        workbench: ContainerWorkbench
    ) {
        if (!visited.add(target)) return

        val recipe = getRecipeSlotsFor(target) ?: return
        val inv = mc.thePlayer.inventory
        println("crafting ${target.unlocalizedName} with recipe of $recipe")

        for (ingredient in recipe.values) {
            if (!inv.hasItem(ingredient)) {
                craftWithDependenciesQueued(ingredient, visited, workbench)
            }
        }

        for (ingredient in recipe.values) {
            if (!inv.hasItem(ingredient)) {
                return
            }
        }
        queueCraftOnce(target, workbench)
    }

    private fun queueCraftOnce(target: Item, workbench: ContainerWorkbench) {
        val recipe = getRecipeSlotsFor(target) ?: return

        for ((gridSlot, item) in recipe) {
            val fromSlot = findSlotInInventory(item) ?: return
            clickQueue.add {
                if (mc.thePlayer.openContainer is ContainerWorkbench)
                    mc.playerController.windowClick(workbench.windowId, iToSlot(fromSlot, 10), 0, 0, mc.thePlayer)
            }
            clickQueue.add {
                if (mc.thePlayer.openContainer is ContainerWorkbench)
                    mc.playerController.windowClick(workbench.windowId, gridSlot, 1, 0, mc.thePlayer)
            }
            clickQueue.add {
                if (mc.thePlayer.openContainer is ContainerWorkbench)
                    mc.playerController.windowClick(workbench.windowId, iToSlot(fromSlot, 10), 0, 0, mc.thePlayer)
            }
        }

        clickQueue.add {
            if (mc.thePlayer.openContainer is ContainerWorkbench)
                mc.playerController.windowClick(workbench.windowId, 0, 0, 1, mc.thePlayer)
        }
    }

    private fun findSlotInInventory(item: Item): Int? {
        val inv = mc.thePlayer.inventory
        for (i in 0 until inv.sizeInventory) {
            val stack = inv.getStackInSlot(i)
            if (stack != null && stack.item == item) return i
        }
        return null
    }

    fun isOre(item: Item?): Boolean {
        return item == Item.getItemFromBlock(Blocks.iron_ore) ||
                item == Item.getItemFromBlock(Blocks.gold_ore)
    }

    fun iToSlot(i: Int, offset: Int): Int {
        val diff = (offset - 9)
        return if (i >= 9 && i <= 35) {
            i + diff
        } else if (i >= 0 && i <= 8) {
            i + (35 + diff + 1)
        } else {
            throw IllegalArgumentException("Invalid player slot ID: $i")
        }
    }
}