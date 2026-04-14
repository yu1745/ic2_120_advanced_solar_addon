package ic2_120_advanced_solar_addon.content.block

import ic2_120_advanced_solar_addon.content.tab.SolarMachinesTab
import ic2_120.content.block.MachineBlock
import net.minecraft.block.BlockRenderType
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityTicker
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import stardust.fabric.registry.annotation.ModBlock
import stardust.fabric.registry.type

@ModBlock(name = "molecular_transformer", registerItem = true, tab = SolarMachinesTab::class, group = "machine")
class MolecularTransformerBlock : MachineBlock() {

    companion object {
        const val INPUT_SLOT = 0
        const val OUTPUT_SLOT = 1
        const val INVENTORY_SIZE = 2
        val ACTIVE: BooleanProperty = BooleanProperty.of("active")
    }

    override fun createBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
        MolecularTransformerBlockEntity(pos, state)

    override fun <T : BlockEntity> getTicker(
        world: World,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? =
        if (world.isClient) null
        else checkType(type, MolecularTransformerBlockEntity::class.type()) { w, p, s, be -> (be as MolecularTransformerBlockEntity).tick(w, p, s) }

    override fun createScreenHandlerFactory(state: BlockState, world: World, pos: BlockPos): NamedScreenHandlerFactory? {
        val be = world.getBlockEntity(pos)
        return be as? NamedScreenHandlerFactory
    }

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        hit: BlockHitResult
    ): ActionResult {
        if (!world.isClient) {
            createScreenHandlerFactory(state, world, pos)?.let { factory ->
                player.openHandledScreen(factory)
            }
        }
        return ActionResult.SUCCESS
    }

    override fun appendProperties(builder: StateManager.Builder<net.minecraft.block.Block, BlockState>) {
        super.appendProperties(builder)
        builder.add(ACTIVE)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? =
        super.getPlacementState(ctx)?.with(ACTIVE, false)

    override fun getRenderType(state: BlockState): BlockRenderType = BlockRenderType.INVISIBLE
}
