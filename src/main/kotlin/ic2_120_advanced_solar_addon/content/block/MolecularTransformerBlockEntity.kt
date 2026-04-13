package ic2_120_advanced_solar_addon.content.block

import ic2_120_advanced_solar_addon.content.recipe.MTRecipes
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import team.reborn.energy.api.EnergyStorage
import team.reborn.energy.api.base.SimpleEnergyStorage

class MolecularTransformerBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(ModBlocks.MOLECULAR_TRANSFORMER_ENTITY, pos, state), EnergyStorage {
    
    val inventory: DefaultedList<ItemStack> = DefaultedList.ofSize(2, ItemStack.EMPTY)
    
    val energyStorage = object : SimpleEnergyStorage(Long.MAX_VALUE, Long.MAX_VALUE, 0) {
        override fun onFinalCommit() {
            markDirty()
        }
    }
    
    var energyUsed: Long = 0
    private var currentRecipe: MTRecipes.MTRecipe? = null
    
    override fun insert(maxAmount: Long, transaction: net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext?): Long {
        val inserted = energyStorage.insert(maxAmount, transaction)
        if (inserted > 0) markDirty()
        return inserted
    }
    
    override fun extract(maxAmount: Long, transaction: net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext?): Long = 0
    override fun getAmount(): Long = energyStorage.amount
    override fun getCapacity(): Long = energyStorage.capacity
    
    fun tick() {
        if (world?.isClient == true) return
        
        val inputStack = inventory[0]
        val outputStack = inventory[1]
        
        // 检查是否有配方
        if (currentRecipe == null && !inputStack.isEmpty) {
            val recipe = MTRecipes.findRecipe(inputStack)
            if (recipe != null && canOutput(recipe.output, outputStack)) {
                currentRecipe = recipe
                energyUsed = 0
            }
        }
        
        // 处理配方
        val recipe = currentRecipe
        if (recipe != null) {
            // 消耗能量
            val energyNeeded = recipe.energy - energyUsed
            val energyToUse = minOf(energyStorage.amount, energyNeeded)
            energyStorage.amount -= energyToUse
            energyUsed += energyToUse
            
            // 检查是否完成
            if (energyUsed >= recipe.energy) {
                // 输出产物
                if (inventory[1].isEmpty) {
                    inventory[1] = recipe.output.copy()
                } else {
                    inventory[1].increment(recipe.output.count)
                }
                
                // 消耗输入
                inputStack.decrement(1)
                if (inputStack.isEmpty) {
                    inventory[0] = ItemStack.EMPTY
                }
                
                // 重置
                currentRecipe = null
                energyUsed = 0
            }
            
            markDirty()
        }
    }
    
    private fun canOutput(output: ItemStack, currentOutput: ItemStack): Boolean {
        if (currentOutput.isEmpty) return true
        if (!ItemStack.canCombine(currentOutput, output)) return false
        return currentOutput.count + output.count <= currentOutput.maxCount
    }
    
    fun getProgress(): Float {
        val recipe = currentRecipe ?: return 0f
        return energyUsed.toFloat() / recipe.energy.toFloat()
    }
    
    fun getRequiredEnergy(): Long = currentRecipe?.energy ?: 0
    
    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        Inventories.readNbt(nbt, inventory)
        energyStorage.amount = nbt.getLong("energy")
        energyUsed = nbt.getLong("energyUsed")
    }
    
    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        Inventories.writeNbt(nbt, inventory)
        nbt.putLong("energy", energyStorage.amount)
        nbt.putLong("energyUsed", energyUsed)
    }
}
