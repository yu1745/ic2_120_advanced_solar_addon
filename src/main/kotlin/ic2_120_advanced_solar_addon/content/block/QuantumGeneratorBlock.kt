package ic2_120_advanced_solar_addon.content.block

import ic2_120_advanced_solar_addon.content.recipe.MTRecipes
import net.minecraft.block.AbstractBlock
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.inventory.Inventories
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.collection.DefaultedList
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import team.reborn.energy.api.EnergyStorage
import team.reborn.energy.api.base.SimpleEnergyStorage

class QuantumGeneratorBlock : BlockWithEntity(AbstractBlock.Settings.create().strength(3.0f, 15.0f).requiresTool()) {
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = QuantumGeneratorBlockEntity(pos, state)
    
    override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.MODEL
    
    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (!world.isClient) BlockEntityTicker { w, pos, s, be ->
            (be as? QuantumGeneratorBlockEntity)?.tick()
        } else null
    }
}
