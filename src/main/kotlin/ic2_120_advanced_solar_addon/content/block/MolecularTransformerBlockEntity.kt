package ic2_120_advanced_solar_addon.content.block

import ic2_120_advanced_solar_addon.content.sync.MolecularTransformerSync
import ic2_120_advanced_solar_addon.content.screen.MolecularTransformerScreenHandler
import ic2_120_advanced_solar_addon.content.recipe.MTRecipes
import ic2_120.content.block.ITieredMachine
import ic2_120.content.block.machines.MachineBlockEntity
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventories
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.PacketByteBuf
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerContext
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.state.property.Properties
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import stardust.fabric.registry.annotation.ModBlockEntity
import stardust.fabric.registry.annotation.RegisterEnergy
import stardust.fabric.registry.sync.SyncedData
import stardust.fabric.registry.type
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory

@ModBlockEntity(block = MolecularTransformerBlock::class)
class MolecularTransformerBlockEntity(
    pos: BlockPos,
    state: BlockState
) : MachineBlockEntity(MolecularTransformerBlockEntity::class.type(), pos, state),
    Inventory, ITieredMachine, ExtendedScreenHandlerFactory {

    companion object {
        const val TIER = 10
        // const val REQUIRED_ENERGY_PER_TICK = 4096*4096
    }

    override val tier: Int = TIER
    override val activeProperty = MolecularTransformerBlock.ACTIVE

    @Suppress("unused")
    val syncedData = SyncedData(this)

    @RegisterEnergy
    val sync = MolecularTransformerSync(
        schema = syncedData,
        getFacing = { world?.getBlockState(pos)?.get(Properties.HORIZONTAL_FACING) ?: Direction.NORTH },
        currentTickProvider = { world?.time }
    )

    val inventory: DefaultedList<ItemStack> = DefaultedList.ofSize(MolecularTransformerBlock.INVENTORY_SIZE, ItemStack.EMPTY)

    private var energyUsed: Long = 0
    private var currentRecipe: MTRecipes.MTRecipe? = null

    override fun getInventory(): Inventory = this

    // Inventory
    override fun size(): Int = inventory.size
    override fun getStack(slot: Int): ItemStack = inventory.getOrElse(slot) { ItemStack.EMPTY }
    override fun setStack(slot: Int, stack: ItemStack) {
        inventory[slot] = stack
        markDirty()
    }
    override fun removeStack(slot: Int, amount: Int): ItemStack = Inventories.splitStack(inventory, slot, amount)
    override fun removeStack(slot: Int): ItemStack = Inventories.removeStack(inventory, slot)
    override fun clear() = inventory.clear()
    override fun isEmpty(): Boolean = inventory.all { it.isEmpty }
    override fun canPlayerUse(player: PlayerEntity): Boolean = Inventory.canPlayerUse(this, player)

    fun tick(world: World, pos: BlockPos, state: BlockState) {
        if (world.isClient) return

        val inputStack = inventory[MolecularTransformerBlock.INPUT_SLOT]
        val outputStack = inventory[MolecularTransformerBlock.OUTPUT_SLOT]

        val isActive = if (currentRecipe == null && !inputStack.isEmpty) {
            val recipe = MTRecipes.findRecipe(inputStack)
            if (recipe != null && canOutput(recipe.output, outputStack)) {
                currentRecipe = recipe
                energyUsed = 0
            }
            false
        } else false

        val recipe = currentRecipe
        if (recipe != null) {
            val energyNeeded = recipe.energy - energyUsed
            val energyToUse = minOf(sync.amount, energyNeeded)
            sync.consumeEnergy(energyToUse)
            energyUsed += energyToUse

            if (energyUsed >= recipe.energy) {
                if (inventory[MolecularTransformerBlock.OUTPUT_SLOT].isEmpty) {
                    inventory[MolecularTransformerBlock.OUTPUT_SLOT] = recipe.output.copy()
                } else {
                    inventory[MolecularTransformerBlock.OUTPUT_SLOT].increment(recipe.output.count)
                }

                inputStack.decrement(1)
                if (inputStack.isEmpty) {
                    inventory[MolecularTransformerBlock.INPUT_SLOT] = ItemStack.EMPTY
                }

                currentRecipe = null
                energyUsed = 0
            }

            markDirty()
        }

        // Update sync data
        sync.energy = sync.amount.toInt().coerceIn(0, Int.MAX_VALUE)
        sync.progress = getProgress().toInt()
        sync.requiredEnergy = getRequiredEnergy().toInt()

        sync.syncCurrentTickFlow()
        setActiveState(world, pos, state, currentRecipe != null)
    }

    private fun canOutput(output: ItemStack, currentOutput: ItemStack): Boolean {
        if (currentOutput.isEmpty) return true
        if (!ItemStack.canCombine(currentOutput, output)) return false
        return currentOutput.count + output.count <= currentOutput.maxCount
    }

    private fun getProgress(): Float {
        val recipe = currentRecipe ?: return 0f
        return energyUsed.toFloat() / recipe.energy.toFloat()
    }

    private fun getRequiredEnergy(): Long = currentRecipe?.energy ?: 0

    // ExtendedScreenHandlerFactory
    override fun getDisplayName(): Text = Text.translatable("block.ic2_120_advanced_solar_addon.molecular_transformer")

    override fun createMenu(syncId: Int, playerInventory: PlayerInventory, player: PlayerEntity?): ScreenHandler =
        MolecularTransformerScreenHandler(
            syncId, playerInventory, this,
            ScreenHandlerContext.create(world!!, pos),
            syncedData
        )

    override fun writeScreenOpeningData(player: ServerPlayerEntity, buf: PacketByteBuf) {
        buf.writeBlockPos(pos)
        buf.writeVarInt(syncedData.size())
    }

    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        Inventories.readNbt(nbt, inventory)
        energyUsed = nbt.getLong("energyUsed")
        sync.restoreEnergy(nbt.getLong(MolecularTransformerSync.NBT_ENERGY).coerceIn(0L, sync.capacity))
        syncedData.readNbt(nbt)
    }

    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        Inventories.writeNbt(nbt, inventory)
        nbt.putLong("energyUsed", energyUsed)
        nbt.putLong(MolecularTransformerSync.NBT_ENERGY, sync.amount)
        syncedData.writeNbt(nbt)
    }
}
