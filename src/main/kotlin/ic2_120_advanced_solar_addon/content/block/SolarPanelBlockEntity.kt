package ic2_120_advanced_solar_addon.content.block

import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World
import team.reborn.energy.api.EnergyStorage
import team.reborn.energy.api.base.SimpleEnergyStorage

enum class GenerationState {
    NONE, NIGHT, DAY
}

abstract class SolarPanelBlockEntity(
    type: BlockEntityType<out SolarPanelBlockEntity>,
    pos: BlockPos,
    state: BlockState,
    val dayPower: Int,
    val nightPower: Int,
    maxStorage: Long,
    val tier: Int
) : BlockEntity(type, pos, state), EnergyStorage {
    
    val energyStorage = object : SimpleEnergyStorage(maxStorage, 0, Long.MAX_VALUE) {
        override fun onFinalCommit() {
            markDirty()
        }
    }
    
    var generationState: GenerationState = GenerationState.NONE
    private var ticker: Int = 0
    private val tickRate: Int = 128
    
    override fun insert(maxAmount: Long, transaction: net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext?): Long {
        return 0 // 太阳能板不接受能量输入
    }
    
    override fun extract(maxAmount: Long, transaction: net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext?): Long {
        return energyStorage.extract(maxAmount, transaction)
    }
    
    override fun getAmount(): Long = energyStorage.amount
    override fun getCapacity(): Long = energyStorage.capacity
    
    fun tick() {
        if (world?.isClient == true) return
        
        if (ticker++ % tickRate == 0) {
            checkSky()
        }
        
        // 发电
        when (generationState) {
            GenerationState.DAY -> tryGenerateEnergy(dayPower)
            GenerationState.NIGHT -> tryGenerateEnergy(nightPower)
            GenerationState.NONE -> {}
        }
        
        // 输出能量到相邻的能量接收器
        outputEnergy()
    }
    
    private fun checkSky() {
        val world = this.world ?: return
        val pos = this.pos
        
        if (!world.isSkyVisible(pos.up())) {
            generationState = GenerationState.NONE
            return
        }
        
        val isDay = world.isDay
        val isRaining = world.isRaining || world.isThundering
        val canRain = world.getBiome(pos).value().hasPrecipitation()
        
        generationState = when {
            isDay && (!canRain || !isRaining) -> GenerationState.DAY
            !isDay -> GenerationState.NIGHT
            else -> GenerationState.NONE
        }
        
        markDirty()
    }
    
    private fun tryGenerateEnergy(amount: Int) {
        val inserted = energyStorage.amount + amount
        energyStorage.amount = minOf(inserted, energyStorage.capacity)
        markDirty()
    }
    
    private fun outputEnergy() {
        if (energyStorage.amount <= 0) return
        
        val world = this.world ?: return
        
        // 向四周输出能量
        for (direction in Direction.values()) {
            if (energyStorage.amount <= 0) break
            
            val neighborPos = pos.offset(direction)
            val targetStorage = EnergyStorage.SIDED.find(world, neighborPos, direction.opposite)
            if (targetStorage != null && targetStorage.supportsInsertion()) {
                val maxOutput = minOf(energyStorage.amount, getTierPower())
                val extracted = energyStorage.extract(maxOutput, null)
                val remainder = targetStorage.insert(extracted, null)
                energyStorage.amount += remainder // 退回未插入的部分
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
            else -> 32L
        }
    }
    
    override fun readNbt(nbt: NbtCompound) {
        super.readNbt(nbt)
        energyStorage.amount = nbt.getLong("energy")
        generationState = GenerationState.values()[nbt.getInt("state")]
    }
    
    override fun writeNbt(nbt: NbtCompound) {
        super.writeNbt(nbt)
        nbt.putLong("energy", energyStorage.amount)
        nbt.putInt("state", generationState.ordinal)
    }
}
