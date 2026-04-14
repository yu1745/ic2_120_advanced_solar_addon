package ic2_120_advanced_solar_addon.content

import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.math.Direction
import stardust.fabric.registry.RegistryConfigurer
import team.reborn.energy.api.EnergyStorage
import ic2_120.content.TickLimitedSidedEnergyContainer
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

class RegistryConfigurerImpl : RegistryConfigurer {
    override fun customizeTabEntries(item: Item, itemClass: KClass<*>?, defaultStack: ItemStack): List<ItemStack> {
        return listOf(defaultStack)
    }

    override fun registerEnergyStorage(
        blockEntityType: BlockEntityType<*>,
        blockEntityClass: KClass<*>,
        energyProperty: KProperty1<Any, Any?>
    ) {
        val prop = energyProperty
        EnergyStorage.SIDED.registerForBlockEntity({ be, direction ->
            val container = prop.get(be)
            if (container is TickLimitedSidedEnergyContainer) container.getSideStorage(direction)
            else null
        }, blockEntityType)
    }
}
