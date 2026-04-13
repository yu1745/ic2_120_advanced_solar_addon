package ic2_120_advanced_solar_addon.content.block

import net.minecraft.block.AbstractBlock
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.BlockWithEntity
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class MolecularTransformerBlock : BlockWithEntity(AbstractBlock.Settings.create().strength(3.0f, 15.0f).luminance { 12 }.requiresTool()) {
    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = MolecularTransformerBlockEntity(pos, state)
    
    override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.MODEL
    
    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (!world.isClient) BlockEntityTicker { w, pos, s, be ->
            (be as? MolecularTransformerBlockEntity)?.tick()
        } else null
    }
}
