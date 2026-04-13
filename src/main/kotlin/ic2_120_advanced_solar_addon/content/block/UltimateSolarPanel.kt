package ic2_120_advanced_solar_addon.content.block

import net.minecraft.block.AbstractBlock
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class UltimateSolarPanelBlock : SolarPanelBlock(AbstractBlock.Settings.create().strength(3.0f, 15.0f).requiresTool()) {
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = UltimateSolarPanelBlockEntity(pos, state)
    
    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (!world.isClient) BlockEntityTicker { w, pos, s, be ->
            (be as? UltimateSolarPanelBlockEntity)?.tick()
        } else null
    }
}

class UltimateSolarPanelBlockEntity(pos: BlockPos, state: BlockState) : 
    SolarPanelBlockEntity(
        ModBlocks.ULTIMATE_SOLAR_PANEL_ENTITY, 
        pos, state,
        dayPower = 512,
        nightPower = 64,
        maxStorage = 1000000,
        tier = 3
    )
