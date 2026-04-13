package ic2_120_advanced_solar_addon.content.block

import net.minecraft.block.AbstractBlock
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class AdvancedSolarPanelBlock : SolarPanelBlock(AbstractBlock.Settings.create().strength(3.0f, 15.0f).requiresTool()) {
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = AdvancedSolarPanelBlockEntity(pos, state)
    
    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (!world.isClient) BlockEntityTicker { w, pos, s, be ->
            (be as? AdvancedSolarPanelBlockEntity)?.tick()
        } else null
    }
}

class AdvancedSolarPanelBlockEntity(pos: BlockPos, state: BlockState) : 
    SolarPanelBlockEntity(
        ModBlocks.ADVANCED_SOLAR_PANEL_ENTITY, 
        pos, state,
        dayPower = 8,
        nightPower = 1,
        maxStorage = 32000,
        tier = 1
    )
