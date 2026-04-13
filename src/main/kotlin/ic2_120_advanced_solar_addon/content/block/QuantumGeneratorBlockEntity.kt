package ic2_120_advanced_solar_addon.content.block

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import team.reborn.energy.api.EnergyStorage

class QuantumGeneratorBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(ModBlocks.QUANTUM_GENERATOR_ENTITY, pos, state), EnergyStorage {
    
    var production: Int = 512
    var tier: Int = 3
    var isActive: Boolean = true
    
    // 量子发电机不存储能量，直接输出
    override fun insert(maxAmount: Long, transaction: net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext?): Long = 0
    override fun extract(maxAmount: Long, transaction: net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext?): Long {
        return if (isActive) minOf(maxAmount, getTierPower()) else 0
    }
    override fun getAmount(): Long = 0
    override fun getCapacity(): Long = 0
    
    fun tick() {
        if (world?.isClient == true) return
        if (!isActive) return
        
        // 检查红石信号
        val world = this.world ?: return
        var hasRedstoneSignal = false
        for (direction in Direction.values()) {
            if (world.getEmittedRedstonePower(pos.offset(direction), direction) > 0) {
                hasRedstoneSignal = true
                break
            }
        }
        if (hasRedstoneSignal) return
        
        // 输出能量到相邻的方块
        outputEnergy()
    }
    
    private fun outputEnergy() {
        val world = this.world ?: return
        
        var remainingOutput = production.toLong()
        
        for (direction in Direction.values()) {
            if (remainingOutput <= 0) break
            
            val neighborPos = pos.offset(direction)
            val targetStorage = EnergyStorage.SIDED.find(world, neighborPos, direction.opposite)
            if (targetStorage != null && targetStorage.supportsInsertion()) {
                val packetSize = getTierPower()
                val toOutput = minOf(remainingOutput, packetSize)
                val accepted = targetStorage.insert(toOutput, null)
                remainingOutput -= accepted
            }
        }
    }
    
    private fun getTierPower(): Long {
        return when (tier) {
            1 -> 32L
            2 -> 128L
            3 -> 512L
            4 -> 2048L
            5 -> 8192L
            6 -> 32000L
            else -> 512L
        }
    }
    
    fun changeProduction(delta: Int) {
        production = (production + delta).coerceAtLeast(0)
        markDirty()
    }
    
    fun setTierLevel(newTier: Int) {
        tier = newTier.coerceIn(1, 6)
        markDirty()
    }
    
    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        production = nbt.getInt("production")
        tier = nbt.getInt("tier")
        isActive = nbt.getBoolean("active")
    }
    
    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        nbt.putInt("production", production)
        nbt.putInt("tier", tier)
        nbt.putBoolean("active", isActive)
    }
}
